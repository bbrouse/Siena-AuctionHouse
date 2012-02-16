
import siena.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
	
public class AuctionHouse implements Notifiable{	
	private static int balance = 10000; //balance of the auction house (affected y selling items and buying from supplier
	private static ArrayList<AuctionItem> inventory = new ArrayList<AuctionItem>(); //keeps track of all available items in the auction house
	static AuctionHouse obj = new AuctionHouse();
	static Scanner in = new Scanner(System.in);
	private String myID = "AuctionHouse";
	private static String address;
	
	public AuctionHouse() {
		
		AuctionItem lamp = new AuctionItem("Lamp", 200, 3);
		AuctionItem painting = new AuctionItem("Painting", 525, 1);
		AuctionItem sword = new AuctionItem("Sword", 475, 4);
		AuctionItem desk = new AuctionItem("Desk", 1000, 2);
		AuctionItem pants = new AuctionItem("Pants", 155, 5);
		
		inventory.add(lamp);
		inventory.add(painting);
		inventory.add(sword);
		inventory.add(desk);
		inventory.add(pants);
		
	}
	
	public void notify(Notification e) {
        System.out.println(myID + " just got this event:");
        System.out.println(e.toString() + "\n");
         if(e.getAttribute("SI_Event") != null){
        	if(e.getAttribute("SI_Event").stringValue().equals("Sale_confirmation")){
            	confirmSale(e);
            }
        }
        else if(e.getAttribute("CU_Event") != null){
        	//add incoming customer events here
        }
    };

    public void notify(Notification [] s) { 
    	System.out.println(myID + " just got a bunch of events:");
    	for (int i=0; i<s.length; i++){
    		System.out.println(s[i].toString() + "\n");
    		if(s[i].getAttribute("SI_Event") != null){
            	if(s[i].getAttribute("SI_Event").stringValue().equals("Sale_confirmation")){
                	confirmSale(s[i]);
                }
            }
            else if(s[i].getAttribute("CU_Event") != null){
            	//add incoming customer events here
            }
        }
    }
	
    public String getItemListing() {
		String response = "Items Available: \n";
		for(AuctionItem item: inventory){
			response = response + "\t" + item.name + ", quantity: " + item.numAvailable + "\n";
		}
		
		return response;
    }
    
    public String bid(String itemName, int bidValue){
    	try{
    	String response;
    	AuctionItem currentItem = null;
    	boolean found = false;
    	for(int i=0; i < inventory.size(); i++){
    		if(itemName.equalsIgnoreCase(inventory.get(i).name)){
    			found = true;
    			currentItem = inventory.get(i);
    			break;
    		}
		}
    	
    	if(!found){
    		response = "Item Not Found";
    	}
    	else{
    		if(bidValue >= currentItem.salePrice){
    			response = "Accepted:" + 0;
    			makeSale(itemName, bidValue);
    		}
    		else if(bidValue <= currentItem.stockPrice){
    			response = "Denied:" + currentItem.salePrice;
    		}
    		else{
    			int newOffer = currentItem.salePrice - ((currentItem.salePrice - bidValue)/2);
    			currentItem.salePrice = newOffer;
    			response = "Denied:" + currentItem.salePrice;
    		}
    	}
    	return response;
    	}catch (Exception e) {
    	    System.err.println("Bid Exception: " + e.toString());
    	    e.printStackTrace();
    	}
    	return null;
    }
    
    public void makeSale(String name, int salePrice){
    	AuctionItem currentItem = null;
    	for(int i=0; i < inventory.size(); i++){
    		if(inventory.get(i).name.equalsIgnoreCase(name)){
    			currentItem = inventory.get(i);
    			break;
    		}
		}
    	currentItem.decAvailable();
    	currentItem.resetSalePrice();
    	balance += salePrice;
    }
	
