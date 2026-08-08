# fat jar は CI (gradlew :server:buildFatJar -PbundleWebApp) で事前ビルドする前提。このイメージは jar を載せるだけ。
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/server-all.jar app.jar
# 非 root 実行(多層防御)。UID/GID は数値固定して Cloud Run / k8s の runAsNonRoot 判定を通す
# (--system は UID < 1000 を前提とし 10001 では警告になるため使わない)。
RUN groupadd --gid 10001 app \
    && useradd --uid 10001 --gid 10001 --no-create-home app
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
