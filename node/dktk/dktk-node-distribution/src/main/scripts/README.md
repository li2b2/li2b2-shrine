DKTK client node for the li2b2 SHRINE
=====================================

Run the client via `run_node.sh` or `run_node.bat`.

In the script files, four command line arguments are specified.
1. Location (URL) of the li2b2 broker REST endpoint
2. API Key for the li2b2 broker
3. URL to the Centraxx REST endpoint
4. XSL transformation to translate broker queries (from i2b2) to DTKT syntax

The client application will read and write a file `pending.properties` in it's
working directory, in order to store status information for unfinished queries
bettween runs.
