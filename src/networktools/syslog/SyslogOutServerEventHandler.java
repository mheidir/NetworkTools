/**
 * SyslogOutServerEventHandler.java - a syslog server handler implementation 
 * This program contains a compilation of basic 
 * Network Test Tools for verification of NTP, SNMP and Syslog on appliances within the network
 * 
 * 
 * This code is copyright (c) Muhammad Heidir 2013
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.  A HTML version of the GNU General Public License can be
 * seen at http://www.gnu.org/licenses/gpl.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *  
 * @author Syslog4j
 * @version 0.1, 24th December 2013
 */

package networktools.syslog;

import javax.swing.JTextArea;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;

/**
 * SyslogOutServerHandler redirects output from System.out.println() to JTextArea
 * <br />Re-written from original source by Syslog4j
 *
 * @author Heidir
 * @version 0.1, 24th December 2013
 */
public class SyslogOutServerEventHandler extends OutPrintStreamSyslogServerEventHandler {
    private static final long serialVersionUID = 1690551409588182601L;

    public static SyslogServerSessionEventHandlerIF create() {
        return new SyslogOutServerEventHandler();
    }
	
    public SyslogOutServerEventHandler() {
        super(System.out);
    }
    
    public SyslogOutServerEventHandler(JTextArea txtArea) {
        super(txtArea);
    }
}
