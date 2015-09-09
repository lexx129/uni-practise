import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Main {
    private static FileInfo addInstance(File file) throws NoSuchAlgorithmException, IOException {
//        File curr_file = new File(path);
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        byte[] bytes = new byte[4096];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        while (true) {
            int read = dis.read(bytes);
            if (read == -1) break;
            instance.update(bytes);
        }
        String curr_hash = new BigInteger(instance.digest()).toString(16);
        return new FileInfo(file.length(), curr_hash);
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        FileInfo fi;
        FileOutputStream fos = new FileOutputStream("snapshot.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        File file = new File("C:\\Temp\\123.txt");
        HashMap<String, FileInfo> map = new HashMap<String, FileInfo>();
        fi = addInstance(file);
        map.put(file.getAbsolutePath(), fi);
        oos.writeObject(map);
        oos.flush();
        oos.close();
    }

}
