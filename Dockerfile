FROM java:8
WORKDIR /
ADD build/libs/rest-feed.jar rest-feed.jar
EXPOSE 8080
CMD java -jar rest-feed.jar