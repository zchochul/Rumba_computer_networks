import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPThread extends Thread {

    public DatagramSocket udpSocket = null;

    
    public UDPThread(DatagramSocket socket)
    {
        super();
        this.udpSocket = socket;
    }

    public static String data(byte[] bajt) // funkcja do tworzy string z bitow UDP
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

    public void run() {
        try{  
            Option.latch.await();   //watek czeka na sygnal

            while(true)
            {  
                byte[] inBuf = new byte[10000];
                byte[] outBuf = new byte[10000];
                DatagramPacket inPacketConn = new DatagramPacket(inBuf, inBuf.length);   
                DatagramPacket outPacketConn = new DatagramPacket(outBuf, outBuf.length); 
                udpSocket.receive(inPacketConn);        //odebranie danych
                InetAddress clientAdress = inPacketConn.getAddress(); //pobiera adres na ktorym nadaje klient
                int clientPort = inPacketConn.getPort(); //pobiera port na ktorym nadaje klient
                
                String ifconn[] = data(inBuf).toString().split(" ");    //rozdzielenie odebranych danych

                
                if(ifconn[0].equals("CR1")) //prosba o wyslanie listy plikow (opcja 2)
                {   
                    File f1=new File(Option.katalog);
	           		File fl[]=f1.listFiles();	
                    int c = 0;
                    StringBuilder sb =  new StringBuilder("CA1 \n");   

	           		for(int i=0; i<fl.length; i++)
                        if(fl[i].canRead())
                            c++;
	
                    sb.append("Znaleziono " + c + " plikow.\n");     
	           					
	           		for(int i=0; i<fl.length; i++) 
	           			sb.append(fl[i].getName()+" "+fl[i].length()+ " Bytes\n");

	           		outBuf = (sb.toString()).getBytes();
	           		outPacketConn = new DatagramPacket(outBuf, 0, outBuf.length, clientAdress, clientPort); //wyslanie plikow dostepnych do pobrania
                    udpSocket.send(outPacketConn);
                }
                else if(ifconn[0].equals("CR2")) //prosba o plik (cd opcji 2)
                {
                    File f1=new File(Option.katalog);
                    File fl[]=f1.listFiles();
                    Boolean canSend = false;
                    int index = 0;

                    for(int i=0; i<fl.length; i++)
                        if(((fl[i].getName()).toString()).equalsIgnoreCase(ifconn[1]))
                        {
                            canSend = true;
                            index = i;
                        }   

                    if(canSend)
                    {
                        Option.fileToSend = fl[index];
                        String message = "CA2" + " " + "OK";
                        outBuf=message.getBytes();										
					    outPacketConn = new DatagramPacket(outBuf, 0, outBuf.length, clientAdress,clientPort); //odeslanie pozwilenia na polaczenie
                        udpSocket.send(outPacketConn);
                    }
                    else
                    {
                        String message = "CA2" + " " + "ERROR";
                        outBuf=message.getBytes();										
					    outPacketConn = new DatagramPacket(outBuf, 0, outBuf.length, clientAdress,clientPort); //odeslanie bledu = pliku albo nie ma albo nie mozna odczytac
                        udpSocket.send(outPacketConn);
                    }
                    ifconn = new String[0]; //zerowanie stringa bo cos sie psuje z nim
                }
                else if(ifconn[0].equals("CBA"))
                {
                    if(ifconn[1].equals(Option.login) && Integer.parseInt(ifconn[2]) == Option.TCPport) //jezeli jestem to ja, ignoruj
                        continue;
                    
                    else
                    {
                        Option.userNumber ++;

                        Option.loginsAll.add(ifconn[1]);    //zapisuje login
                        Option.TCPportsAll.add(Integer.parseInt(ifconn[2]));      //zapisuje port TCP

                        Option.portsAll.add(clientPort);        //zapisuje port UDP
                        Option.adressAll.add(clientAdress);     //zapisuje adres

                        String useroneall=Option.userNumber+"\t"+ifconn[1]+"\t"+clientAdress+":"+clientPort; 
                        Option.usersAll.add(useroneall);     //zapisuje wszystko
                    }
                }
                else if(ifconn[0].equals("CA1")) //odpowiedz z nadeslanymi plikami
                    Option.listaPikow = new String(inPacketConn.getData(), 5, inPacketConn.getLength()); 

                else if(ifconn[0].equals("CA2")) //odpowiedz czy wysle plik
                {
                    if(ifconn[1].equals("OK"))
                        Methods.TCPConnection(Option.TCPportsAll.get(Option.adressAll.indexOf(clientAdress)),clientAdress,Option.choosenFile);  
                    
                    else if(ifconn[1].equals("ERROR"))
                        System.out.println("Blad! \t Nie mozna otrzymac zadanego pliku!");  
                }
                else {}
            }
        }
        catch (Exception e)
        {
            System.out.println("Coś zepsuło się w wątku UDP \n");
            e.printStackTrace();
        }
    }
    
}