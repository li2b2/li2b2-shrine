#!/usr/bin/env bash

# Export ontology (subtree) into an XML file
# for full export, remove the subtree key

java -cp lib/\* de.sekmi.li2b2.client.ont.XMLExport http://services.i2b2.org/i2b2/services/PMService/ demo@i2b2demo demouser '\\i2b2_REP\i2b2\Reports\' > ontology.xml

# if the PM cell is not available on the network,
# you can use the i2b2 PHP proxy as in run_node.bat
