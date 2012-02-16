
import java.io.Serializable;

import siena.*;

public class AuctionItem implements Serializable{
	public String name;
	public int stockPrice;
	public int numAvailable;
	public int salePrice;
	
	public AuctionItem(String n, int inStockPrice, int inNumAvailable){
		name = n;
		stockPrice = inStockPrice;
		numAvailable = inNumAvailable;
		salePrice = (int) (inStockPrice + (inStockPrice * .50));
	}
	
	public void incAvailable(){
		numAvailable++;
	}
	
	public void decAvailable(){
		numAvailable--;
	}
	
	public void resetSalePrice(){
		salePrice = (int) (stockPrice + (stockPrice * .50));
	}
}