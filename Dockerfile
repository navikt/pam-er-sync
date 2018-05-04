FROM navikt/java:8
COPY app/target/pam-er-sync-*.jar /app/app.jar

EXPOSE 9012