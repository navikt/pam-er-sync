FROM ghcr.io/navikt/baseimages/temurin:21
COPY target/pam-er-sync-*.jar /app/app.jar
EXPOSE 9012

