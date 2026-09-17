FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-27@sha256:f726508b3d7e9843fed7331e56a12f5debc891a8503fadaed8d9e6c1e3a53d8d

COPY target/pam-er-sync-*.jar /app.jar

ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb:NO.UTF-8' \
    TZ="Europe/Oslo" \
    JDK_JAVA_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"

EXPOSE 9012

CMD ["-jar", "/app.jar"]
