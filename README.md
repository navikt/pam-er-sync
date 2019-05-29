PAM Enhetsregister Sync

Laster ned datasettet fra BRREG og indekserer til lokal en Elastic Search instance.

TODO:

# Elasticsearch on localhost Docker
If you're able to run Docker, you could run the application against your own Elasticsearch instance.
1. Create the container, as described [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html) (check version yourself, :latest didn't work):
    ```
    > docker run -d --name elastic -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.2.4
    ```
2. Change your configuration to use this instance:
    ```
    pam:
      elasticsearch:
        url: http://localhost:9200
    ```

# Configuration for NAIS
* ``Dockerfile``  
Copied from *pam_ad*, modified to ``COPY app/target/pam-er-sync-*.jar /app/app.jar`` and ``EXPOSE 9012``.
* ``Jenkinsfile``
Copied from *pam_ad*, modified to ``def app = "pam-er-sync"``.
* ``nais.yaml``  
Defines the NAIS application. Note that we only request one replica, and use the default ``/actuator/info`` as the readiness endpoint.
Only one Fasit resource defined, type ApplicationProperties, so the resource name isn't referenced elsewhere.
* ``src/main/resources/application.yml``  
See section below.
* ``src/main/resources/logback.xml``  
Copied from *pam_ad*, a generic Logback file using a Logstash encoder. Rename this file temporarily if you want a more human-readable output during development and testing.

# Application configuration
We import some environment variables through an ApplicationProperties resource in Fasit. The following properties are expected, with defaults in parantheses:
* ``server.port`` (9012)  
Assigned port to this service.
* ``pam.http.proxy.url`` (http://155.55.60.117:8088)  
This will need to be changed in configuration before deployment, as it is only suitable for use during development.
* ``pam.elasticsearch.url`` (https://pamsok-elasticsearch.nais.oera-q.local)  
Defaults to a test instance of Elasticsearch. Modify as needed by deployment, or set to your local Elasticsearch (see above).
* ``pam.enhetsregister.scheduler.enabled`` (false)  
If *true*, the scheduled synchronization of all configured sources (see below) will trigger according to the cron value.
* ``pam.enhetsregister.scheduler.cron`` (0 0 0 * * *)  
The cron value, Spring style (including leading seconds).
* ``pam.enhetsregister.sources.timeout.millis`` (25000)  
The delay, in millis, before timeout when downloading the CSV file from one of the sources defined below.
* ``pam.enhetsregister.sources.hovedenhet.enabled`` (false)  
If *true*, the Hovedenhet source will be synchronized, and may be manually triggered using the appropriate endpoint. This endpoint will answer with 404 if *false*.
* ``pam.enhetsregister.sources.hovedenhet.url`` (https://data.brreg.no/enhetsregisteret/api/enheter/lastned)  
The URL for downloading the Hovedenhet JSON file.
* ``pam.enhetsregister.sources.underenhet.enabled`` (true)  
If *true*, the Underenhet source will be synchronized, and may be manually triggered using the appropriate endpoint. This endpoint will answer with 404 if *false*.
* ``pam.enhetsregister.sources.underenhet.url`` (https://data.brreg.no/enhetsregisteret/api/underenheter/lastned)  
The URL for downloading the Underenhet JSON file.