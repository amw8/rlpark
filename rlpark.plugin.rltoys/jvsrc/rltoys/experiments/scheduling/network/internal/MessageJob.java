package rltoys.experiments.scheduling.network.internal;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import rltoys.experiments.scheduling.network.internal.Messages.MessageType;

public class MessageJob extends Message {
  private final Serializable job;
  private final int jobId;

  public MessageJob(int jobId, Runnable job) {
    super(MessageType.Job);
    this.job = (Serializable) job;
    this.jobId = jobId;
  }

  protected MessageJob(MessageBinary message, ClassLoader classLoader) throws IOException {
    super(message);
    InputStream in = message.contentInputStream();
    jobId = readJobId(in);
    job = !isJobNull() ? readJob(in, classLoader) : null;
  }

  private boolean isJobNull() {
    return jobId < 0;
  }

  @Override
  protected void writeContentBuffer(ByteArrayOutputStream out) throws IOException {
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeInt(jobId);
    if (isJobNull())
      return;
    ObjectOutputStream objOut = new ObjectOutputStream(out);
    objOut.writeObject(job);
  }

  private int readJobId(InputStream in) throws IOException {
    return new DataInputStream(in).readInt();
  }

  private Serializable readJob(InputStream in, ClassLoader classLoader) throws IOException {
    ObjectInputStream objIn = NetworkClassLoader.createObjectInputStream(in, classLoader);
    Serializable job = null;
    try {
      job = (Serializable) objIn.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return job;
  }

  public Runnable job() {
    return (Runnable) job;
  }

  public int id() {
    return jobId;
  }

  public static Message nullJob() {
    return new MessageJob(-1, null);
  }
}
