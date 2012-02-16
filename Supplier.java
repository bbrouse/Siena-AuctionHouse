
import siena.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Supplier implements Notifiable{
	
	private String myID = "Supplier";
	private static String address;
	
	public void restock(Notification e)
	{ 
		ThinClient siena;
		try{
		// accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
		siena = new ThinClient(address);
		String item = e.getAttribute("item").stringValue();
		int num = e.getAttribute("number").intValue();
		int price = e.getAttribute("price").intValue();
		int balance = e.getAttribute("balance").intValue();
		
		if((num * price)>balance){
			System.out.println("You do not have enough money to buy " + num + " " + item + "s.");
		}
		else
		{
			balance -= (num * price);
			System.out.println("Successfully restocked " + item + ".");
		}

		Notification out = new Notification();
		out.putAttribute("SI_Event", "Sale_confirmation");
		out.putAttribute("item", item);
		out.putAttribute("number", num);
		out.putAttribute("new_balance", balance);
		out.putAttribute("destination", "AH");
	    System.out.println("publishing supplier sale" + out.toString());
		siena.publish(out);
    	} catch (SienaException ex) {
			System.err.println("Siena error:" + ex.toString());
    	}
	    
	}
	
	
	public void notify(Notification e) {
        System.out.println(myID + " just got this event:");
        System.out.println(e.toString() + "\n");
        if(e.getAttribute("AH_Event").stringValue().equals("Restock")){
        	restock(e);
        }
    };

    public void notify(Notification [] s) { 
    	System.out.println(myID + " just got a bunch of events:");
    	for (int i=0; i<s.length; i++){
    		System.out.println(s[i].toString() + "\n");
    		if(s[i].getAttribute("AH_Event").stringValue().equals("Restock")){
    			restock(s[i]);
    		}
        }
    }

	public static void main(String[] args) {
		if(args.length != 1) {
		    System.err.println("Usage: Supplier <server-address>");
		    System.exit(1);
		}
		
		ThinClient mySiena;
		try {
			ThinClient siena;
			// accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
			address = args[0];
			siena = new ThinClient(address);

		    Filter f = new Filter();
		    f.addConstraint("AH_Event", Op.EQ, "Restock"); 
		    Supplier party = new Supplier();
		    
		    System.out.println("Supplier Subscribing: " + f.toString());
		    try {
		    	siena.subscribe(f, party);
		    	try {
		    		Thread.sleep(86400000);	// sleeps for 24 hours
		    	} catch (java.lang.InterruptedException ex) {
		    		System.out.println("supplier interrupted"); 
		    	}
		    	
		    	System.out.println("supplier unsubscribing");
		    	siena.unsubscribe(f, party);
		    	
		    } catch (SienaException ex) {
		    		System.err.println("Siena error in supplier:" + ex.toString());
		    }
		    	
		    	System.out.println("Supplier shutting down.");
		    	siena.shutdown();
		    	System.exit(0);
		    	
		} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
		} 
	    };
}