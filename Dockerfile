FROM navikt/java:8
COPY target/pam-er-sync-*.jar app.jar

EXPOSE 9012