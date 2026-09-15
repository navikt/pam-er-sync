FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-26@sha256:5a81440744a6c5860256c3ddf85bac3e42c88f291d17d7d0e492112883c0a8bb

COPY target/pam-er-sync-*.jar /app.jar

ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb:NO.UTF-8' \
    TZ="Europe/Oslo" \
    JDK_JAVA_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 9012

CMD ["-jar", "/app.jar"]
