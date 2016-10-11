Retrieve queries from an ASHRINE and convert and execute them
in a DKTK Centraxx data warehouse.

Algorithm
---------

1. Load pending queries from pending.properties
2. Load requests from the SHRINE and add them to the pending queries
3. Try to retrieve results (patient count) for each pending query and notify broker if successful
4. Unfinished qureries are written to pending.properties

Run the DKTK node
-----------------

1. Copy the following files into a directory `lib`: shrine-dktk-node-*.jar, broker-client-*.jar, broker-api-*.jar
2. Copy/Edit query translation scripts from examples folder
3. Run `java -cp lib/\* org.aktin.shrine.node.dktk.CentraxxNode [broker-uri] [api-key] [centraxx-endpoint] [transformation]`.

Example:

```
java -cp lib/\* org.aktin.shrine.node.dktk.CentraxxNode http://localhost:8080/broker/ centraxx1 http://localhost:8888/centraxx/rest/teiler/ target/dktk_translation.xsl
```
