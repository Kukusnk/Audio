import javax.sound.sampled.AudioFormat;

public interface Audio {

    AudioFormat audioFormat();

    byte[] data();
}

