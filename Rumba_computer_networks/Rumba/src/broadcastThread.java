import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class broadcastThread extends Thread{

    public DatagramSocket broadcastSocket = null;
    broadcastThread(DatagramSocket socket)
    {
        super();
        this.broadcastSocket = socket;
    }

    public static String data(byte[] bajt) // funkcja tworzaca string z bitow z UDP 
    {
        if (bajt == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (bajt[i] != 0) {
            ret.append((char) bajt[i]);
            i++;
        }
        String return_string = null;
        return_string = ret.toString(); //rzutowanie StringBuildera na string
        return return_string;
    }

    public void run()
    {
        try{
            DatagramSocket broadcastListenerSocket = new DatagramSocket(50000);
            Option.latch.await();   //watek czeka na sygnal wlaczenia (opisany w UserThread)
            while(true)
            {
                byte[] inBuf = new byte[10000];
                byte[] outBuf = new byte[10000];
                DatagramPacket inPacketConn = new DatagramPacket(inBuf, inBuf.length);   
                DatagramPacket outPacketConn = new DatagramPacket(outBuf, outBuf.length); 
                broadcastListenerSocket.receive(inPacketConn);        //odebranie danych

                InetAddress clientAdress = inPacketConn.getAddress();
                int clientPort = inPacketConn.getPort();

                String ifconn[] = data(inBuf).toString().split(" ");    //rozdzielenie odebranych danych

                if(ifconn[0].equals("CB"))   //przyjscie broadcastu
                {  
                    Option.userNumber ++;

                    Option.loginsAll.add(ifconn[1]);    //zapisuje login
        	        Option.TCPportsAll.add(Integer.parseInt(ifconn[2]));      //zapisuje port TCP
                    Option.portsAll.add(clientPort);        //zapisuje port UDP
                    Option.adressAll.add(clientAdress);     //zapisuje adres

                    String useroneall=Option.userNumber+"\t"+ifconn[1]+"\t"+clientAdress+":"+clientPort; // <numer w liscie> <login> <adres> <port>
                    Option.usersAll.add(useroneall);     //zapisuje wszystko
                    
                    String echo = "CBA" + " " + Option.login + " " + Option.TCPport;
                    outBuf = echo.getBytes();
	           		outPacketConn = new DatagramPacket(outBuf, 0, outBuf.length, clientAdress, clientPort); //wyslanie odpowiedzi na broadcast
                    broadcastSocket.send(outPacketConn);
                }
                else if(ifconn[0].equals("CRM")) //rezygnowanie z uslugi (opcja 3)
                {
                    for(int i=0;i<Option.usersAll.size();i++) 
                    {
                        if(Option.usersAll.get(i).toString().contains(String.valueOf(clientAdress)))	//usuwanie obecnego uzytkownika
                        {
                            Option.usersAll.remove(i);
                            Option.portsAll.remove(i);
                            Option.adressAll.remove(i);
                            Option.TCPportsAll.remove(i);
                            Option.loginsAll.remove(i);
                            Option.userNumber --;
                        }	  
                    }    
                }
                else{}
            }
        }
        catch(Exception e){
            System.out.println("BLAD w watku Broadcast \n");
            e.printStackTrace();
        }
    }
}