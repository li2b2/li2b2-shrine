li2b2-SHRINE
============
Lightweight libraries for asynchronous query execution on distributed biomedical data repositories (e.g. i2b2).


Examples
--------
To use li2b2-SHRINE, you need at least one working i2b2 data warehouse (or you can choose 
to use the public demo data warehouse at https://i2b2.org/webclient/ )

1. Find out the API endpoint URL of your data warehouse (urlCellPM, domain, proxy)
You can find this information by pointing your browser to your webclient address and then accessing /webclient/i2b2_config_data.js

Take a note of the values at urlProxy, domain, urlCellPM. 

For urlProxy and urlCellPM you need to determine the absolute url with host. E.g. if urlProxy=index.php and you accessed the configration from 
`https://i2b2.org/webclient/i2b2_config_data.js` then your absolute proxy URL will be `https://i2b2.org/webclient/index.php`.


2. Download and unzip the latest i2b2-shrine-distributable...zip from https://github.com/li2b2/li2b2-shrine/releases
To run the shrine server, you can double-click on run_shrine.bat.
The li2b2-SHRINE webclient can then be accessed via http://localhost:8080/webclient/
If needed, the port number can be changed in run_shrine.bat

Any queries executed on the SHRINE webclient, will be stored and need to be fetched and executed by other data warehouses.
E.g. Create and run a query asking for female patients.

3. Run the node application for each data warehouse you want the queries to be executed on.
E.g. for the i2b2 installation in (1) use the noted urlProxy, urlCellPM and domain as follows:

Download and unzip the latest i2b2-node-distributable...zip from https://github.com/li2b2/li2b2-shrine/releases

Edit the file run_node.bat to contain your urlCellPM,urlProxy and user@domain and password. The default configuration will access
the public demo data warehouse at i2b2.org.
To forward the queries created in (2) to your data warehouse, double click on run_node.bat

4. View the results in the li2b2-SHRINE webclient from (2). 
You can drag-and-drop "Query 0" to Query Tool / Query Name to display the result diagram.
You can also click the `+` in "Previous Queries" to show numbers. If still logged in, 
refresh the "Previous Queries" by right-click or clicking on the refresh symbol next to "Previous Queries".

