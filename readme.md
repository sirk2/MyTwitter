# MyTwitter
MyTwitter is a simple social networking application, similar to Twitter. It exposes REST web services but does not persist posted tweets which are stored in memory during runtime only.

## Building
The application can be build from provided source files using Apache Maven 3.x.
There must be Java 1.8+ installed in operating system and dependencies listed in pom.xml must be on classpath.

## Installation 
Place file **mytwitter-0.0.1-SNAPSHOT.jar** in any file system folder.

## Running MyTwitter
Launch command
```bash
java -jar /full/path/mytwitter-0.0.1-SNAPSHOT.jar
```
where */full/path/* is the full path to application JAR

## Usage
Requests can be posted from any web services client e.g., Postman (https://www.postman.com/)

## Documentation
After starting MyTwitter, online documentation is available under http://localhost:8080/v2/api-docs (JSON, looks better in Firefox than in Chrome) and http://localhost:8080/swagger-ui.html (more descriptive). Port number is set in **application.properties** file where the value can be changed. 
