# syntax=docker/dockerfile:1

FROM ubuntu:20.04
WORKDIR /app

RUN apt update
RUN DEBIAN_FRONTEND=noninteractive TZ=Etc/UTC apt -y install tzdata
RUN apt install -y git
RUN apt install -y openjdk-16-jdk

RUN git clone https://github.com/ashelkovnykov/offline-wallet-generator.git
RUN mkdir output

RUN pwd

WORKDIR ./offline-wallet-generator/
RUN ./gradlew.sh clean build

ENTRYPOINT ["./bin/release.sh", "-o", "../output/"]
CMD ["--help"]

