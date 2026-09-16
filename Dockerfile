FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-26@sha256:ae80aad8b17c5d0dcf80bb1870642d66de610fac266810ae720d3bc853144db0

COPY target/pam-er-sync-*.jar /app.jar

ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb:NO.UTF-8' \
    TZ="Europe/Oslo" \
    JDK_JAVA_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 9012

CMD ["-jar", "/app.jar"]
