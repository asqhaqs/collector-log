```
cd lib/
mvn install:install-file -Dfile=kafka-clients-2.0.5.jar  -DgroupId=org.apache.kafka -DartifactId=kafka-clients -Dversion=2.0.0 -Dpackaging=jar
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>2.0.0</version>
    <!--<scope>system</scope>-->
    <!--<systemPath>${basedir}/lib/kafka-clients-2.0.5.jar</systemPath>-->
</dependency>
```