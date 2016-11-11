#!/usr/bin/env bash

java -cp lib/\* de.sekmi.shrine.node.dktk.CentraxxNode http://gbn.data.dzl.de/broker/ centraxx1 http://localhost:8888/centraxx/rest/teiler/ i2b2_to_dktk.xsl
 
