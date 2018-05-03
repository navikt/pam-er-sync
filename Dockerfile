FROM navikt/java:8
COPY app/target/pam-er-sync-*.jar app.jar

EXPOSE 9012