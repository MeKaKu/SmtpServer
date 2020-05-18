package smtpserver;

import smtpserver.service.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmtpServerRunner {
    public static void main(String[] args) {
        try {

            Server server = new Server(25, "diker.xyz");
            new Thread(server).start();

        } catch (IOException ex) {
            Logger.getLogger(SmtpServerRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
