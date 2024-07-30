package Parser.audio;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WaveAudio implements Audio {

    class HeaderRange {
        public int startByte;
        public int lastByte;

        public HeaderRange(int startByte, int lastByte) {
            this.startByte = startByte;
            this.lastByte = lastByte;
        }
    }


    private final HeaderRange HEADER_ENCODING_BYTE_RANGE = new HeaderRange(20, 22);
    private final HeaderRange HEADER_SAMPLE_RATE_BYTE_RANGE = new HeaderRange(24, 28);
    private final HeaderRange HEADER_SAMPLE_SIZE_IN_BITS_RANGE = new HeaderRange(34, 36);
    private final HeaderRange HEADER_CHANNELS_BYTE_RANGE = new HeaderRange(22, 24);
    private final HeaderRange HEADER_FRAME_SIZE_RANGE = new HeaderRange(32, 34);
    private final HeaderRange HEADER_FRAME_RATE_RANGE = new HeaderRange(28, 32);

    private final HeaderRange HEADER_RIFF_BYTE_RANGE = new HeaderRange(0, 4);
    private final HeaderRange HEADER_WAVE_BYTE_RANGE = new HeaderRange(8, 12);
    private final HeaderRange HEADER_DATA_SIZE_RANGE = new HeaderRange(40, 44);
    private final HeaderRange HEADER_BLOCK_SIZE_RANGE = new HeaderRange(16, 20);
    private final HeaderRange HEADER_FILE_SIZE_RANGE = new HeaderRange(4, 8);
    private final HeaderRange HEADER_DATA_BLOCK_ID_RANGE = new HeaderRange(36, 40);
    private final HeaderRange HEADER_FORMAT_BLOCK_ID_RANGE = new HeaderRange(12, 16);


    private AudioFormat audioFormat;
    private byte[] audioData;
    private byte[] headerData;

    @Override
    public AudioFormat audioFormat() {
        return audioFormat;
    }

    public void read(File file) throws IOException {
        if (file.length() > 0) {
            FileInputStream waveFile = new FileInputStream(file);
            headerData = waveFile.readNBytes(44);
            waveFile.skipNBytes(44);
            audioData = waveFile.readAllBytes();
            waveFile.close();
        }
        if (!isAvailableIdentifier()) {
            System.out.println("Invalid Identifier");
            System.exit(0);
        }
        audioFormat = parseAudioFormat();
    }

    private int getIntByRange(HeaderRange range) {
        int number, numbers = 0, shift = 0;
        for (int i = range.startByte; i < range.lastByte; i++) {
            number = Byte.toUnsignedInt(headerData[i]) << shift;
            shift += 8;
            numbers = numbers | number;
        }
        return numbers;
    }

    private AudioFormat parseAudioFormat() {

        AudioFormat af;
        if (getIntByRange(HEADER_ENCODING_BYTE_RANGE) == 1) {
            af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) getIntByRange(HEADER_SAMPLE_RATE_BYTE_RANGE), /*BitsPerSample*/ getIntByRange(HEADER_SAMPLE_SIZE_IN_BITS_RANGE),
                    getIntByRange(HEADER_CHANNELS_BYTE_RANGE), /*BytePerBloc*/ getIntByRange(HEADER_FRAME_SIZE_RANGE), /*BytePerSec*/ getIntByRange(HEADER_FRAME_RATE_RANGE), true);
        } else if (getIntByRange(HEADER_ENCODING_BYTE_RANGE) == 3) {
            af = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float) getIntByRange(HEADER_SAMPLE_RATE_BYTE_RANGE), /*BitsPerSample*/ getIntByRange(HEADER_SAMPLE_SIZE_IN_BITS_RANGE),
                    getIntByRange(HEADER_CHANNELS_BYTE_RANGE), /*BytePerBloc*/ getIntByRange(HEADER_FRAME_SIZE_RANGE), /*BytePerSec*/ getIntByRange(HEADER_FRAME_RATE_RANGE), true);
        } else {
            String encoding = String.valueOf(getIntByRange(HEADER_ENCODING_BYTE_RANGE));
            af = new AudioFormat(new AudioFormat.Encoding(encoding), (float) getIntByRange(HEADER_SAMPLE_RATE_BYTE_RANGE), /*BitsPerSample*/ getIntByRange(HEADER_SAMPLE_SIZE_IN_BITS_RANGE),
                    getIntByRange(HEADER_CHANNELS_BYTE_RANGE), /*BytePerBloc*/ getIntByRange(HEADER_FRAME_SIZE_RANGE), /*BytePerSec*/ getIntByRange(HEADER_FRAME_RATE_RANGE), true);
        }
        return af;
    }

    private boolean isAvailableIdentifier() {
        String riff = "";
        String wave = "";
        int j = HEADER_WAVE_BYTE_RANGE.startByte;
        for (int i = HEADER_RIFF_BYTE_RANGE.startByte; i < HEADER_RIFF_BYTE_RANGE.lastByte
                && j < HEADER_WAVE_BYTE_RANGE.lastByte; i++, j++) {
            riff += ((char) headerData[i]);
            wave += ((char) headerData[j]);
        }
        return riff.equals("RIFF") && wave.equals("WAVE");
    }

    public int getDataSize() {
        return getIntByRange(HEADER_DATA_SIZE_RANGE);
    }

    public String getDataBlockID() {
        String dataBlockID = "";
        for (int i = HEADER_DATA_BLOCK_ID_RANGE.startByte; i < HEADER_DATA_BLOCK_ID_RANGE.lastByte; i++) {
            dataBlockID += ((char) headerData[i]);
        }
        return dataBlockID;
    }

    public int getBlockSize() {
        return getIntByRange(HEADER_BLOCK_SIZE_RANGE);
    }

    public String getFormatBlockID() {
        String formatBlockID = "";
        for (int i = HEADER_FORMAT_BLOCK_ID_RANGE.startByte; i < HEADER_FORMAT_BLOCK_ID_RANGE.lastByte; i++) {
            formatBlockID += ((char) headerData[i]);
        }
        return formatBlockID;
    }

    public int getFileSize() {
        return getIntByRange(HEADER_FILE_SIZE_RANGE);
    }

    public void plotWithLines(File file) {
        int cursor = 0, unsigned;

//        try {
//            FileInputStream s = new FileInputStream(file);
//            BufferedInputStream b = new BufferedInputStream(s);
//            byte[] data = new byte[128];
//            b.skip(44);
//            while ((bytes = b.read(data)) > 0) {
        // do something
        for (byte audioDatum : audioData) {
            unsigned = audioDatum & 0xFF;
            System.out.println(cursor + " " + unsigned);
            cursor++;
        }
        // }
//            System.out.println("$ javac javaFileName.java\n" +
//                    "$ java javaFileName > data.txt\n" +
//                    "$ gnuplot\n" +
//                    "gnuplot> set size ratio 0.3\n" +
//                    "gnuplot> plot \"data.txt\" with lines");
//            b.read(data);
//            b.close();
//        } catch (IOException e) {
//            System.out.println("Yes");
//        }


    }

    @Override
    public byte[] data() {
        return headerData;
    }

    public byte[] audioData() {
        return audioData;
    }
}
