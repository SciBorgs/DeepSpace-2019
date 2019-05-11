package frc.robot.helpers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

// FILE HAS NOT BEEN CLEANED UP //
public class Client {
    private static final String SERVER_IP_ADDRESS = "server3"; // set to drive computer static ip/network name
    private static final int SERVER_PORT = 5803;
    private static Socket socket;
    private static ObjectOutputStream objectOutputStream;

    public Client() throws IOException {
        socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public boolean sendPoints(Point[] points) throws IOException {
        try {
            objectOutputStream.writeObject(points);
            return true;
        } catch (SocketException e) {
            System.out.println("Robot disconnected from server, reconnecting...");
            socket.close();
            socket = new Socket(SERVER_IP_ADDRESS, SERVER_PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            return false;
        }
    }
}
