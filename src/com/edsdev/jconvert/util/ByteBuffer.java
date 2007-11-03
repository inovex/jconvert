package com.edsdev.jconvert.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class ByteBuffer {

    private byte[] data;

    private int length;

    private CRC32 crcCalc = new CRC32();

    private int crc;

    public ByteBuffer(byte[] inData, int inLength) {
        data = inData;
        length = inLength;

        crcCalc.reset();
        crcCalc.update(data);
        crc = (int) crcCalc.getValue();
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public int getCRC() {
        return crc;
    }

    public void save(String fileName) {
        File fp = new File(fileName);

        if (fp.exists()) {
            File oldFP = new File(fileName + "~");
            if (oldFP.exists()) {
                oldFP.delete();
            }
            fp.renameTo(oldFP);
        }

        try {
            FileOutputStream fos = new FileOutputStream(fp);
            fos.write(data, 0, length);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
