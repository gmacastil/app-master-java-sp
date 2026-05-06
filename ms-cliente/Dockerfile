FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
RUN groupadd --system --gid 1000 appgroup && useradd --system --uid 1000 --gid appgroup appuser \
    && chown -R appuser:appgroup /app
USER 1000
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]