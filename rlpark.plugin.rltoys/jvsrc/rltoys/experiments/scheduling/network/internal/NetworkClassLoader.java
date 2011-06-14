package rltoys.experiments.scheduling.network.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;


public class NetworkClassLoader extends ClassLoader {
  private final static Map<String, Class<?>> cache = new HashMap<String, Class<?>>();
  private static int downloaded = 0;

  static boolean forceNetworkClassResolution = false;
  private final SyncSocket socket;

  public NetworkClassLoader(SyncSocket socket) {
    this.socket = socket;
  }

  @Override
  public Class<?> findClass(String name) {
    Class<?> result = cache.get(name);
    if (result != null)
      return result;
    try {
      MessageClassData messageClassData = socket.classTransaction(name);
      byte[] classData = messageClassData.classData();
      downloaded += classData.length;
      result = defineClass(name, classData, 0, classData.length, getClass().getProtectionDomain());
      cache.put(name, result);
      return result;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void enableForceNetworkClassResolution() {
    forceNetworkClassResolution = true;
  }

  public static ObjectInputStream createObjectInputStream(InputStream in, final ClassLoader classLoader)
      throws IOException {
    if (classLoader == null)
      return new ObjectInputStream(in);
    return new ObjectInputStream(in) {
      @Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();
        if (forceNetworkClassResolution && debuggingNameCorrect(className))
          return ((NetworkClassLoader) classLoader).findClass(className);
        try {
          return Class.forName(className);
        } catch (ClassNotFoundException exc) {
          try {
            return classLoader.loadClass(className);
          } catch (ClassNotFoundException e) {
          }
        }
        throw new ClassNotFoundException(className);
      }

      private boolean debuggingNameCorrect(String className) {
        if (className.startsWith("java."))
          return false;
        return className.contains("$");
      }
    };
  }

  static public NetworkClassLoader newClassLoader(final SyncSocket socket) {
    return AccessController.doPrivileged(new PrivilegedAction<NetworkClassLoader>() {
      @Override
      public NetworkClassLoader run() {
        return new NetworkClassLoader(socket);
      }
    });
  }

  static public int downloaded() {
    return downloaded;
  }
}
