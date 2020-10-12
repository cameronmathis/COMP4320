import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.util.Arrays;
import java.util.Scanner;
public class UDPClient {

   private static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
   private static final int MAXTRIES = 5;     // Maximum retransmissions

   public static void main(String[] args) throws IOException {
   
      if (args.length != 2)  // Test for correct # of args
         throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
   
      InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server address
   
      int servPort = Integer.parseInt(args[1]);
   
      for(;;) {
         System.out.println("This program computes polynomials in the following format: P(x) = a3*x^3 + a2*x^2 + a1*x + a0\twith 0 <= ai <= 64 and 0 <= x <= 64 for all i 0 <= i <= 3.");
         Scanner scanner = new Scanner(System.in);
         System.out.print("Enter x: ");
         int x = scanner.nextInt();

         if (x < 0 || x > 64) {
            System.out.println("Error, x input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a3: ");
         int a3 = scanner.nextInt();
         if (a3 < 0 || a3 > 64) {
            System.out.println("Error, a3 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a2: ");
         int a2 = scanner.nextInt();
         if (a2 < 0 || a2 > 64) {
            System.out.println("Error, a2 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a1: ");
         int a1 = scanner.nextInt();
         if (a1 < 0 || a1 > 64) {
            System.out.println("Error, a1 input invalid!");
            scanner.close();
            continue;
         }
         System.out.print("Enter a0: ");
         int a0 = scanner.nextInt();
         if (a0 < 0 || a0 > 64) {
            System.out.println("Error, a0 input invalid!");
            scanner.close();
            continue;
         }
         scanner.close();

         // create byte array to send
         byte[] tmlBytes = new byte[1];
         tmlBytes[0] = 9;
         byte[] requestIDBytes = new byte[2];
         requestIDBytes[0] = 1;
         byte[] xBytes = new byte[1];
         xBytes[0] = Byte.parseByte(Integer.toHexString(x), 16);
         byte[] a3Bytes = new byte[1];
         a3Bytes[0] = Byte.parseByte(Integer.toHexString(a3), 16);
         byte[] a2Bytes = new byte[1];
         a2Bytes[0] = Byte.parseByte(Integer.toHexString(a2), 16);
         byte[] a1Bytes = new byte[1];
         a1Bytes[0] = Byte.parseByte(Integer.toHexString(a1), 16);
         byte[] a0Bytes = new byte[1];
         a0Bytes[0] = Byte.parseByte(Integer.toHexString(a0), 16);
         byte[] checksumBytes = new byte[1];
         byte[] bytesToSend = new byte[9];
         System.arraycopy(tmlBytes, 0, bytesToSend, 0, tmlBytes.length);
         System.arraycopy(requestIDBytes, 0, bytesToSend, tmlBytes.length, requestIDBytes.length);
         System.arraycopy(xBytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length, xBytes.length);
         System.arraycopy(a3Bytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length + xBytes.length, a3Bytes.length);
         System.arraycopy(a2Bytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length + xBytes.length + a3Bytes.length, a2Bytes.length);
         System.arraycopy(a1Bytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length + xBytes.length + a3Bytes.length + a2Bytes.length, a1Bytes.length);
         System.arraycopy(a0Bytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length + xBytes.length + a3Bytes.length + a2Bytes.length + a1Bytes.length, a0Bytes.length);
         System.arraycopy(checksumBytes, 0, bytesToSend, tmlBytes.length + requestIDBytes.length + xBytes.length + a3Bytes.length + a2Bytes.length + a1Bytes.length + a0Bytes.length, checksumBytes.length);
         System.out.println(Arrays.toString(bytesToSend));
         DatagramSocket socket = new DatagramSocket();
      
         socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)
         long sendTime = System.nanoTime();
         DatagramPacket sendPacket = new DatagramPacket(bytesToSend,  // Sending packet
            bytesToSend.length, serverAddress, servPort);
      
         DatagramPacket receivePacket =                              // Receiving packet
            new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
      
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
      
         if (receivedResponse)
            System.out.println("Received: " + new String(receivePacket.getData()));
         else
            System.out.println("No response -- giving up.");
         System.out.println("Time elapsed: " + (recTime - sendTime) + " ns");
         
         socket.close();         
      }
   }
}
