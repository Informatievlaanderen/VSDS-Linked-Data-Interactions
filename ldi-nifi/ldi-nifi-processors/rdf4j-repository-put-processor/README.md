# RDF4J repository sink
## About
An Apache NiFi processor that materialises an LDES stream into a triplestore.
Any triplestore that supports the RDF4J remote repository API can be used.

## Build
This NiFi plugin can be build with Maven (needs maven installed):
```
mvn package
```
The NAR file is copied to the 'nifi-extentions' folder, in the root of the repository.

## Install
Either copy the NAR file to the NiFi extentions folder or bind-mount the 'nifi-extentions' folder in docker-compose.yml.

## Configuration
Add the processor to the NiFi workflow and configure it. The processor offers 2 parameters:

 - Predicate used for isVersionOf: The predicate that the LDES stream consumed by the client uses to indicate the version property (ldes:versionOfPath).
 - Restrict output to members: If set to true, statements about entities that are not part of the LDES member will be removed from the stream. In case of doubt leave this disabled.

