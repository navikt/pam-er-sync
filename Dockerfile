FROM navikt/java:8
COPY target/pam-er-sync-*.jar /app/app.jar

EXPOSE 9012