#!/usr/bin/env bash

java -cp lib/\* org.aktin.shrine.node.dktk.CentraxxNode http://gbn.data.dzl.de/broker/ centraxx1 http://localhost:8888/centraxx/rest/teiler/ dktk_translation.xsl
 
