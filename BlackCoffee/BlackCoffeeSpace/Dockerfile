#importo un immagine docker per avere la base
FROM amazoncorretto:21-alpine

#da usare per non fornire la vera route all'utente
RUN addgroup -S spring && adduser -S spring -G spring

#creo la directory prima di spostare lo user a spring
ENV LOG_DIR=log
RUN mkdir ${LOG_DIR}
RUN chown spring:spring ${LOG_DIR}

USER spring:spring

ENV SPRING_PROFILES_ACTIVE=dist
ARG DEPENDENCY=expand

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 80
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "unito.taass.blackcoffeespace.BlackCoffeeSpaceApplication"]