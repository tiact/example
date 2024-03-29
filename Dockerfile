FROM java

MAINTAINER TIA <f.n.k.kana@gmail.com>

#EXPOSE 8081

ADD target/tia-websocket-0.0.1-SNAPSHOT.jar chat.jar

# Run the jar file 
ENTRYPOINT ["java","-jar","/chat.jar"]

