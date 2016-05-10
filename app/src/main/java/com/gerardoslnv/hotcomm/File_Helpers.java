package com.gerardoslnv.hotcomm;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class File_Helpers {


    public static void downloadPDF(String remoteFilePath, File targetFile) {
        try {
            URL fileURL = new URL(remoteFilePath);
            HttpURLConnection mHttpConn = (HttpURLConnection) fileURL.openConnection();
            InputStream inputStream = new BufferedInputStream(mHttpConn.getInputStream());

            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
            copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private static void copy(InputStream is, OutputStream os) {
        final byte[] buf = new byte[1024];
        int numBytes;

        try {
            while (-1 != (numBytes = is.read(buf))) {
                os.write(buf, 0, numBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                Log.e("IOExcept.", "IOException");
            }
        }
        Log.i("copy function", "File copied over");
    }
}