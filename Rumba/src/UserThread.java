import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;


public class UserThread extends Thread{

	private static Scanner src;
	public DatagramSocket userSocket = null;

	public UserThread(DatagramSocket socket)
    {
        super();
        this.userSocket = socket;
    }

    public void run()
    {	
		int wyborInt;
		src = new Scanner(System.in);
        try{
			DatagramPacket outPacket = null;
			byte[] outBuf;

     		System.out.println("~~~~~RUMBA~~~~~");
     		System.out.println("~~~~~MK & ZCH~~~~~");
     		
     		System.out.println("Podaj swoj login:");
     		Option.login=src.nextLine(); //pobiera login
     		
			System.out.println("Podaj pelna sciezke do katalogu, ktory chcesz udostepnic innym");
			System.out.println("np.:");
			System.out.println("WINDOWS: C:\\Users\\Nazwa_uzytkownika\\ ");
			System.out.println("LINUKS:  /home/nazwa_uzytkownika/ ");
			Option.katalog=src.nextLine(); //pobiera sciezke
												 
			Option.latch.countDown(); //sygnal dla innych watkow, zeby ruszyly
			Option.set_Broadcast_add();
			Option.CBMessage = "CB" +" "+ Option.login +" "+ Option.TCPport; //wiadomosc do broadcastu tworzona	(kazdy klient, ktory dostanie taki komunikat dodaje do listy klientow te osobek)				
			Methods.sendBroadcast(userSocket, Option.CBMessage);	//wyslanie broadcastu sendBroadcast(socket, message)
			
			
        	while(true) 
        	{
	          //------------------------MENU----------------------------------------------------
				System.out.println("MENU:");
				System.out.println("1 - Liczba Klientow");
				System.out.println("2 - Pobranie pliku od wybranego klienta");
				System.out.println("3 - Wyjscie");
				System.out.println("Wybierz opcje:");			
				
				wyborInt=src.nextInt();   //pobieranie wyboru
				
				if(wyborInt==1) //wyswietlanie liczby uzytkownikow
				{
					System.out.println("Obecnie jest " + Option.loginsAll.size() + " zalogowanych uzytkownikow.");

					for(int i = 0; i < Option.loginsAll.size(); i++)
						System.out.println(i + ". " +Option.loginsAll.get(i)); 	//tworzy liste uzytkownikow 1. User1
				}				
				else if(wyborInt==2) //pobieranie pliku uzytkownika
				{
					System.out.println("Wybierz numer uzytkownika, od ktorego chcesz pobrac plik:");	
					for(int i = 0; i < Option.usersAll.size(); i++)
						System.out.println(Option.usersAll.get(i)); 	//podaje i-tego uzytkownika, np. 1. User1 \127.0.0.1:69420, zeby sprawdzic czy wszystko dziala poprawnie
					
					Option.userNumber = src.nextInt();  //WAZNE pobieramy numer uzytkownika od ktorego bedziemy brac pliki

					String message = "CR1" + " ";	
					outBuf = new byte[10000];																 
					outBuf=message.getBytes();										
					outPacket = new DatagramPacket(outBuf, 0, outBuf.length, Option.adressAll.get(Option.userNumber - 1),Option.portsAll.get(Option.userNumber - 1)); //wyslanie zapytania o katalog
					userSocket.send(outPacket);
					outBuf = new byte[10000];

					System.out.println("Pobieram dane...");     
					sleep(2000);	//w przypadku mniejszego opoznienia - bledy

					System.out.println(Option.listaPikow); // wyswietla liste plikow
					System.out.println("Podaj nazwe pliku, ktory chcesz pobrac:");

					Option.choosenFile = src.nextLine();	//2 razy bo jak jest 1 to od razu pobiera cos z klawiatury
					Option.choosenFile = src.nextLine(); 	//WAZNE nazwa pliku
					message = "CR2" + " " + Option.choosenFile;

					outBuf=message.getBytes();										
					outPacket = new DatagramPacket(outBuf, 0, outBuf.length, Option.adressAll.get(Option.userNumber - 1),Option.portsAll.get(Option.userNumber - 1)); //wyslanie zapytania z katalogiem
					userSocket.send(outPacket);
					sleep(1000); //powstrzymanie menu przed automatycznym uruchomieniem (powinna tu byc zmienna ktora pozwala zaczac kolejna iteracje po przeslaniu)
				} 
				else if(wyborInt==3)
				{
					System.out.println("Zegnaj!");
					String msg = "CRM"+" "+"EXIT";
					Methods.sendBroadcast(userSocket, msg);	//komuniakt o zakonczeniu akcji
					System.exit(0);
				}
				else
				{
					System.out.println("Niepoprawna wartosc!");
				}
        	} 
        }
        catch (Exception e)
        {
        	System.out.println("Blad watku USER \n");
            e.printStackTrace();
        }
    }
    
}