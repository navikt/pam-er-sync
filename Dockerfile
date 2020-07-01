FROM navikt/java:12
COPY target/pam-er-sync-*.jar /app/app.jar
ENV JAVA_OPTS="-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3 -Djavax.net.debug=ssl:handshake:verbose:keymanager:trustmanager"
EXPOSE 9012

ENTRYPOINT ["java", "-jar ${APP_JAR}"]
