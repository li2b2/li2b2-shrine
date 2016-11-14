#!/usr/bin/env bash

java -cp lib/\* de.li2b2.shrine.node.dktk.CentraxxNode http://localhost:8080/broker/ xxxApiKey123 http://localhost:8888/centraxx/rest/teiler/ i2b2_to_dktk.xsl
 
