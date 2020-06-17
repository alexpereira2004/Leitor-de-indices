FROM openjdk:8-jre-alpine3.9
LABEL maintainer "Alex L Pereira <alexpereira2004@gmail.com>"

RUN mkdir /app

WORKDIR /app

COPY ./target/leitor-de-indices-0.0.1-SNAPSHOT.jar ./

CMD ["java", "-jar", "leitor-de-indices-0.0.1-SNAPSHOT.jar"]