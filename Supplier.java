
import siena.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

public class Supplier{

	public int restock(String item, Integer num, int price, int balance)
	{
		if((num * price)>balance){
			System.out.println("You do not have enough money to buy " + num + " " + item + "s.");
		}
		else
		{
			balance -= (num * price);
			System.out.println("Successfully restocked " + item + ".");
		}
		return balance;
	}

	public static void main(String args[]) {
	
		try {
			Supplier obj = new Supplier();
			SI stub = (SI) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(9988);
			registry.bind("SI", stub);

			System.err.println("Supplier ready to operate");
		} catch (Exception e) {
			System.err.println("Supplier exception: " + e.toString());
			e.printStackTrace();
		}
    }
}