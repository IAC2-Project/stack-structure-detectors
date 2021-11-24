# How to get started

Download edmm (Tested with Version 1.0.19) from https://github.com/UST-EDMM/edmm/releases \
Install edmm with `./mvnw clean install` \
If tests fail add `-DskipTests` \
Now you can use edmm-core in the library.

# How to use the tool
Run the monolithic stack detector
```
$ java Detector monolith
-e,--min_edges <arg>      Minimal number of edges to be a monolithic stack.
-h,--help                 Show the help message
-i,--input <arg>          Filename of the EDMM yml with the deployment model.
-o,--output <arg>         Output filename for detector results
-v,--min_vertices <arg>   Minimal number of vertices to be a monolithic stack.
```
or run the service/micro stack detector
```
$ java Detector [detectorName]
-h,--help                   Show the help message
-i,--input <arg>            Filename of the EDMM yml with the deployment model.
-o,--output <arg>           Output filename for detector results
-s,--service_finder <arg>   Selection of the service finder [business_logic, property].
```

Use the ServiceFinder application to find the services from the business logic and store the result in the
output edmm yml file if you want to modify the services used by the detector.
```
$ java ServiceFinder
-h,--help           Show the help message
-i,--input <arg>    Filename of the EDMM yml with the deployment model.
-o,--output <arg>   Output filename for detector results
```
