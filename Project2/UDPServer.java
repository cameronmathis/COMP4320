import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException

public class UDPServer {

  private static final int ECHOMAX = 9;  // Maximum size of datagram packet

  public static void main(String[] args) throws IOException {

    if (args.length != 1)  // Test for correct argument list
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int serverPort = Integer.parseInt(args[0]);

    DatagramSocket socket = new DatagramSocket(serverPort);
    DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
    
    for (;;) {  // Run forever, receiving and echoing datagrams
      socket.receive(packet);     // Receive packet from client
      byte[] recievedPacket = packet.getData();
      int packetLength  = packet.getLength();
      int TML = 9;

      // Assert that byte length recieved is equal to object's TML value
      if(TML != packetLength) {
        System.out.println("Length wrong!");
      }

      int x = 0;
      int a0 = 0;
      int a1 = 0;
      int a2 = 0;
      int a3 = 0;
      long result;
      boolean isError = false;
      

      socket.send(packet);       // Send the same packet back to client
      packet.setLength(ECHOMAX); // Reset length to avoid shrinking buffer

      socket.close();
    }
    /* NOT REACHED */
  }
}