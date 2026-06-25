FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25@sha256:2c799fa168bfa527884e8ae5a14ad3d46a6919d37185ca2466eead6eb1e76d1e

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY target/pam-er-sync-*.jar /app.jar
EXPOSE 9012
ENTRYPOINT ["java", "-jar", "/app.jar"]
