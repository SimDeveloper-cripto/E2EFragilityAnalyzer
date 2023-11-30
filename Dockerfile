
# This Dockerfile is used to run JTrac docker container

FROM openjdk:11
COPY ./JTracVersions/2.2.0 .
RUN rm -rf logs/ && rm -rf data/
ENTRYPOINT ["sh","/start.sh"]