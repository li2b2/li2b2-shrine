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


Testing and Debugging
=====================

First, run this program without any queries at the broker side. The programm
will contact the broker, retrieve 0 (zero) queries and report it's status.
Usually the program will output that there are no pending queries and exit
normally. If no error is displayed, you can be sure that the broker endpoint
URL is reachable.
(If errors are displayed, see the section below for troubleshooting)

Second, you should create a simple query at the broker and run the program
again. This time, it will retrieve the query, transform it and forward
it to the Centraxx endpoint. During each run, the program will check whether
any pending queries were completed by the Centraxx endpoint. Run the program
multiple times until Centraxx is finished with the query. After this case,
any further program runs will always report no pending queries.

 
Troubleshooting
===============

Server returned HTTP response code: 401
---------------------------------------
You are using invalid authentication. E.g. API key wrong or missing.


Connection refused: connect
---------------------------
Either the broker or the Centraxx server endpoints could not be reached.
Please make sure that the the endpoint URLs can be accessed, e.g. by
trying to enter the URLs into a browser.