public class Operation {

    public byte tml;
    public byte request_id;
    public byte op_code;
    public byte num_operands;
    public short operand1;
    public short operand2;
  
    public Operation(byte tml, byte request_id, byte op_code, byte num_operands, short operand1, short operand2)  {
      this.tml = tml;
      this.request_id = request_id;
      this.op_code = op_code;
      this.num_operands = num_operands;
      this.operand1 = operand1;
      this.operand2 = operand2;
    }
  
    public String toString() {
      final String EOLN = java.lang.System.getProperty("line.separator");
      String value = "TML        = " + tml + EOLN +
                     "request id = " + request_id + EOLN +
                     "op code    = " + op_code + EOLN +
                     "num ops    = " + num_operands + EOLN +
                     "operand 1  = " + operand1 + EOLN +
                     "operand 2  = " + operand2 + EOLN;
      return value;
    }
  }
