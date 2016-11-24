/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networktools.snmp;

import static networktools.snmp.TrapSenderVersion1.Oid;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 * @author Muhammad Heidir
 * @description Extends the original class by Jitendra
 */
public class TrapSenderVersion1a extends TrapSenderVersion1 {
    public void sendTrap_Version1(String community, String oid, String ipAddress, int port) {
        try {
            // Create Transport Mapping
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(community));
            cTarget.setVersion(SnmpConstants.version1);
            cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            cTarget.setTimeout(5000);
            cTarget.setRetries(2);

            PDUv1 pdu = new PDUv1();
            pdu.setType(PDU.V1TRAP);
            pdu.setEnterprise(new OID(Oid));
            pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
            pdu.setSpecificTrap(1);
            pdu.setAgentAddress(new IpAddress(ipAddress));

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            System.out.println("Sending V1 Trap... Check Wheather NMS is Listening or not? ");
            snmp.send(pdu, cTarget);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
