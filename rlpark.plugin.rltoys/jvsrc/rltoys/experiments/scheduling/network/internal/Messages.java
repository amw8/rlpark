package rltoys.experiments.scheduling.network.internal;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class Messages {
  static private boolean verbose = true;
  static private boolean debug = false;
  static byte[] Header = new byte[] { 'D', 'o', 'c' };
  static int HeaderSize = Header.length + Integer.SIZE / Byte.SIZE + Integer.SIZE / Byte.SIZE;

  public enum MessageType {
    Error,
    RequestJob,
    Job,
    RequestClass,
    ClassData
  }

  public static Message cast(MessageBinary message, ClassLoader classLoader) {
    try {
      switch (message.type()) {
      case Error:
        return null;
      case RequestJob:
        return new MessageRequestJob(message);
      case Job:
        return new MessageJob(message, classLoader);
      case RequestClass:
        return new MessageRequestClass(message);
      case ClassData:
        return new MessageClassData(message);
      }
    } catch (IOException e) {
      displayError(e);
      return null;
    }
    return message;
  }

  public static Message readNextMessage(SyncSocket clientSocket) {
    return readNextMessage(clientSocket, null);
  }


  public static Message readNextMessage(SyncSocket clientSocket, ClassLoader classLoader) {
    return cast(clientSocket.read(), classLoader);
  }

  static public void disableVerbose() {
    verbose = false;
  }

  static public void enableDebug() {
    debug = true;
  }

  static public void displayError(Throwable throwable) {
    Class<?>[] classIgnored = new Class<?>[] { EOFException.class, SocketException.class };
    boolean ignored = false;
    for (Class<?> classType : classIgnored)
      if (classType.isInstance(throwable)) {
        ignored = true;
        break;
      }
    if (verbose || !ignored)
      throwable.printStackTrace();
  }

  public static void println(String message) {
    if (verbose)
      System.out.println(message);
  }

  public static void debug(String message) {
    if (debug)
      System.out.println(message);
  }
}
