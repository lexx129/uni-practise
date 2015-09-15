import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Main {

    private static String getHash(File file) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        byte[] bytes = new byte[4096];
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            while (true) {
                int read = dis.read(bytes);
                if (read == -1) break;
                instance.update(bytes);
            }
        } catch (IOException e) {
            System.err.println("Access to file " + file.getName() + " is denied :(");
        }
        return new BigInteger(instance.digest()).toString(16);
    }

    private static FileInfo addInstance(File file) throws NoSuchAlgorithmException, IOException {
//        File curr_file = new File(path);
        String curr_hash = getHash(file);
        return new FileInfo(file.length(), curr_hash);
    }

    private static void scan(String rootDir) throws IOException, NoSuchAlgorithmException {
        HashMap<String, FileInfo> map = new HashMap<>();
        FileInfo fi;
        FileOutputStream fos = new FileOutputStream("snapshot.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        String rootDir = "d:\\";
        File root = new File(rootDir);
        File[] files = root.listFiles();
        int i = 0;
        while (i < files.length) {
            File firstElement = files[i];
            File[] subFiles = null;
            if (firstElement.isDirectory()) {
                subFiles = firstElement.listFiles();
            } else {
                i++;
                continue;
            }
            File[] temp = new File[files.length + subFiles.length];
            for (int j = 0; j <= i; j++)
                temp[j] = files[j];
            for (int k = 0; k < subFiles.length; k++)
                temp[i + 1 + k] = subFiles[k];
            for (int m = i + 1; m < files.length; m++)
                temp[m + subFiles.length] = files[m];
            files = temp;
            i++;
        }
        for (File file : files) {
//            System.out.println(file.getPath());
            if (!file.isDirectory()) {
                fi = addInstance(file);
                map.put(file.getAbsolutePath(), fi);
            }
        }
        oos.writeObject(map);
        oos.flush();
        oos.close();
    }

    private static boolean check

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        // тут надо запросить область поиска
        File file = new File("C:\\Drivers");
        scan("C:\\Drivers");


    }

}
