# LDES Client SDK

This module contains the LDES client SDK that replicates and synchronises an LDES and keeps (non-persisted) state for that process.

Wrappers can call the SDK to do the actual work of scheduling fragment fetching and extracting members.

- [Service](#service)
    - [Instantiating](#instantiating)
    - [Processing](#processing-ldes)
    - [Persistence](#persist-the-state)
- [ModelConverter](#modelconverter)
- [SDK configuration](#sdk-configuration)
    - [Configurable options](#configurable-options)
    - [Accepted RDF formats](#accepted-rdf-formats)


The main goal for the LDES Client SDK is to replicate an LDES and then synchronize it.

This is achieved by configuring the processor with an initial fragment url. When the processor is triggered, the fragment will be processed and all relations will be added to the (non-persisted) queue.

As long as the processor is running, a queue is maintained that accepts new fragments to process.
The processor also keeps track of the mutable and immutable fragments that have already been processed.

When an attempt is made to queue a known immutable fragment, it will be ignored.
Fragments in the mutable fragment store will be queued when they're expired. Should a fragment be processed from a stream that does not set the max-age in the Cache-control header, a default expiration interval will be used to set an expiration date on the fragment.

Processed members of mutable fragments are also kept in state. They are ignored if presented more than once.

## Service

### Instantiating

Call the [LdesClientImplFactory](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientImplFactory.java) to get an instance of the [LdesService](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/services/LdesServiceImpl.java).

```java
LdesClientImplFactory.getService();
```

This call can be made without arguments, as above, in which case values will be taken from [LdesClientDefaults](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientDefaults.java). Arguments are also accepted:

```java
LdesClientImplFactory.getService(Lang dataSourceFormat);
LdesClientImplFactory.getService(Lang dataSourceFormat, Long expirationInterval);
```


Missing or invalid values will be replaced by values from [LdesClientDefaults](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientDefaults.java).

### Processing LDES

Once an instance of the [LdesService](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/services/LdesServiceImpl.java) is obtained, queue the initial fragment and begin processing.

```java
String initialFragmentId = "http://localhost:10101/ldes-test-fragment";
LdesService ldesService = LdesClientImplFactory.getService();

ldesService.queueFragment(initialFragmentId);

while (ldesService.hasFragmentsToProcess) {
	LdesFragment fragment = ldesService.processNextFragment();
	
	...
}
```

This will fetch the initial fragment (most likely after a redirect), process it and follow all relations. The resulting fragment can then be split into members that are ready for ingestion by an LDES server.

### Persist the state

When the SDK is processing fragments, it needs to keep an eye on the state of the processed LDES fragments and members.
The SDK relies on the implementation of an [LdesStateManager](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/state/LdesStateManager.java) for this.

#### Persistence of mutable and immutable fragments and members

The state manager keeps a queue of fragments to process and remembers what fragments and members have already been processed. A distinction is made between immutable fragments and mutable fragments.

When a fragment has been processed that is immutable, it will never again be processed by the SDK, but instead removed from the queue should it be queued again.

When a fragment has been processed that is mutable, it will be stored as such. Based on the fragment expiration date, that is either provided by the data producer or set using the configured expiration interval ([expirationInterval](#configurable-options)), the fragment will be returned for processing when the queue is empty and the fragment's expiration date has been reached.

Processed members are also watched by the state manager to ensure that a member is never processed twice.

Currently the state manager uses a SQLite database to store this state. See [SqlitePersistedLdesStateManager](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/state/SqlitePersistedLdesStateManager.java) for details.

#### Resume

The state persistence enables users of this SDK to pause and resume their LDES processing.

When a processor is stopped and then restarted, the SDK will reload its internal state and resume processing from the last queued fragment, regardless of what fragments are queued during the restart process.


## ModelConverter

### Models to Strings

A [ModelConverter](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/converters/ModelConverter.java) is available to convert the Jena models to Strings.

```java
ModelConverter.convertModelToString(Model model);
ModelConverter.convertModelToString(Model model, Lang dataDestinationFormat);
```

When called without specifying the `dataDestinationFormat`, the value is taken from [LdesClientDefaults](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientDefaults.java).

**Default value**: n-quads

### Strings to Models

Converting a String to a Model is also provided:

```java
ModelConverter.convertStringToModel(String input);
ModelConverter.convertStringToModel(String input, Lang dataSourceFormat);
```

When called without specifying the `dataSourceFormat`, the value is taken from [LdesClientDefaults](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientDefaults.java).

**Default value**: JSONLD11

### SDK configuration


#### Configurable options

- `dataSourceFormat`
  
  The expected RDF input format as recognised by [org.apache.jena.riot.Lang](https://javadoc.io/doc/org.apache.jena/jena-arq/4.6.1/org.apache.jena.arq/org/apache/jena/riot/Lang.html).

  **Default value**: JSONLD11

- `dataDestinationFormat`
  
  The desired RDF output format as recognised by [org.apache.jena.riot.Lang](https://javadoc.io/doc/org.apache.jena/jena-arq/4.6.1/org.apache.jena.arq/org/apache/jena/riot/Lang.html).

  **Default value**: n-quads

- `expirationInterval`

  A number (in seconds) used in setting an expiration date for fragments with an unknown refresh interval.

  **Default value**: 604800


When [LdesClientImplFactory](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientImplFactory.java) is called without these arguments, then default values from [LdesClientDefaults](process/src/src/main/java/be/vlaanderen/informatievlaanderen/ldes/client/LdesClientDefaults.java) will be used.

#### Accepted RDF formats

An indicative, possibly non-exhaustive list of RDF formats recognised by Jena is provided below.
Please refer to the [Jena documentation](https://javadoc.io/doc/org.apache.jena/jena-arq/4.6.1/org.apache.jena.arq/org/apache/jena/riot/Lang.html) of the implemented Jena version for an authoritative list.

Also take into consideration that Jena accepts variants of RDF format names in many cases. It is best practice to use the formal names.

##### - **CSV** ([RFC4180](https://www.rfc-editor.org/rfc/rfc4180.html))
##### - **JSONLD** ([W3C Recommendation 16 July 2020](https://www.w3.org/TR/json-ld11/))
##### - **N3** (treat as [Turtle](#turtle-or-ttl-rdf-1-1-turtle-w3c-recommendation-25-february-2014-https-www-w3-org-tr-turtle))
##### - **NQUADS** or **NQ** ([RDF 1.1 N-Quads: W3C Recommendation 25 February 2014](https://www.w3.org/TR/n-quads/))
##### - **NTRIPLES** or **NT** ([RDF 1.1 N-Triples: W3C Recommendation 25 February 2014](https://www.w3.org/TR/n-triples/))
##### - **RDFJSON** ([RDF 1.1. JSON Alternate Serialization (RDF/JSON): W3C Working Group Note 07 November 2013](https://www.w3.org/TR/rdf-json/), see also [RDF JSON](https://jena.apache.org/documentation/io/rdf-json.html))
##### - **RDFNULL** (The "null" language, a sink that prints nothing, usually very efficiently)
##### - **RDFTHRIFT** ([RDF Binary using Apache Thrift](https://jena.apache.org/documentation/io/rdf-binary.html))
##### - **RDFXML** ([RDF/XML Syntax Specification (Revised): W3C Recommendation 10 February 2004](https://www.w3.org/TR/REC-rdf-syntax/))
##### - **TRIG** ([RDF 1.1. Trig: W3C Recommendation 25 February 2014](https://www.w3.org/TR/trig/))
##### - **TRIX** ([Trix](https://www.w3.org/2004/03/trix/), see also [Trix support in Apache Jena](https://jena.apache.org/documentation/io/trix.html))
##### - **TSV** ([IANA.org](https://www.iana.org/assignments/media-types/text/tab-separated-values))
##### - **TURTLE** or **TTL** ([RDF 1.1. Turtle: W3C Recommendation 25 February 2014](https://www.w3.org/TR/turtle/))