	public static void stock(){
		AuctionItem currentItem = null;
		try{
		ThinClient siena;
		siena = new ThinClient(address);
		for(int i=0; i < inventory.size(); i++){
			if(inventory.get(i).numAvailable == 0){
				currentItem = inventory.get(i);
				Notification e = new Notification();
			    e.putAttribute("AH_Event", "Restock");
		    	e.putAttribute("item", currentItem.name);
		    	e.putAttribute("number", 1);
				e.putAttribute("price", currentItem.stockPrice);
				e.putAttribute("balance", balance);
				
				try {
					siena.publish(e);
		    	} catch (SienaException ex) {
					System.err.println("Siena error:" + ex.toString());
		    	}
			}
		} 
		} catch (SienaException ex) {
    		System.err.println("Siena error in AuctionHouse:" + ex.toString());
		}
	}
	
	public static void confirmSale(Notification e){
		if(e.getAttribute("balance") != null){
			balance = e.getAttribute("balance").intValue();
			System.out.println("Balance changed to " + balance);
		}
		
	}
	
	public static void publishAuction(ThinClient siena){
		try {					
			Notification e = new Notification();
			e.putAttribute("AH_Event", "new_auction");
			e.putAttribute("Item", "Sword");
			e.putAttribute("price", 500);
			e.putAttribute("description", "It's a sword.");
			System.out.println("publishing " + e.toString());
			try {
				siena.publish(e);
			} catch (SienaException ex) {
				System.err.println("Siena error:" + ex.toString());
			}
			//Thread.sleep(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	
		Filter f = new Filter();
		f.addConstraint("CU_Event", "Bid");
		AuctionHouse party = new AuctionHouse();
		
		System.out.println("subscribing for " + f.toString());
	    try {
		siena.subscribe(f, party);
			try {
				Thread.sleep(60000);	// sleeps for 1 minute
			} catch (java.lang.InterruptedException ex) {
				System.out.println("interrupted"); 
			}
			System.out.println("unsubscribing");
			siena.unsubscribe(f, party);
	    } catch (SienaException ex) {
		System.err.println("Siena error:" + ex.toString());
	    }
	}
	
    public static void main(String args[]) {		
    	if(args.length != 1) {
		    System.err.println("Usage: Supplier <server-address>");
		    System.exit(1);
		}
		
		try {
			ThinClient siena;
			// accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
			address = args[0];
			siena = new ThinClient(address);

		    Filter f = new Filter();
		    f.addConstraint("SI_Event", Op.EQ, "Sale_confirmation");
		    //------ADD ALL CUSTOMER INCOMING EVENTS HERE----
		    AuctionHouse party = new AuctionHouse();
		    
		    System.out.println("AuctionHouse Subscribing: " + f.toString());
		    try {
		    	siena.subscribe(f, party);
		    	try {
		    		for (int i=0; i<1; i++) {
		    		    Notification e = new Notification();
		    		    e.putAttribute("AH_Event", "Restock");
		    	    	e.putAttribute("item", "CHAIR");
		    	    	e.putAttribute("number", 2);
		    			e.putAttribute("price", 150);
		    			e.putAttribute("balance", 1000);
		    		    System.out.println("publishing " + e.toString());
		    		    try {
		    				siena.publish(e);
		    	    	} catch (SienaException ex) {
		    				System.err.println("Siena error:" + ex.toString());
		    	    	}
		    	    	Thread.sleep(1000);
		    	    }	
		    		//Thread.sleep(86400000);	// sleeps for 24 hours
		    	} catch (java.lang.InterruptedException ex) {
		    		System.out.println("AuctionHouse interrupted"); 
		    	}
		    	
		    	System.out.println("AuctionHouse unsubscribing");
		    	siena.unsubscribe(f, party);
		    	
		    } catch (SienaException ex) {
		    		System.err.println("Siena error in AuctionHouse:" + ex.toString());
		    }
		    	
				publishAuction(siena);
				
		    	System.out.println("AuctionHouse shutting down.");
		    	siena.shutdown();
		    	System.exit(0);
		    	
		} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
		} 
	    };
}

