FROM navikt/java:12
COPY target/pam-er-sync-*.jar /app/app.jar
ENV JAVA_OPTS="-Djdk.tls.client.protocols=TLSv1.2"
EXPOSE 9012
