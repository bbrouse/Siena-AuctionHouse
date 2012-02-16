import siena.*;
import java.util.Date;
import java.util.Scanner;

public class Customer implements Notifiable {

	private String myID = "Customer";
	Scanner in = new Scanner(System.in);
	static ThinClient mySiena;

    /*public void notify(Notification e) {
        if(e.getAttribute("AH_Event") != null)
		{
			if(e.getAttribute("Item") != null)
			{
				String item = e.getAttribute("Item").toString();
				System.out.println("Would you like to bid for the " + item + "? (Y/N)");
				String choice = in.nextLine();
				if(choice.equalsIgnoreCase("y"))
				{
					System.out.println("The current price of " + item + " is " + e.getAttribute("price").toString() + ".");
					System.out.println("Please enter a numerical bid.");
					Integer bid = Integer.parseInt(in.nextLine());
					System.out.println("Placing bid of " + bid + " on item " + item + ".");
					publish(item, bid);
				}
			}
		}
    };*/
	
	public void notify(Notification e) {
        System.out.println(myID + " just got this event:");
        System.out.println(e.toString());
		publish(e.getAttribute("Item").toString(), 750);
    };
	

    public void notify(Notification [] s) { 
    	System.out.println(myID + " just got a bunch of events:");
    	for (int i=0; i<s.length; i++)
    		System.out.println(s[i].toString());
    }
	
	public static void publish(String item, Integer bid){
		try {
			Thread.sleep(2000);
			Notification e = new Notification();
			e.putAttribute("timestamp", "" + new Date().getTime());
			e.putAttribute("Bid", bid);
			e.putAttribute("CU_Event", "Bid");
			e.putAttribute("Item", "Sword");
			e.putAttribute("destination", "AH");
			System.out.println("publishing " + e.toString());
			try {
				mySiena.publish(e);
			} catch (SienaException ex) {
				System.err.println("Siena error:" + ex.toString());
			}
			Thread.sleep(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage: InterestedParty <server-address>");
			System.exit(1);
		}
		
		try {
			// accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
			mySiena = new ThinClient(args[0]);

			Filter f = new Filter();
			f.addConstraint("AH_Event", Op.EQ, "new_auction"); // interested only in Antiques
			f.addConstraint("price", Op.LT, 5000); // interested only if their initial price is below 4,997$
			Customer party = new Customer();
			
			System.out.println("subscribing for " + f.toString());
			try {
				mySiena.subscribe(f, party);
				try {
					Thread.sleep(120000);	// sleeps for 2 minutes
				} catch (java.lang.InterruptedException ex) {
					System.out.println("interrupted"); 
				}
				System.out.println("unsubscribing");
				mySiena.unsubscribe(f, party);
			} catch (SienaException ex) {
			System.err.println("Siena error:" + ex.toString());
			}
			System.out.println("shutting down.");
			mySiena.shutdown();
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		} 
    };
}
