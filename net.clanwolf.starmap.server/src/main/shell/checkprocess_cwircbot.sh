#!/bin/sh
ps -fC java | grep CWIRCBot.jar || /opt/jdk-17/bin/java -jar /var/www/vhosts/clanwolf.net/httpdocs/apps/C3/server/CWIRCBot.jar ; exit
exit
