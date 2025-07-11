FROM eclipse-temurin:20-jdk

WORKDIR /app

COPY ./ .

RUN ./gradlew build

RUN useradd --system --uid 1001 appuser

RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app-0.0.1-SNAPSHOT.jar"]