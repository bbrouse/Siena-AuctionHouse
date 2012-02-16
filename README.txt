To run these included files, make a classes directory:
mkdir ./classes

Then you need to compile with:
javac -d classes/ -cp ./classes:siena-2.0.1.jar *.java

And then start the server with:
java -cp ./classes:siena-2.0.1.jar -Xms128m -Xmx512m siena.StartDVDRPServer -id myserver -receiver tcp:localhost:2345 -log ./log.txt -store ./subscriptions.txt &

Firstly, run the server file with:
java -cp ./classes:siena-2.0.1.jar Supplier tcp:localhost:2345 &

And then the customer(s) with:
java -cp ./classes:siena-2.0.1.jar Customer tcp:localhost:2345 &

And lastly the AuctionHouse with:
java -cp ./classes:siena-2.0.1.jar AuctionHouse tcp:localhost:2345 &

It's important to run the AuctionHouse last as the other two entities rely on publishes from the AH.

Other than that, just know that these programs don't wait very long, so there isn't much time to wait in executing them.

We included sample interactions as a faux Auction House transaction.