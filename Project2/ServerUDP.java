import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class ServerUDP {
  private static final int ECHOMAX = 9;  // maximum size of datagram packet

  public static void main(String[] args) throws IOException {
    if (args.length != 1)  // test for correct argument list
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int serverPort = Integer.parseInt(args[0]);

    DatagramSocket socket = new DatagramSocket(serverPort);
    RequestDecoder decoder = new RequestDecoderBin();
    ResponseEncoderBin encoder = new ResponseEncoderBin();
    
    System.out.println("Server is running.");

    for (;;) {  // run forever, receiving and echoing datagrams
      DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
      socket.receive(packet);     // receive packet from client

      Request request = decoder.decode(packet);
      byte error_code = 0;
      byte checksum = 0;
      int packetLength  = packet.getLength();
      // check that the checksums match
      if(request.checksum != checksum) {
        error_code = 127;
      }
      // check that byte length recieved is equal to object's TML value
      if(request.tml != packetLength) {
        error_code = 127;
      }

      int x = request.x;
      int a3 = request.a3;
      int a2 = request.a2;
      int a1 = request.a1;
      int a0 = request.a0;
      int opResult = 0;
      
      // perform calculation
      opResult = (a3)*(x*x*x) + (a2)*(x*x) + (a1)*(x) + (a0);

      byte tml = 9;
      Response response = new Response(tml, request.request_id, error_code, opResult, checksum);

      byte[] bin = encoder.encode(response);
      packet.setData(bin);
      socket.send(packet); // send the packet back to client
    }
    /* NOT REACHED */
  }
}
