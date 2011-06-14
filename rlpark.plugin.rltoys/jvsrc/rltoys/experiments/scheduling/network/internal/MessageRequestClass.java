package rltoys.experiments.scheduling.network.internal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rltoys.experiments.scheduling.network.internal.Messages.MessageType;

public class MessageRequestClass extends Message {
  private final String className;

  public MessageRequestClass(String className) {
    super(MessageType.RequestClass);
    this.className = className;
  }

  protected MessageRequestClass(MessageBinary message) throws IOException {
    super(message);
    className = readClassname(message.contentInputStream());
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeUTF(className);
  }

  private String readClassname(InputStream in) throws IOException {
    return new DataInputStream(in).readUTF();
  }

  public String className() {
    return className;
  }
}
