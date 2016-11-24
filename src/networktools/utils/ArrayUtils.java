/**
 * ArrayUtils.java - an ArrayList utility for creating a String array without knowing exact value
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
 * @author Muhammad Heidir
 * @version 0.2, 1st January 2014
 */

package networktools.utils;

import java.util.ArrayList;

/**
 * Simple ArrayList implementation for storing of SNMP Trap variables
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class ArrayUtils {
    ArrayList<String> list;
    
    /**
     * ArrayUtils constructor
     * <br />
     * This method initialises ArrayList for storing of strings
     *
     * @see ArrayUtils
     * @since version 0.2
     */
    public ArrayUtils() {
         list = new ArrayList<String>();
    }
    
    /**
     *
     * addString method
     * This method receives a string and appends it into the list
     *
     * @param message String variable to be printed
     * @since version 0.2
     */
    public void addString(String message) {
        list.add(message);
    }
    
    /**
     *
     * getLength method
     * The method returns an integer containing the size of ArrayList
     *
     * @return integer Size of ArrayList
     * @since version 0.2
     */
    public int getLength() {
        return list.size();
    }
    
    /**
     *
     * getStringArray method
     * This method returns String array based on the contents of the ArrayList
     * according to the index starting from 0
     *
     * @return integer Size of ArrayList
     * @since version 0.2
     */
    public String[] getStringArray() {
        String[] msg = new String[list.size()];
        
        for (int i=0; i<list.size(); i++) {
            msg[i] = list.get(i);
        }
        return msg;
    }
}
