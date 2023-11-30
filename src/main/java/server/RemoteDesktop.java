package server;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

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
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (!socket.isClosed()) {

                // Recieving request from the client
//                EControlCode request = EControlCode.valueOf(reader.readLine());
                String request = reader.readLine();

                // Check if shutdown
                if (request.equals(EControlCode.SHUTDOWN.toString())) {
                    Runtime.getRuntime().exec("shutdown -s -t 3600");
                    System.out.println("\tComputer will be turned off in 1 hour");
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
                    System.out.println("\tCancel shutdown process");
                    writer.println("\tCancel shutdown process");
                    writer.flush();
                }

                // Take a screenshot using BufferedImage
                // - Robot: capture pixels of screen
                // - BufferedImage: an image object
                // - ImageIO: save image a file PNG
                // - Toolkit.getDefaultToolkit().getSize(): get the size of the screen
                // - 
                else if (request.equals(EControlCode.SCREEENSHOT.toString())) {

                    // Create the rectangle bound for screenshot create the image object then passed into it
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage screenshot = new Robot().createScreenCapture(screenRect);

                    // Create the buffer array containing the image buffered
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    // Convert the image buffer to png image compression
                    ImageIO.write(screenshot, "png", baos);

                    // Write the length of image and write the bytearray to the cliant
                    writer.print(baos);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(RemoteDesktop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException ex) {
            Logger.getLogger(RemoteDesktop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
