/**
 * Utils.java - a utility class
 * This contains  the basic utilities required for the effective operation of Network Tools
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
 * @author Heidir
 * @version 1.0, 1st August 2012
 */

package networktools.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * contains  the basic utilities required for the effective operation of Network Tools
 *
 * @author Heidir
 * @version 0.2, 1st January 2014
 */
public class Utils {
    /**
     * encodeToString
     * <br />
     * Encodes an image to Base64 string
     *
     * @param image The image to encode
     * @param type jpeg, bmp, png, ....
     * @return encoded string
     * @author Bai Ben
     * @source http://ben-bai.blogspot.sg/2012/08/java-convert-image-to-base64-string-and.html
     * @since version 1.0
     */
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
    
    /**
     * decodeToImage
     * <br />
     * Decodes Base64 string to image
     *
     * @param imageString The string to decode
     * @return decoded image
     * @author Bai Ben
     * @source http://ben-bai.blogspot.sg/2012/08/java-convert-image-to-base64-string-and.html
     * @since version 1.0
     */
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
    
    /**
     * validatePort
     * <br />
     * Validates port number to be in a range between 1 and 65535 for TCP/IP communications
     *
     * @param port Port number
     * @return true if valid
     * @since version 0.2, 1st January 2014
     */
    public static boolean validatePort(int port) {
        if (port >= 1 && port < 65535)
            return true;
        else 
            return false;
    }
    
     /**
     * validatePort
     * <br />
     * Validates port number to be in a range between 1 and 65535 for TCP/IP communications.
     *
     * @param txtField Accepts JTextField object 
     * @return true if valid
     * @since version 0.2, 1st January 2014
     */
    public static boolean validatePort(JTextField txtField) {
        if (Integer.parseInt(txtField.getText().toString()) >= 1 && Integer.parseInt(txtField.getText().toString()) < 65535)
            return true;
        else 
            return false;
    }
    
    /**
     * getIntFromField
     * <br />
     * Parses a JTextField contents and return value as integer<br />
     * Prerequisite: Recommended to run validateFieldasInt() first to validate content before calling method
     *
     * @param txtField Accepts JTextField object 
     * @return integer value of content
     * @since version 0.2, 1st January 2014
     */
    public static int getIntFromField(JTextField txtField) {
        int intField = 0;
        
        if (validateFieldIsNotEmpty(txtField)) {
            try {
                intField = Integer.parseInt(txtField.getText().toString());
                return intField;
                
            } catch (Exception e) {
                return intField;
            }
        }
        return intField;
    }
    
    /**
     * validateFieldasInt
     * <br />
     * Validates contents of JTextField as having integer values
     *
     * @param txtField Accepts JTextField object 
     * @return true if valid
     * @since version 0.2, 1st January 2014
     */
    public static boolean validateFieldasInt(JTextField txtField) {
        int intField;
        
        if (validateFieldIsNotEmpty(txtField)) {
            try {
                intField = Integer.parseInt(txtField.getText().toString());
                return true;            
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * validateFieldIsNotEmpty
     * <br />
     * Parses a JTextField contents and verify contains a value
     *
     * @param txtField Accepts JTextField object 
     * @return true if contains a value
     * @since version 0.2, 1st January 2014
     */
    public static boolean validateFieldIsNotEmpty(JTextField txtField) {
        if (txtField.getText().isEmpty() || txtField.getText().toString().trim()=="")
            return false;
        return true;
    }
    
    /**
     * addMessage
     * <br />
     * Appends string into designated JTextArea
     *
     * @param txtArea Accepts JTextArea object 
     * @param msg String array containing strings to be appended into the JTextArea
     * @since version 0.2, 1st January 2014
     */
    public static void addMessage(JTextArea txtArea, String[] msg) {
        for (int i=0; i<msg.length; i++) {
            txtArea.append(msg[i] + "\n");
        }        
        txtArea.append("------------------------------------------\n");
    }
    
    /**
     * addMessage
     * <br />
     * Appends string into designated JTextArea
     *
     * @param txtArea Accepts JTextArea object 
     * @param msg String to be appended into the JTextArea
     * @since version 0.2, 1st January 2014
     */
    public static void addMessage(JTextArea txtArea, String msg) {
        txtArea.append(msg + "\n");
    }
    
    /**
     * isPortUp
     * <br />
     * Validate whether a specific TCP/IP port is already in use
     *
     * @param port An integer value between 1 to 65535
     * @return true if port is in use
     * @since version 0.2, 1st January 2014
     */
    public static boolean isPortUp(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (BindException ex) {
            
        } catch (IOException e) {
            
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                    
                }
                return true;
            }
        }
        return false;
    }
}
