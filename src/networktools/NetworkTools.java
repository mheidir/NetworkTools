/**
 * NetworkTools - a Network Test Tools for Java.  This program contains a compilation of basic 
 * Network Test Tools for verification of NTP, SNMP and Syslog on appliances within the network
 * 
 * NTP Test Tool - A Java implementation of NTP Client
 * SNMP Test Tool - A Java implementation of SNMP Polling
 * Syslog Test Tool - A Java implementation for sending Syslog messages or receive Syslog messages
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
 * @author Muhammad Heidir
 */
package networktools;

/**
 * The main Network Tools class to start up the utility
 *
 * @author Heidir
 * @version 0.1, 24th December 2013
 */
public class NetworkTools {

    /**
     *
     * Main Method
     * <br />
     * This methods initialises Network Tools user interface and display to screen
     *
     * @param args Not processed, no options available
     * @since version 0.1
     */
    public static void main(String[] args) {
        NToolGUI ui = new NToolGUI();
        ui.createAndShowUI();
    }
}
