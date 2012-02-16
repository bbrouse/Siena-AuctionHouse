#!/bin/sh

#create dir for .class files
mkdir ./classes

#compile java files
javac -d classes/ -cp ./classes:siena-2.0.1.jar *.java

#start SIENA SERVER with tcp protocol on port 2345
java -cp ./classes:siena-2.0.1.jar -Xms128m -Xmx512m siena.StartDVDRPServer -id myserver -receiver tcp:localhost:2345 -log ./log.txt - store ./subscriptions.txt &

#start an Interested Party 
java -cp ./classes:siena-2.0.1.jar InterestedParty tcp:localhost:2345 &

# start an Object of Interest
java -cp ./classes:siena-2.0.1.jar ObjectOfInterest tcp:localhost:2345 &

sleep 1

# start another Interested Party
java -cp ./classes:siena-2.0.1.jar InterestedParty tcp:localhost:2345 &


