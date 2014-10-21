# Datavis system testing project

## Description

Mavenized scala project using Gatling(http://gatling.io/)

## Running tests

Gatling attaches to the tests maven phase. Currently, if more than one simulatio class has to be run, new executions have to be defined at pom.xml, as:

    <execution>
        <id>setup</id>
        <phase>test</phase>
        <goals>
            <goal>execute</goal>
        </goals>
        <configuration>
            <dataFolder>./</dataFolder>
            <noReports>true</noReports>
            <simulationClass>com.stratio.tests.PrePT</simulationClass>
        </configuration>
    </execution>
