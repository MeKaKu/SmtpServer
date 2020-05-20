package smtpserver;

import smtpserver.service.Communication;
import smtpserver.service.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmtpServerRunner {
    public static void main(String[] args) {
        try {

            Server server = new Server(25, "diker.xyz");
            Communication communication = new Communication(2525);
            new Thread(server).start();
            new Thread(communication).start();

        } catch (IOException ex) {
            Logger.getLogger(SmtpServerRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
