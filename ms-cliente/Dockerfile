FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
RUN set -eux; \
        group_name="$(getent group 1000 | cut -d: -f1 || true)"; \
        if [ -z "$group_name" ]; then \
            groupadd --system --gid 1000 appgroup; \
            group_name="appgroup"; \
        fi; \
        user_name="$(getent passwd 1000 | cut -d: -f1 || true)"; \
        if [ -z "$user_name" ]; then \
            useradd --system --uid 1000 --gid "$group_name" appuser; \
            user_name="appuser"; \
        fi; \
        chown -R "$user_name:$group_name" /app
USER 1000
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]