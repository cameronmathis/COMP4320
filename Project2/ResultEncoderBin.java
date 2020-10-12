import java.io.*;  // for ByteArrayOutputStream and DataOutputStream

public class ResultEncoderBin {
    
    public byte[] encode(Request request) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeByte(request.tml);
        out.writeByte(request.request_id);
        out.writeByte(request.x);
        out.writeByte(request.a3);
        out.writeShort(request.a2);
        out.writeShort(request.a1);
        out.writeShort(request.a0);
        out.writeShort(request.checksum);
    
        out.flush();
        return buf.toByteArray();
    }
}
