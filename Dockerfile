FROM openjdk:21

COPY .  .

RUN sh gradlew shadowJar;

#Entrypoint runs when container actually starts!!!
ENTRYPOINT ["java", "-jar", "/build/libs/Freddy-2.0.0-all.jar"]
