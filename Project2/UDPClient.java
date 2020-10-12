import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random; 

public class UDPClient {
   private static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
   private static final int MAXTRIES = 5;     // Maximum retransmissions

   public static void main(String[] args) throws IOException {
      if (args.length != 2)  // Test for correct # of args
         throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
   
      InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server address
   
      int servPort = Integer.parseInt(args[1]);

      Random random = new Random(); 
      int request_id = random.nextInt(128);

      ResponseEncoder encoder = new ResponseEncoderBin();
      ResponseDecoder decoder = new ResponseDecoderBin();

      for(;;) {
         System.out.println("This program computes polynomials in the following format: P(x) = a3*x^3 + a2*x^2 + a1*x + a0\twith 0 <= ai <= 64 and 0 <= x <= 64 for all i 0 <= i <= 3.");
         Scanner scanner = new Scanner(System.in);
         System.out.print("Enter x: ");
         int x = scanner.nextByte();
         if (x < 0 || x > 64) {
            System.out.println("Error, x input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a3: ");
         int a3 = scanner.nextByte();
         if (a3 < 0 || a3 > 64) {
            System.out.println("Error, a3 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a2: ");
         int a2 = scanner.nextByte();
         if (a2 < 0 || a2 > 64) {
            System.out.println("Error, a2 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a1: ");
         int a1 = scanner.nextByte();
         if (a1 < 0 || a1 > 64) {
            System.out.println("Error, a1 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a0: ");
         int a0 = scanner.nextByte();
         if (a0 < 0 || a0 > 64) {
            System.out.println("Error, a0 input invalid!");
            scanner.close();
            continue;
         }
         scanner.close();

         byte tml = 9;
         byte checksum = 0;
         Request request = new Request(tml, (short)request_id, (byte)x, (byte)a3, (byte)a2, (byte)a1, (byte)a0, (byte)checksum);

         request_id = (request_id + 1) % 128;

         
         byte[] bytesToSend = encoder.encode(request);
         DatagramSocket socket = new DatagramSocket();
      
         socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)
         long sendTime = System.nanoTime();
         DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, serverAddress, servPort); // sending packet
      
         DatagramPacket receivePacket = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length); // recieving packet
      
         int tries = 0;      // Packets may be lost, so we have to keep trying
         boolean receivedResponse = false;
         do {
            socket.send(sendPacket);          // Send the echo string
            try {
               socket.receive(receivePacket);  // Attempt echo reply reception
            
               if (!receivePacket.getAddress().equals(serverAddress))  // Check source
                  throw new IOException("Received packet from an unknown source");
            
               receivedResponse = true;
            } catch (InterruptedIOException e) {  // We did not get anything
               tries += 1;
               System.out.println("Timed out, " + (MAXTRIES-tries) + " more tries...");
            }
         } while ((!receivedResponse) && (tries < MAXTRIES));
         long recTime = System.nanoTime();
      
         if (receivedResponse) {
            Response response = decoder.decode(receivePacket);
            
            byte[] bytes = receivePacket.getData();
         
            System.out.println("Sent Packet    : " + new String(hexChars(bytesToSend, ttl)));
            System.out.println("Received Packet: " + new String(hexChars(bytes, response.tml)));
            System.out.println("Request ID is: " + response.request_id);
            System.out.println("The result is: " + response.result);
         } else {
            System.out.println("No response -- giving up.");
         }
         System.out.println("Time elapsed: " + (recTime - sendTime) + " ns");
         
         socket.close();         
      }
   }

   private static char[] hexChars(byte[] bytes, int length_in) {
      char [] hexChars = new char[length_in * 2];
      char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
      for (int j = 0; j < length_in; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARS[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
      }
      return hexChars;
   }
}
