PAM Enhetsregister Sync

Spring batch app som laster ned datasettet fra BRREG og indekserer til en lokal OpenSearch instance.


# OpenSearch on localhost Docker
The repository ships with a `compose.yml` that starts OpenSearch 3, OpenSearch Dashboards and the app:
```
> docker compose up -d
```
OpenSearch is then available on http://localhost:9200 and Dashboards on http://localhost:5601.
The app reads `OPEN_SEARCH_URI`, `OPEN_SEARCH_USERNAME` and `OPEN_SEARCH_PASSWORD`, which
`compose.yml` already sets. To sync and verify in one go, run `./local-sync-test.sh`.

To run only OpenSearch and point a locally started app at it:
1. Start the container:
    ```
    > docker compose up -d opensearch
    ```
2. Change your configuration to use this instance:
    ```
    opensearch:
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
* ``opensearch.url`` (http://localhost:9200, from ``OPEN_SEARCH_URI``)  
The OpenSearch instance to index into. Credentials are set with ``opensearch.user`` and ``opensearch.password`` (``OPEN_SEARCH_USERNAME`` / ``OPEN_SEARCH_PASSWORD``).
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
