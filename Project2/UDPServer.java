import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException

public class UDPServer {
  private static final int ECHOMAX = 9;  // Maximum size of datagram packet

  public static void main(String[] args) throws IOException {
    if (args.length != 1)  // Test for correct argument list
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int serverPort = Integer.parseInt(args[0]);

    DatagramSocket socket = new DatagramSocket(serverPort);
    ResultEncoderBin encoder = new ResultEncoderBin();
    ResultDecoder decoder = new ResultDecoderBin();
    
    for (;;) {  // Run forever, receiving and echoing datagrams
      DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
      socket.receive(packet);     // Receive packet from client
      int packetLength  = packet.getLength();

      Request request = decoder.decode(packet);
      byte error_code = 0;

      // Assert that byte length recieved is equal to object's TML value
      if(request.tml != packetLength) {
        error_code = 127;
      }

      int x = request.x;
      int a3 = request.a3;
      int a2 = request.a2;
      int a1 = request.a1;
      int a0 = request.a0;
      long opResult = 0;
      
      // perform calculation
      opResult = (a3)*(x*x*x) + (a2)*(x*x) + (a1)*(x) + (a0); 

      byte tml = 7;
      byte checksum = 0;
      Result result = new Result(tml, request.request_id, error_code, opResult, checksum);

      byte[] bin = encoder.encode(result);
      packet.setData(bin);
      socket.send(packet);       // Send the packet back to client

      socket.close();
    }
    /* NOT REACHED */
  }
}
