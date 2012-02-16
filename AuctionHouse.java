
import siena.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
	
public class AuctionHouse{	
	private static int balance = 10000; //balance of the auction house (affected y selling items and buying from supplier)

	private static ArrayList<AuctionItem> inventory = new ArrayList<AuctionItem>(); //keeps track of all available items in the auction house
    
	static AuctionHouse obj = new AuctionHouse();
	
	static Scanner in = new Scanner(System.in);
	
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
				Registry registry = LocateRegistry.getRegistry(9988);
				SI stub = (SI) registry.lookup("SI"); 
				
				System.out.println("Which item would you like to restock?");
				String item = in.nextLine();
				int oldBalance = balance;
				for(int i=0; i < inventory.size(); i++){
					if(item.equalsIgnoreCase(inventory.get(i).name)){
						currentItem = inventory.get(i);
						System.out.println("How many of " + item + " would you like to buy?");
						Integer num = Integer.parseInt(in.nextLine());
						balance = stub.restock(inventory.get(i).name, num, inventory.get(i).stockPrice, balance);
						if(oldBalance > balance)
							inventory.get(i).numAvailable += num;
						return;
					}
				} 
				System.out.println("The auction house does not have a " + item + ".");
			}
			catch (Exception e) {
				System.err.println("Customer exception: " + e.toString());
				e.printStackTrace();
			}
	}
	
    public static void main(String args[]) {		
		if(args.length > 0){
			if(args[0].equalsIgnoreCase("Restock"))
				stock();
			
			return;
		}
		
		try {
			AH stub = (AH) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(9988);
			registry.bind("AH", stub);

			System.err.println("AuctionHouse ready to operate");
		} catch (Exception e) {
			System.err.println("AuctionHouse exception: " + e.toString());
			e.printStackTrace();
		}
    }
}

