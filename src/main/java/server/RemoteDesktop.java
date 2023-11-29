package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author duyvu
 */
public class RemoteDesktop {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 5000));

            // Connect to all clients
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted: " + socket.getInetAddress().getHostAddress());

                // Handle request thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleClientRequest(socket);
                    }
                }).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleClientRequest(Socket socket) {
        try {
            // Initialize the output and input objects
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (!socket.isClosed()) {

                // Recieving request from the client
//                EControlCode request = EControlCode.valueOf(reader.readLine());
                String request = reader.readLine();

                // Check if shutdown
                if (request.equals(EControlCode.SHUTDOWN.toString())) {
                    Runtime.getRuntime().exec("shutdown -s -t 3600");
                    writer.println("\tComputer will be turned off in 1 hour");
                    writer.flush();
                }

                // Check if restart
                else if (request.equals(EControlCode.RESTART.toString())) {
                    Runtime.getRuntime().exec("shutdown -r -t 3600");
                    System.out.println("\tComputer will be restarted in 1 hour");
                    writer.println("\tComputer will be restarted in 1 hour");
                    writer.flush();
                }

                else if (request.equals(EControlCode.EXITSHUTDOWN.toString())) {
                    Runtime.getRuntime().exec("shutdown -a");
                    System.out.println("\tComputer will be restarted in 1 hour");
                    writer.println("\tCancel shutdown process");
                    writer.flush();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(RemoteDesktop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
