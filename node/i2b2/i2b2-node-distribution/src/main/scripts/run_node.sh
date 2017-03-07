#!/usr/bin/env bash

# If using cron to schedule execution of this script, please make sure
# it is not executed in parallel - e.g. by cron with short intervals.

java -cp lib/\* de.li2b2.shrine.node.i2b2.I2b2Node http://localhost:8080/broker/ xxxApiKey567 'http://services.i2b2.org/i2b2/services/PMService/|https://www.i2b2.org/webclient/index.php' demo@i2b2demo demouser i2b2_to_i2b2.xsl
 
# for testing, you can use the random count node
#java -cp lib/\* de.li2b2.shrine.node.i2b2.RandomCountNode http://localhost:8080/broker/ xxxApiKey567 
