FROM gradle:jdk17-jammy AS gradle-builder
COPY . ./
RUN gradle clean app:build -q -x test


FROM eclipse-temurin:17.0.8.1_1-jre-jammy AS loyer-builder
WORKDIR app/build/
COPY --from=gradle-builder /home/gradle/app/build/libs/chatgpt-kt-bot.jar ./chatgpt-kt-bot.jar
RUN ["mkdir", "dependencies", "snapshot-dependencies", "spring-boot-loader", "application"]
RUN java -Djarmode=layertools -jar chatgpt-kt-bot.jar extract


FROM eclipse-temurin:17.0.8.1_1-jre-jammy
ENV APP_HOME=/opt/chatgpt-kt-bot
WORKDIR ${APP_HOME}

COPY ./bin/ ./bin/
COPY --from=loyer-builder /app/build/dependencies/ ./
COPY --from=loyer-builder /app/build/snapshot-dependencies/ ./
COPY --from=loyer-builder /app/build/spring-boot-loader/ ./
COPY --from=loyer-builder /app/build/application/ ./

EXPOSE 10003

RUN ["chmod", "+x", "./bin/run.sh"]
ENTRYPOINT ["./bin/run.sh"]