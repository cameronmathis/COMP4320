import java.io.*;  // for ByteArrayOutputStream and DataOutputStream

public class RequestEncoderBin implements RequestEncoder, RequestBinConst {

  private String encoding;  // Character encoding

  public RequestEncoderBin() {
    encoding = DEFAULT_ENCODING;
  }

  public RequestEncoderBin(String encoding) {
    this.encoding = encoding;
  }

  public byte[] encode(Request request) throws Exception {

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buf);
    out.writeLong(request.ID);
    // Will deal with the lasname at the end
    out.writeShort(request.streetNumber);
    out.writeInt(request.zipCode);
    byte flags = 0;
    if (request.single)
	flags = SINGLE_FLAG;
    if (request.rich)
	flags |= RICH_FLAG;
    if (request.female)
	flags |= FEMALE_FLAG;
    out.writeByte(flags);

    byte[] encodedLastname = request.lastName.getBytes(encoding);
    if (encodedLastname.length > MAX_LASTNAME_LEN)
      throw new IOException("Request lastname exceeds encoded length limit");
    out.writeByte(encodedLastname.length); // provides length of lastname
    out.write(encodedLastname);
    out.flush();
    return buf.toByteArray();
  }
}
