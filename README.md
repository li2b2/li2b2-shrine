li2b2-SHRINE
============
Lightweight libraries for asynchronous query execution on distributed biomedical data repositories (e.g. i2b2).


Usage
=====

First step: Determine data warehouse installation / data repositories to use for distributed queries
----------
To use li2b2-SHRINE, you need at least one working i2b2 data warehouse (or you can choose 
to use the public demo data warehouse at https://i2b2.org/webclient/ )

For each i2b2 data repository, you need to find the API endpoint configuration (`urlCellPM`, `domain`, `proxy`).
You can find this information by pointing your browser to your webclient address and then accessing /webclient/i2b2_config_data.js  
Take a note of the values at `urlProxy`, `domain`, `urlCellPM`.

For `urlProxy` and `urlCellPM` you need to determine the absolute url with host. E.g. if `urlProxy=index.php` and you accessed the configration from 
`https://i2b2.org/webclient/i2b2_config_data.js` then your absolute proxy URL will be `https://i2b2.org/webclient/index.php`.


Second step: Run the li2b2-SHRINE broker/server
------------
Download and unzip the latest `i2b2-shrine-distributable...zip` from https://github.com/li2b2/li2b2-shrine/releases
To run the shrine server, you can double-click on run_shrine.bat.

The li2b2-SHRINE webclient can then be accessed via http://localhost:8080/webclient/  

If needed, the port number can be changed in run_shrine.bat  

Any queries executed on the SHRINE webclient, will be stored and need to be fetched and executed by other data warehouses.  
E.g. Create and run a query asking for female patients.

A file `api-keys.properites` contains the API-Keys to identify and authenticate any data warehouse nodes. You can change all
values to your needs or leave them as they are for testing. The API-Keys will be used in the next step. 


Third step: For each data repository, run the node/connector application
-----------
For i2b2 data repositories, you can use the latest `i2b2-node-distributable...zip` from https://github.com/li2b2/li2b2-shrine/releases

The node application will fetch all queries from the SHRINE broker and executes them on a specific data repository.
This application must be run on a computer or network which can access both the target data repository as well as the SHRINE broker.
For productive environments, you can would run this application directly on the data warehouse server.

The node application does not run continuously, but executes pending queries and terminates afterwards. For continuous execution,
you can schedule this application to run e.g. every 5 minutes (e.g. cron on linux).

E.g. for the i2b2 installation in (1) use the noted `urlProxy`, `urlCellPM` and `domain` as follows:  
Download and unzip the latest `i2b2-node-distributable...zip` from https://github.com/li2b2/li2b2-shrine/releases  
Edit the file `run_node.bat` to contain your `urlCellPM`, `urlProxy`, `user@domain` and password. 
If you don't change this configuration, the default configuration will access the public demo data warehouse at i2b2.

The `run_node.bat` also contains one API-Key from step two. If modified, change accordingly.

To forward the queries created in step two to your data warehouse, double click on run_node.bat

If the node application is run directly on the i2b2 server, the `urlProxy` is not needed.

This step should be executed for each data warehouse / data repository.



Last step: View the results in the li2b2-SHRINE webclient
----------

Open li2b2-SHRINE webclient from step two. Your previous queries are listed on the left bottom section "Previous Queries".

You can drag-and-drop "Query 0" to Query Tool / Query Name to display the result diagram. 

You can also click the `+` in "Previous Queries" to show numbers. If still logged in, you may need 
to refresh the "Previous Queries" by right-click or clicking on the refresh symbol next to "Previous Queries".

If you choose to run more queries, don't forget to run the node-application from step two which fetches and executes queries (for each data repository).

