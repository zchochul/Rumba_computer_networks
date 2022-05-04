
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class TCPThread extends Thread {

    public TCPThread()
    {
        super();
    }

    public void run()
    {
        byte[] outBuf;
        FileInputStream file_input = null;
		BufferedInputStream buffer_input = null;
        OutputStream output_stream = null;
        Socket socketTCP = null;
        ServerSocket servSocketTCP = null;
        try {
            servSocketTCP = new ServerSocket(Option.TCPport);
            Option.latch.await();   //watek czeka na sygnal
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true)
        {
            try {			   
                socketTCP = servSocketTCP.accept();
                File ff = new File(Option.fileToSend.getAbsolutePath());
    
                outBuf  = new byte [(int)ff.length()];
                file_input = new FileInputStream(ff);
                buffer_input = new BufferedInputStream(file_input);
                buffer_input.read(outBuf,0,outBuf.length);
                output_stream = socketTCP.getOutputStream();
                output_stream.write(outBuf,0,outBuf.length);

                output_stream.flush();
                outBuf = null;
                socketTCP.close();
                buffer_input.close();
            }
            catch(IOException ioe) {
                System.out.println("BLAD watku TCP \n");
                System.out.println(ioe);
            }
        }  
    }
    
}
