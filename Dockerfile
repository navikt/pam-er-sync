FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-26@sha256:9b76e51fe513a1e053ea863e4ff9d267ffd0edd9cc91f4fcf2f4cbfaf527699d

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY target/pam-er-sync-*.jar /app.jar
EXPOSE 9012
ENTRYPOINT ["java", "-jar", "/app.jar"]
