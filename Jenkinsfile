@Library('deploy')
import deploy
import groovy.json.JsonOutput

def deployLib = new deploy() as Object

node {
    def app = "pam-er-sync"
    def committer, committerEmail, changelog, releaseVersion
    def mvnHome = tool "maven-3.3.9"
    def repo = "navikt"
    def mvn = "${mvnHome}/bin/mvn" as Object
    def environment = "${env.DEPLOY_ENV}" as Object
    def appConfig = "nais.yaml"
    def dockerRepo = "repo.adeo.no:5443"
    def zone = 'sbs'
    def groupId = "nais"
    def namespace = "${env.NAMESPACE}" as Object
    def production = true
    def color

    stage("Initialization") {
        cleanWs()
        withCredentials([string(credentialsId: 'navikt-ci-oauthtoken', variable: 'token')]) {
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                sh(script: "git clone https://${token}:x-oauth-basic@github.com/${repo}/${app}.git .")
            }
        }
        commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        commitUrl = "https://github.com/${repo}/${app}/commit/${commitHash}"
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)

        notifyGithub(repo, app, 'continuous-integration/jenkins', commitHash, 'pending', "Build #${env.BUILD_NUMBER} has started" as Object)
        pom = readMavenPom file: 'pom.xml'
        releaseVersion = pom.version.tokenize("-").getAt(0) + ".${env.BUILD_NUMBER}-${commitHashShort}"
    }

    stage("Build & publish") {
        sh "${mvn} versions:set -B -DnewVersion=${releaseVersion}"
        sh "${mvn} clean install -B -e"
        sh "docker build --build-arg version=${releaseVersion} --build-arg app_name=${app} -t ${dockerRepo}/${app}:${releaseVersion} ."

        withCredentials([usernamePassword(credentialsId: 'nexusUploader', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            sh "docker login -u ${env.USERNAME} -p ${env.PASSWORD} ${dockerRepo} && docker push ${dockerRepo}/${app}:${releaseVersion}"
            sh "curl --fail -v -u ${env.USERNAME}:${env.PASSWORD} --upload-file ${appConfig} https://repo.adeo.no/repository/raw/${groupId}/${app}/${releaseVersion}/nais.yaml"
        }

        sh "${mvn} versions:revert"
        notifyGithub(repo, app, 'continuous-integration/jenkins', commitHash, 'success', "Build #${env.BUILD_NUMBER} has finished" as Object)

    }

    stage("Deploy to TEST") {
        callback = "${env.BUILD_URL}input/Deploy/"

        def deploy = deployLib.deployNaisApp(app, releaseVersion, environment, zone, namespace, callback, committer).key

        try {
            timeout(time: 15, unit: 'MINUTES') {
                input id: 'deploy', message: "Check status here:  https://jira.adeo.no/browse/${deploy}"
            }

            color = 'good'
            GString message = "${app} version ${releaseVersion} has been deployed to pre-prod."
            slackSend color: color, channel: '#pam_bygg', message: message, teamDomain: 'nav-it', tokenCredentialId: 'pam-slack'

        } catch (Exception e) {
            color = 'warning'
            GString message = "Build ${releaseVersion} of ${app} could not be deployed to pre-prod"
            slackSend color: color, channel: '#pam_bygg', message: message, teamDomain: 'nav-it', tokenCredentialId: 'pam-slack'

            throw new Exception("Deploy feilet :( \n Se https://jira.adeo.no/browse/" + deploy + " for detaljer", e)
        }
        try {
            timeout(time: 1, unit: 'MINUTES') {
                input id: 'prod', message: "Deploy to prod?"
            }
        }
        catch (Exception ignored) {
            echo "Cancel production deployment"
            production = false
            currentBuild.result = 'SUCCESS'
        }
    }

    if (production) {
        stage("PRODUCTION") {
            echo "Deploying ${releaseVersion} to production"
            callback = "${env.BUILD_URL}input/Deploy/"
            def deploy = deployLib.deployNaisApp(app, releaseVersion, 'p', zone, namespace, callback, committer, false).key
            try {
                timeout(time: 15, unit: 'MINUTES') {
                    input id: 'deploy', message: "Check status here:  https://jira.adeo.no/browse/${deploy}"
                }
                color = 'good'
                GString message = "${app} version ${releaseVersion} has been deployed to production."
                slackSend color: color, channel: '#pam_bygg', message: message, teamDomain: 'nav-it', tokenCredentialId: 'pam-slack'
            }
            catch (Exception e) {
                color = 'danger'
                GString message = "Build ${releaseVersion} of ${app} could not be deployed to production"
                slackSend color: color, channel: '#pam_bygg', message: message, teamDomain: 'nav-it', tokenCredentialId: 'pam-slack'
                throw new Exception("Deploy feilet :( \n Se https://jira.adeo.no/browse/" + deploy + " for detaljer", e)
            }
        }
        stage("Tag and bump version") {
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                withCredentials([string(credentialsId: 'navikt-ci-oauthtoken', variable: 'token')]) {
                    sh("git tag -a ${releaseVersion} -m ${releaseVersion}")
                    sh("git push https://${token}:x-oauth-basic@github.com/${repo}/${app}.git --tags")
                }
            }
            sh "${mvn} build-helper:parse-version versions:set -B -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.nextMinorVersion}-SNAPSHOT -DgenerateBackupPoms=false"
            pom = readMavenPom file: 'pom.xml'
            newDevVersion = pom.version
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                withCredentials([string(credentialsId: 'navikt-ci-oauthtoken', variable: 'token')]) {
                    sh "git commit -am \"updated to ${newDevVersion} after deployed to production by ${committer}\""
                    sh "git push  origin master"
                }
            }
        }
    }
}

def notifyGithub(owner, app, context, sha, state, description) {
    def postBody = [
            state      : "${state}",
            context    : "${context}",
            description: "${description}",
            target_url : "${env.BUILD_URL}"
    ]
    def postBodyString = JsonOutput.toJson(postBody)

    withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
        withCredentials([string(credentialsId: 'navikt-ci-oauthtoken', variable: 'token')]) {
            sh """
                curl -H 'Authorization: token ${token}' \
                    -H 'Content-Type: application/json' \
                    -X POST \
                    -d '${postBodyString}' \
                    'https://api.github.com/repos/${owner}/${app}/statuses/${sha}'
            """
        }
    }
}