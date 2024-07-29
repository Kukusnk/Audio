import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WaveAudio implements Audio {
    private AudioFormat audioFormat;
    private byte[] data;

    public void read(File file) throws IOException {
        if (file.length() > 0) {
            FileInputStream waveFile = new FileInputStream(file);
            data = waveFile.readAllBytes();
        }
        if (!isAvailableIdentifier()) {
            System.out.println("Invalid Identifier");
            System.exit(0);
        }
    }

    private int bytesToIntParse(int start, int end) {
        int number, numbers = 0, shift = 0;
        for (int i = start; i < end; i++) {
            number = Byte.toUnsignedInt(data[i]) << shift;
            shift += 8;
            numbers = numbers | number;
        }
        return numbers;
    }

    @Override
    public AudioFormat audioFormat() {

        if (bytesToIntParse(20, 22) == 1) {
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float) bytesToIntParse(24, 28), bytesToIntParse(34, 36),
                    bytesToIntParse(22, 24), bytesToIntParse(28, 32), bytesToIntParse(32, 34), false);
        } else if (bytesToIntParse(20, 22) == 3) {
            audioFormat = new AudioFormat(new AudioFormat.Encoding("IEEE 754"), (float) bytesToIntParse(24, 28), bytesToIntParse(34, 36),
                    bytesToIntParse(22, 24), bytesToIntParse(28, 32), bytesToIntParse(32, 34), false);
        } else {
//            String encoding = "";
//            for (int i = 20; i < 22; i++) {
//                encoding += ((char) data[i]);
//            }
            String encoding = String.valueOf(bytesToIntParse(20, 22));
            audioFormat = new AudioFormat(new AudioFormat.Encoding(encoding), (float) bytesToIntParse(24, 28), bytesToIntParse(34, 36),
                    bytesToIntParse(22, 24), bytesToIntParse(28, 32), bytesToIntParse(32, 34), false);
        }
        return audioFormat;
    }

    private boolean isAvailableIdentifier() {
        String riff = "";
        String wave = "";
        int j = 8;
        for (int i = 0; i < 4 && j < 12; i++, j++) {
            riff += ((char) data[i]);
            wave += ((char) data[j]);
        }
        return riff.equals("RIFF") && wave.equals("WAVE");
    }

    public int getDataSize() {
        return bytesToIntParse(40, 44);
    }

    private String getDataBlockID() {
        String dataBlockID = "";
        for (int i = 36; i < 40; i++) {
            dataBlockID += ((char) data[i]);
        }
        return dataBlockID;
    }

    private int getBlockSize() {
        return bytesToIntParse(16, 20);
    }

    private String getFormatBlockID() {
        String formatBlockID = "";
        for (int i = 12; i < 16; i++) {
            formatBlockID += ((char) data[i]);
        }
        return formatBlockID;
    }

    private int getFileSize() {
        return bytesToIntParse(4, 8);
    }

     public BufferedInputStream plotWithLines(FileInputStream file) throws IOException {
        int bytes, cursor, unsigned;
        BufferedInputStream b = new BufferedInputStream(file);
            byte[] data = new byte[128];
            b.skip(44);
            cursor = 0;
            while ((bytes = b.read(data)) > 0) {
                // do something
                for (int i = 0; i < bytes; i++) {
                    unsigned = data[i] & 0xFF; // Java..
                    System.out.println(cursor + " " + unsigned);
                    cursor++;
                }
            }
            System.out.println("$ javac javaFileName.java\n" +
                    "$ java javaFileName > data.txt\n" +
                    "$ gnuplot\n" +
                    "gnuplot> set size ratio 0.3\n" +
                    "gnuplot> plot \"data.txt\" with lines");
        return b;
    }


//    private void readBitsPerSample() {
//        setWaveHeader(BitsPerSample_2b, String.valueOf(numberParse(34, 36)));
//    }

//    private void readBytePerBlock() {
//        setWaveHeader(BytePerBlock_2b, String.valueOf(numberParse(32, 34)));
//    }

//    private void readBytePerSec() {
//        setWaveHeader(BytePerSec, String.valueOf(numberParse(28, 32)));
//    }

//    private void readFrequence() {
//        setWaveHeader(Frequence, String.valueOf(numberParse(24, 28)));
//    }

//    private void readNbrChannels() {
//        setWaveHeader(NbrChannels_2b, String.valueOf(numberParse(22, 24)));
//    }

//    private void readRiffId() {
//        setWaveHeader(FileTypeBlockID, stringParse(0, 4));
//        str = "";
//    }


//    private void readFileFormatID() {
//        setWaveHeader(FileFormatID, stringParse(8, 12));
//        str = "";
//    }


    @Override
    public byte[] data() {
        return data;
    }
}
