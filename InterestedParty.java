// -*- Java -*-
//
//  This file is part of Siena, a wide-area event notification system.
//  See http://www.inf.unisi.ch/carzaniga/siena
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
// this is an example of an interested party, that is, a consumer of
// notifications
//
import siena.*;

public class InterestedParty implements Notifiable {

	private String myID = "SomeSpecificID";

    public void notify(Notification e) {
        System.out.println(myID + " just got this event:");
        System.out.println(e.toString());
    };

    public void notify(Notification [] s) { 
    	System.out.println(myID + " just got a bunch of events:");
    	for (int i=0; i<s.length; i++)
    		System.out.println(s[i].toString());
    }

    public static void main(String[] args) {
	if(args.length != 1) {
	    System.err.println("Usage: InterestedParty <server-address>");
	    System.exit(1);
	}
	
	ThinClient mySiena;
	try {
		// accepts one argument with a String in the form <protocol>:<ipaddress>:<port>
		mySiena = new ThinClient(args[0]);

	    Filter f = new Filter();
	    f.addConstraint("tag", Op.EQ, "Antiques"); // interested only in Antiques
	    f.addConstraint("price", Op.LT, 4997); // interested only if their initial price is below 4,997$
	    InterestedParty party = new InterestedParty();
	    
	    System.out.println("subscribing for " + f.toString());
	    try {
		mySiena.subscribe(f, party);
		try {
		    Thread.sleep(60000);	// sleeps for 1 minute
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
