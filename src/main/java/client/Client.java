/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            boolean isDisconnect = false;

            while (!isDisconnect) {

                System.out.println("\nMENU: ");
                System.out.println("1. Shutdown");
                System.out.println("2. Restart");
                System.out.println("3. Turn off shutdown");
                System.out.println("4. Screenshot");
                System.out.println("[0]. Disconnect the server");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 0: {
                        socket.close();
                        break;
                    }
                    case 1: {
                        writer.println(EControlCode.SHUTDOWN);
                        System.out.println(reader.readLine());
                        break;
                    }

                    case 2: {
                        writer.println(EControlCode.RESTART);
                        System.out.println(reader.readLine());
                        break;
                    }

                    case 3: {
                        writer.println(EControlCode.EXITSHUTDOWN);
                        System.out.println(reader.readLine());
                        break;
                    }

                    case 4: {
                        writer.println(EControlCode.SCREEENSHOT);

                        // Read Image length
                        int imageSize = Integer.parseInt(reader.readLine());

                        // Read Image bytes
                        byte[] imageBytes = new byte[imageSize];

                        // Change from read() tro readNBytes()
                        // read does not guarantee to read the whole array byte
                        int byteRead = socket.getInputStream().readNBytes(imageBytes, 0, imageSize);

                        // Check if the image has been read or not
                        // Then save to the folder
                        if (byteRead > 0) {
                            System.out.print("Save image as a name: ");
                            String imageName = sc.nextLine();

                            // Locate the image directory
                            Path imagePath = Paths.get("./src/main/java/client/images/" + imageName + ".png").toAbsolutePath();

                            Files.write(imagePath, imageBytes);
                            System.out.println("Saving image successfully");
                        }
                        break;
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
