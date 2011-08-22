package rltoys.experiments.scheduling.internal.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rltoys.experiments.scheduling.internal.messages.Messages.MessageType;

public class MessageRequestJob extends Message {
  private final boolean blocking;

  public MessageRequestJob(boolean blocking) {
    super(MessageType.RequestJob);
    this.blocking = blocking;
  }

  protected MessageRequestJob(MessageBinary message) throws IOException {
    super(message);
    blocking = readBlocking(message.contentInputStream());
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeBoolean(blocking);
  }

  private boolean readBlocking(InputStream in) throws IOException {
    return new DataInputStream(in).readBoolean();
  }

  public boolean blocking() {
    return blocking;
  }
}
