public class MessageInfo {
  public String message;
  public byte messageStatus;
  
  /* this somewhat weird looking method is a constructor btw */
  public MessageInfo(String message, byte messageStatus) {
    this.message = message;
    this.messageStatus = messageStatus;
  }

  public static MessageInfo incoming(String text) {
    return new MessageInfo(text, (byte) 0);
  }

  public static MessageInfo sending(String text) {
    return new MessageInfo(text, (byte) -1);
  }

  public String toString() {return message;}
}
