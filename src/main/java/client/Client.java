/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.EControlCode;

/**
 *
 * @author duyvu
 */
public class Client {

    public static void main(String[] args) {
        try {
            // Initialize the socket
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 5000));

            // Initialize the output and input objects
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);

            boolean isDisconnect = false;

            while (!isDisconnect) {
                System.out.println("\nMENU: ");
                System.out.println("1. Shutdown");
                System.out.println("2. Restart");
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1: {
                        writer.println(EControlCode.SHUTDOWN);
                        writer.flush();
                        break;
                    }

                    case 2: {
                        writer.println(EControlCode.RESTART);
                        writer.flush();
                        break;
                    }
                    default:
                        throw new AssertionError();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
