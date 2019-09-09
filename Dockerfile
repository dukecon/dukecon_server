FROM openjdk:8-jdk-slim
ENV PORT 8080
EXPOSE 8080

ENV JAVA_DEFAULT_OPTS "-Xms768M -Xmx1536M"

COPY impl/target/*.jar /opt/dukecon/dukecon.jar
WORKDIR /opt/dukecon
CMD java \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseCGroupMemoryLimitForHeap \
    -Djava.security.egd=file:/dev/./urandom \
    ${JAVA_DEFAULT_OPTS} \
    ${JAVA_OPTS} \
    -jar dukecon.jar \
    ${DUKECON_ARGS}
