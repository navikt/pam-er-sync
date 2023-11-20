FROM navikt/java:17
COPY target/pam-er-sync-*.jar /app/app.jar
EXPOSE 9012

