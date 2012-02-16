// -*- Java -*-
//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.inf.unisi.ch/carzaniga/siena/
//
//  Author: Antonio Carzaniga (firstname.lastname@unisi.ch)
//  See the file AUTHORS for full details. 
//
//  Copyright (C) 1998-1999 University of Colorado
//
//  This program is free software; you can redistribute it and/or
//  modify it under the terms of the GNU General Public License
//  as published by the Free Software Foundation; either version 2
//  of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
//  USA, or send email to Antonio Carzaniga (firstname.lastname@unisi.ch).
//

//
// this is an example of an object of interest, that is, a producer of
// notifications
//
import siena.*;
import java.util.Date;


public class ObjectOfInterest {	
    public static void main(String[] args) {
	try {
	    ThinClient siena = null;
	    // accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
	    switch(args.length) {
		    case 1: siena = new ThinClient(args[0]); 
	 	   case 0: break;
	    	default:
			System.err.println("Usage: ObjectOfInterest [server-address]");
			System.exit(1);
	    }
	    
 
		for (int i=0; i<10; i++) {
		    Notification e = new Notification();
		    e.putAttribute("timestamp", "" + new Date().getTime());
	    	e.putAttribute("price", 5000-i);
	    	e.putAttribute("tag", "Antiques");
			e.putAttribute("description", "some random piece of overpriced old stuff");
			e.putAttribute("itemId", "AH-item#0001");
		    System.out.println("publishing " + e.toString());
		    try {
				siena.publish(e);
	    	} catch (SienaException ex) {
				System.err.println("Siena error:" + ex.toString());
	    	}
	    	Thread.sleep(1000);
	    }	
	    System.out.println("shutting down.");
	    siena.shutdown();
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
    }
}
