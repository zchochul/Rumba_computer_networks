import java.net.DatagramSocket;

public class Client{

    public static void main(String[] args) throws Exception {        

        DatagramSocket socket = new DatagramSocket();

        Thread watekTCP = new TCPThread();
        Thread watekBroadcast = new broadcastThread(socket);
        Thread watekUDP = new UDPThread(socket);
        Thread watekClient = new UserThread(socket);
        
        watekClient.start();
        watekTCP.start();
        watekBroadcast.start();
        watekUDP.start();
    }
}