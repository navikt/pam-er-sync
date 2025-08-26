FROM eclipse-temurin:21-jre-alpine

COPY target/pam-er-sync-*.jar /app/app.jar
EXPOSE 9012

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

ENTRYPOINT ["java","-jar","/app/app.jar"]
