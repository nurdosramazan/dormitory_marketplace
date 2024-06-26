#pulling base image
FROM openjdk:17-oracle

#making directory for jar and transfer it
RUN mkdir /opt/app
COPY target/dormitory_marketplace-0.0.1-SNAPSHOT.jar /opt/app/dm.jar
COPY ./start_command.sh /opt/app/start_command.sh
RUN chmod +x /opt/app/start_command.sh
WORKDIR /opt/app

#running jar
CMD ["./start_command.sh"]