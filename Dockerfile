FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-27@sha256:af529f1b0f8b1e046858318418edc7daa81355f5351c2863adde304d345ee150

COPY target/pam-er-sync-*.jar /app.jar

ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb:NO.UTF-8' \
    TZ="Europe/Oslo" \
    JDK_JAVA_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 9012

CMD ["-jar", "/app.jar"]
