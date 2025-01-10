FROM amazoncorretto:23-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/chat-0.0.1.jar chat.jar
ENTRYPOINT ["java", "-jar", "/chat.jar"]