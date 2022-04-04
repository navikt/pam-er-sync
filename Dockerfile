FROM navikt/java:12
COPY target/pam-er-sync-*.jar /app/app.jar
EXPOSE 9012

