FROM openjdk:11-jre-slim
COPY @project.artifactId@-@project.version@.jar @project.artifactId@.jar
ENTRYPOINT ["java","-Xmx1g","-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n","-jar","@project.artifactId@.jar"]