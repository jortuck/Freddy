FROM openjdk:16

COPY /bot   /

RUN sh gradlew shadowJar;

#Entrypoint runs when container actually starts!!!
ENTRYPOINT ["/start.sh"]
