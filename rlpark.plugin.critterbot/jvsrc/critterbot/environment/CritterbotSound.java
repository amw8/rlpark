package critterbot.environment;

import java.io.IOException;

import rlpark.plugin.robot.Robots;
import rlpark.plugin.robot.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.drops.DropArray;
import rlpark.plugin.robot.disco.drops.DropData;
import rlpark.plugin.robot.disco.io.DiscoPacket;
import rlpark.plugin.robot.disco.io.DiscoSocket;
import rlpark.plugin.robot.sync.ObservationReceiver;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;

public class CritterbotSound implements ObservationReceiver {
  private final String hostname;
  private final int port;
  private DiscoSocket socket;
  public static final int NbMicrophoneBin = 9;
  private final Drop soundFFTDrop = new Drop("FftAudioDrop", new DropData[] {
      new DropArray(CritterbotDrops.MicrophoneFFT + "Left", NbMicrophoneBin),
      new DropArray(CritterbotDrops.MicrophoneFFT + "Right", NbMicrophoneBin)
  });
  private final DropScalarGroup soundData = new DropScalarGroup(soundFFTDrop);

  protected CritterbotSound(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  @Override
  public void initialize() {
    try {
      socket = new DiscoSocket(hostname, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ObservationVersatile waitForData() {
    DiscoPacket packet = null;
    try {
      packet = socket.recv();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return packet != null ? Robots.createObservation(System.currentTimeMillis(), packet.byteBuffer(), soundData) : null;
  }

  @Override
  public boolean isClosed() {
    return socket == null || socket.isSocketClosed();
  }

  public Legend legend() {
    return soundData.legend();
  }

  @Override
  public int packetSize() {
    return soundFFTDrop.dataSize();
  }
}
