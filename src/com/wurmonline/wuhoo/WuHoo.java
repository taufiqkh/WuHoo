package com.wurmonline.wuhoo;

import com.wurmonline.server.webinterface.WebInterface;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Main class
 */
public class WuHoo {
    public static void main(String[] args) {
        try {
            WebInterface webInterface = (WebInterface) java.rmi.Naming.lookup(
                    "//localhost:" + WebInterface.DEFAULT_RMI_PORT + "/WebInterface");
            System.out.println("Game Info:");
            System.out.println(webInterface.getGameInfo());
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
