package utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

public class HashFunc {

    public static String getHash(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes);
        byte[] hash = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return hexString.toString();
    }

    public static byte[] fetchBytes(URL url) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        URLConnection conn;
        try {
            conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.connect();
        } catch (IOException e) {
            return null;
            //TODO log this return with url obj
        }
        try (InputStream stream = conn.getInputStream()) {

        byte[] chunk = new byte[4096];
            int bytesRead;

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outputStream.toByteArray();
    }
}


