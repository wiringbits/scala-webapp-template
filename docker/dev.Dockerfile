# Dev image shared by the backend and web services. Has JDK + sbt + Node; the source is baked in and
# pre-compiled so `sbt server/run` and `sbt dev-web` start quickly. No nginx, no production bundle.
FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y --no-install-recommends nodejs && \
    npm install -g yarn && \
    rm -rf /var/lib/apt/lists/*
RUN curl -fsSL https://github.com/sbt/sbt/releases/download/v1.12.11/sbt-1.12.11.tgz | tar xz -C /opt
ENV PATH="/opt/sbt/bin:${PATH}"
# webpack needs the legacy OpenSSL provider on Node 17+.
ENV NODE_OPTIONS=--openssl-legacy-provider
WORKDIR /src
COPY . .
# Pre-resolve dependencies and compile so the first `docker compose up` doesn't do a cold build in
# both containers at once. (The Scala.js link + webpack install still happen on the first dev-web run.)
RUN sbt -J-Xmx4g --batch "server/compile" "web/compile"
EXPOSE 9000 8080
