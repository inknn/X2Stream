FROM openjdk:11.0.7-jre-slim
ADD X2Stream.jar /opt
ADD resources /opt/resources
CMD ["java", "-jar", "/opt/X2Stream.jar"]