import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Methods {

    public static void TCPConnection(int port,InetAddress addr,String fileName) //sciaganie pliku
    {
        int bytesRead;
		int current = 0;
		FileOutputStream file_output = null;
		BufferedOutputStream buffer_output = null;
		Socket sock = null;
        try{
            sock = new Socket(addr,port);
			final byte[] bytearr = new byte[6022386];
			final InputStream input_stream = sock.getInputStream();
			file_output = new FileOutputStream(fileName);
			buffer_output = new BufferedOutputStream(file_output);
			bytesRead = input_stream.read(bytearr,0,bytearr.length);
			current = bytesRead; //pointer

			do {
				bytesRead =
					input_stream.read(bytearr, current, (bytearr.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} while(bytesRead > -1); //brak bajtow do przeczytania

			buffer_output.write(bytearr, 0 , current);
			buffer_output.flush();
            System.out.println("Plik " + fileName + " zostal pobrany (" + current + " bytes read)");  
        }    
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {   //finally wykona sie po try, nawet jak wylapie wyjatek
            try{
                if (file_output != null) file_output.close();
                if (buffer_output != null) buffer_output.close();
                if (sock != null) sock.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            Option.choosenFile = null;
        }
    }

    public static void sendBroadcast(DatagramSocket socket, String message)
    {
        try{
            byte[] outBuf = new byte[(int)message.length()];
            DatagramPacket	packet = null;									
			outBuf=message.getBytes();										
            
			//System.out.println(Option.myAddress);
			packet = new DatagramPacket(outBuf, 0, outBuf.length, Option.myAddress,50000); //wyslanie broadacstu na port 50 000, bo nie jest zajety przez IANA
            socket.send(packet);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
}