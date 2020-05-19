package smtpserver.utils;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DnsMxUtil {

    public static boolean dnsMailServer(String hostname,String address){
        try {
            Lookup lookup = new Lookup(hostname, Type.MX);
            lookup.run();
            System.out.println("DnsMX:helo from IP/"+InetAddress.getByName(hostname).getHostAddress());
            System.out.println("DnsMX:socket from IP/"+address);
            if (Lookup.SUCCESSFUL != lookup.getResult()) {
            } else {
                System.out.println("A Mail Sever.");
                System.out.println("DnsMX:MX IP/"+InetAddress.getByName(String.valueOf(lookup.getAnswers()[0].getName())).getHostAddress());
                if(address.equals(InetAddress.getByName(String.valueOf(lookup.getAnswers()[0].getName())).getHostAddress()))
                    return true;
            }
        } catch (TextParseException | UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMailServer(String hostname,String address) throws IOException {
        String[] dom = hostname.split(".");
        if(dom.length<2){
            return false;
        }
        //if(!InetAddress.getByName(hostname).isReachable(30)) return false;
        return address.equals(InetAddress.getByName(hostname).getHostAddress());
    }

}
