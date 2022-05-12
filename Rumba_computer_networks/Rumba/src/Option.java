import java.io.File;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;

public final class Option {

    public static List<InetAddress> getBroadcastAddrs() throws SocketException
	{
		Set<InetAddress> set = new LinkedHashSet<>();
		Enumeration<NetworkInterface> lista_osob = NetworkInterface.getNetworkInterfaces(); //zwraca wszystkie osoby
		
		for( ; lista_osob.hasMoreElements(); ) {
		NetworkInterface osoba = lista_osob.nextElement();
		if( osoba.isUp() && !osoba.isLoopback() )  { //jezeli osoba jest podpieta i dziala to 
			for( InterfaceAddress interface_address_osoby : osoba.getInterfaceAddresses() )
			set.add( interface_address_osoby.getBroadcast() );
         		}
      		}
      		return Arrays.asList( set.toArray( new InetAddress[0] ) );
    }

    public static InetAddress myAddress;
    public static final CountDownLatch latch = new CountDownLatch(1);
    public static String login, katalog, listaPikow, choosenFile, CBMessage; //katalog (sciezka do udost.)
    public static File fileToSend;
    public static int userNumber = 0;

    static Random rand = new Random();
    public static  int TCPport = rand.nextInt(10000) + 50000;   //losuje port TCP (na tym bedziemy sie laczyc)

    public static void set_Broadcast_add()
    {
    	
        try {
        	if(katalog.indexOf('/')>=0) {
        		myAddress =  getBroadcastAddrs().get(1);
        		System.out.println("Jest to linux");
        	}
        	else	{
        		myAddress =  getBroadcastAddrs().get(0);
        		System.out.println("Jest to windows");
        	}
            //System.out.println(getBroadcastAddrs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static List<String> usersAll = new ArrayList<String>();
    public static List<String> directoriesAll = new ArrayList<String>();
    public static List<String> loginsAll = new ArrayList<String>();
    public static List<Integer> portsAll = new ArrayList<Integer>();
    public static List<InetAddress> adressAll = new ArrayList<InetAddress>();
    public static List<Integer> TCPportsAll = new ArrayList<Integer>();
}