<h1>Wallethub Log Parser</h1>
This project will parse the given log file and load the data into a mysql database per the requirement doc. The compiled
jar file is located in the target directory and the SQL queries and schema are located in the src/main/resources/ directory.

<h2>Technologies</h2>

Java 11

Maven

Spring Batch

Spring Data JPA

<h2>Configuration</h2>

All configuration options can be set either on the commandline or in a config file (application.yml/application.properties).
There are two example application.yml files. One in the src/main/resources/ and one in the src/test/resources directory.

<h2>Usage</h2>

java -jar target/wallethub-log-parser-1.0.jar --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200 --accesslog=src/main/resources/access.log

java -jar target/wallethub-log-parser-1.0.jar --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500 --accesslog=src/main/resources/access.log
