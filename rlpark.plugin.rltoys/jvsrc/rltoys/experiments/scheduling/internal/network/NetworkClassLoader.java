package rltoys.experiments.scheduling.internal.network;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import rltoys.experiments.scheduling.internal.messages.ClassLoading.NetworkFindClass;
import rltoys.experiments.scheduling.internal.messages.MessageClassData;


public class NetworkClassLoader extends ClassLoader implements NetworkFindClass {
  private final SyncSocket socket;
  private final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

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
      result = defineClass(name, classData, 0, classData.length, getClass().getProtectionDomain());
      cache.put(name, result);
      return result;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  static public NetworkClassLoader newClassLoader(final SyncSocket socket) {
    return AccessController.doPrivileged(new PrivilegedAction<NetworkClassLoader>() {
      @Override
      public NetworkClassLoader run() {
        return new NetworkClassLoader(socket);
      }
    });
  }
}
