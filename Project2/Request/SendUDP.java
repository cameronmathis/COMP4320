import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException

public class SendUDP {

  public static void main(String args[]) throws Exception {

      if (args.length != 2 && args.length != 3)  // Test for correct # of args        
	      throw new IllegalArgumentException("Parameter(s): <Destination>" +
					     " <Port> [<encoding]");
      
      
      InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
      int destPort = Integer.parseInt(args[1]);               // Destination port
      
      Request request = new Request(1234567890987654L, "Alice Adams",
				 (short) 777, 90007, true, true, false);
      
      DatagramSocket sock = new DatagramSocket(); // UDP socket for sending
      
      
      // Use the encoding scheme given on the command line (args[2])
      RequestEncoder encoder = (args.length == 3 ?
				  new RequestEncoderBin(args[2]) :
				  new RequestEncoderBin());
      

      byte[] codedRequest = encoder.encode(request); // Encode Request
      
      DatagramPacket message = new DatagramPacket(codedRequest, codedRequest.length, 
						  destAddr, destPort);
      sock.send(message);
      
      sock.close();
  }
}
