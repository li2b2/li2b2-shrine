Retrieve queries from an ASHRINE and convert and execute them
in a DKTK Centraxx data warehouse.

Algorithm
---------

1. Load pending queries from pending.properties
2. Load requests from the SHRINE and add them to the pending queries
3. Try to retrieve results (patient count) for each pending query and notify broker if successful
4. Unfinished qureries are written to pending.properties
