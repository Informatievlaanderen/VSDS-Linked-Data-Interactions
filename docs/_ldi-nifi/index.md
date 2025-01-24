---
title: Linked Data Interactions For Apache NiFi
layout: home
nav_order: 0
---

# Linked Data Interactions For Apache NiFi

[Apache Nifi] is an easy to use, powerful, and reliable system to process and distribute data.

## Set up NiFi instance with LDI processors

The processors can be imported into a NiFi docker instance via volume binding:

1. Create a `docker-compose.yml` file with the following content in a new directory
    ````yaml
    services:
        nifi:
            image: apache/nifi:2.0.0
            environment:
              SINGLE_USER_CREDENTIALS_USERNAME: admin
              SINGLE_USER_CREDENTIALS_PASSWORD: ctsBtRBKHRAx69EqUghvvgEvjnaLjFEB
            ports:
              - 8443:8443
            volumes:
              - ./nifi-ext:/opt/nifi/nifi-current/nar_extensions:rw
              - ./nifi-drivers:/opt/nifi/nifi-current/lib_temp:rw
            entrypoint: [ "/bin/bash",
                          "-c",
                          "cp -r /opt/nifi/nifi-current/lib_temp/* /opt/nifi/nifi-current/lib/ && exec /opt/nifi/scripts/start.sh" ]
    ````
2. Create a directory `nifi-ext` in your current directory.
3. Download either the `...-nar-bundle.jar` and unpack this or download the individual required processors (.nar extension) from the [nexus repository].
   Next, place the required processors in the `nifi-ext` directory.
4. Additionally, for using any processor with a persistence layer, you will need to download the needed drivers.
   Create a `nifi-drivers` directory and place these drivers inside.
   Possible drivers are:
    - [PostgreSQL driver](https://jdbc.postgresql.org/download)
   - [SQLite driver](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)
5. Finally, start your instance.
    ````shell
    docker compose up
    ````
6. Log in at `https://localhost:8443/nifi` with the credentials mentioned in step 1
7. All downloaded extensions are available under the ``be.vlaanderen.informatievlaanderen.ldes.ldi.nifi`` group.

{: .note }
All documentation and notes about configuration are available in the NiFi component itself.

## Setting up Database Connection Pools

As of LDI 2.13.0, some processors require a database connection pool to be set up.
Although this is a pure NiFi configuration, a minimal explanation will be mentioned here. \
For more details, please refer to
the [NiFi documentation on DBCP](https://nifi.apache.org/components/org.apache.nifi.dbcp.DBCPConnectionPool/).
This can be done by following these steps:

1. Go to the `Controller Services` tab in the NiFi UI.
2. Click on the `Create new service` button.
3. Select the `DBCPConnectionPool` service.
4. Configure the service with the required properties:
   - `Database Connection URL`,
      - `jdbc:sqlite:/path/to/database.db` for SQLite
      - `jdbc:postgresql://localhost:5432/database` for PostgreSQL,
   - `Database Driver Class Name`
      - `org.sqlite.JDBC` for SQLite
      - `org.postgresql.Driver` for PostgreSQL,
   - `Database User`,
   - `Password`,
5. Save the service.
6. Enable the service.
7. Use the service in the processors that require it.

[Apache NiFi]: https://nifi.apache.org/
[nexus repository]: https://s01.oss.sonatype.org/#nexus-search;quick~be.vlaanderen.informatievlaanderen.ldes.ldi.nifi