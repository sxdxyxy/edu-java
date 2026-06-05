# ===========================================================
# safe-edu backend image (eclipse-temurin 是 Debian 系, 用 apt-get)
# ===========================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY lib ./lib
COPY src ./src
COPY .mvn-settings.xml /tmp/mvn-settings.xml
ARG BUILD_PROFILE=prod
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -P"${BUILD_PROFILE}" -DskipTests -e -gs /tmp/mvn-settings.xml clean package

FROM eclipse-temurin:17-jre
ENV TZ=Asia/Shanghai
WORKDIR /app

# Debian 系用 apt-get, 加 curl wget 用于健康检查
RUN apt-get update && apt-get install -y --no-install-recommends \
        tzdata curl wget \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo Asia/Shanghai > /etc/timezone \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/target/safe-edu.jar /app/safe-edu.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8"

EXPOSE 8000

HEALTHCHECK --interval=30s --timeout=5s --retries=10 --start-period=120s \
    CMD wget -qO- http://127.0.0.1:8000/doc.html >/dev/null 2>&1 || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/safe-edu.jar"]
