FROM openjdk:16
USER freddy:1000:1000

RUN mkdir /src /gradle

COPY ./src /src
COPY ./gradle /gradle
COPY ./gradlew /gradlew
COPY ./settings.gradle /settings.gradle

RUN gradlew shadowJar;

RUN java -jar /build/libs/GradleBot-1.0-SNAPSHOT-all.jar
