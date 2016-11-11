#!/usr/bin/env bash

java -cp lib/\* org.aktin.shrine.node.i2b2.I2b2Node http://localhost:8080/broker/ i2b2_1 'http://services.i2b2.org/i2b2/services/PMService/|https://www.i2b2.org/webclient/index.php' demo@i2b2demo demouser i2b2_translation.xsl
 
