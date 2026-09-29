FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-27@sha256:e5c27cf7c47fbed5202adc198805cb9777d9889b6846f1c0fc84a1c0883a434c

COPY target/pam-er-sync-*.jar /app.jar

ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb:NO.UTF-8' \
    TZ="Europe/Oslo" \
    JDK_JAVA_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 9012

CMD ["-jar", "/app.jar"]
