FROM openjdk:8
ENV SPRING_PROFILES_ACTIVE prod
VOLUME /tmp
EXPOSE 9086
ADD ./target/ms-transactions-0.0.1-SNAPSHOT.jar ms-transactions.jar
ENTRYPOINT ["java","-jar","/ms-transactions.jar"]