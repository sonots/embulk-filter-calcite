# Apache Calcite filter plugin for Embulk

## Overview

* **Plugin type**: filter

This plugin allows users to translate rows flexibly by SQL queries specified by them. 

## Architecture

This plugin allows translating rows by SQL queries in Pages received from input plugin and sending the query results to next filter or output plugin as modified Pages. It uses [Apache Calcite](https://calcite.apache.org/), which is the foundation for your next high-performance database and enbles executing SQL queries to customized storage by the [custom adaptor](https://calcite.apache.org/docs/tutorial.html). The plugin applies Page storage adaptor to Apache Calcite and then enables executing SQL queries to Pages via JDBC Driver provided.

Here is Embulk config example for this plugin:

```yaml
filters:
  - type: calcite
    query: SELECT * FROM $PAGES
```

Users can define `SELECT` query as query option in the filter config section. `$PAGES` represents Pages that input plugin creates and sends. `$PAGES` schema is Embulk input schema given. On the other hand, the output schema of the plugin is built from the metadata of query result.

SQL language provided by Apache Calcite: https://calcite.apache.org/docs/reference.html

| Embulk type | Apache Calcite type |      JDBC type      |
| ----------- | ------------------- | ------------------- |
| boolean     | BOOLEAN             | java.lang.Boolean   |
| long        | BIGINT              | java.lang.Long      |
| double      | DOUBLE              | java.lang.Double    |
| timestamp   | TIMESTAMP           | java.sql.Timestamp  |
| string      | VARCHAR             | java.lang.String    |
| json        | VARCHAR             | java.lang.String    |

Data types by Apache Calcite: https://calcite.apache.org/docs/reference.html#data-types

## Configuration

- **query**: SQL to run (string)
- **default_timezone**: If the sql type of a column is `date`/`time`/`datetime` and the embulk type is `string`, column values are formatted int this default_timezone. You can overwrite timezone for each columns using column_options option. (string, default: `UTC`)

## Example

```yaml
filters:
  - type: calcite
    query: SELECT * FROM $PAGES
```

## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```
