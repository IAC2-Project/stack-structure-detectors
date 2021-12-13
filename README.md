# How to use the tools
The tools can be downloaded from the [releases](https://github.com/IAC2-Project/stack-structure-detectors/releases) page.

## Prerequisits
  - Java 8

## CLI Commands

Run the monolithic stack detector
```
$ java -jar Detector.jar monolith
-e,--min_edges <arg>      Minimal number of edges to be a monolithic stack (exclusinve).
-h,--help                 Show the help message
-i,--input <arg>          Filename of the EDMM yml with the deployment model.
-o,--output <arg>         Output filename for detector results
-v,--min_vertices <arg>   Minimal number of vertices to be a monolithic stack (exclusive).
```
or run the service/micro stack detector
```
$ java -jar Detector.jar [detectorName]
-h,--help                   Show the help message
-i,--input <arg>            Filename of the EDMM yml with the deployment model.
-o,--output <arg>           Output filename for detector results
-s,--service_finder <arg>   Selection of the service finder [business_logic, property].
```

Use the ServiceFinder application to find the services from the business logic and store the result in the
output edmm yml file if you want to modify the services used by the detector.
```
$ java -jar ServiceFinder.jar
-h,--help           Show the help message
-i,--input <arg>    Filename of the EDMM yml with the deployment model.
-o,--output <arg>   Output filename for detector results
```

# How to build the project

This project uses Maven.
You can package the project with the commang `mvn package`.
This will create two jar files with their dependencies embedded in them; one for the `Detector` tool
and the other for the `ServiceFinder` tool.