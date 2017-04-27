## ---
## build

FROM maven:3.3.3-jdk-8
WORKDIR /work
ADD / /work
RUN mvn package


## ---
## production image

FROM java:8-jdk

ENV HOME /home/app
RUN groupadd -g 10000 app && \
    useradd -u 10000 -g 10000 -c "App user" -d $HOME -m app

EXPOSE 8080

WORKDIR $HOME
ENTRYPOINT java -jar app.jar
VOLUME ["$HOME/config"]

USER app
COPY --from=0 /work/target/app.jar $HOME