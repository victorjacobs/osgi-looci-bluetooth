/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
/*
 * Copyright (c) 2012, Katholieke Universiteit Leuven
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package Bluetooth;

import java.util.HashMap;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.util.Utils;

/**
 * 
 *
 * @author 
 * @version 1.0
 * @since 2012-01-01
 *
 */

public class bluetoothOsgiComp extends LoociComponent {
	
    /**
     * Holds the parent codebase
     */
    private bluetoothOsgi _parent;
    
    private ConsoleFrame fr;


	/**
	 * LooCIComponent(<name>, <provided interfaces>, <required interfaces>);
	 */
    public bluetoothOsgiComp(bluetoothOsgi parent) {
    	fr = new ConsoleFrame();
    	System.out.println("[BT] Component created");
        _parent = parent;
    }

    @Override
    public void receive(short event_id, byte[] payload) {
    	if (event_id == 4242) {
    		Event ev = getReceptionEvent();
    		
    		// Construct string
    		String pl = new String(ev.getPayload());
    		
    		//System.out.println("[DISPLAY] temp received from " +  ev.getSourceAddress() + "/" + ev.getSourceComp() + " at " + System.currentTimeMillis() + ": " + (int)ev.getPayload()[0]);
    		fr.println("[BT] received INQ: " + pl);
    	}
		
    }
    
    @Override
    public void componentStart() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                fr.setVisible(true);
                fr.println("[BT] Started");
            }
        });
    }

    @Override
    public void componentStop() {
    	fr.setVisible(false);
    	fr.dispose();
    }
}
