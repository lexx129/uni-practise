import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
        if (file.isDirectory()){
            String curr_hash =
        }
        else
        String curr_hash = getHash(file);
        return new FileInfo(file.length(), curr_hash);

    }

    private static void scan(File rootDir) throws IOException, NoSuchAlgorithmException {
        HashMap<String, FileInfo> map = new LinkedHashMap<>();
        FileInfo fi;
        FileOutputStream fos = new FileOutputStream("snapshot.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        String rootDir = "d:\\";
//        File root = new File(rootDir);
        File[] files = rootDir.listFiles();
        int i = 0;
        assert files != null;
        while (i < files.length) {
            File firstElement = files[i];
            File[] subFiles = null;
            if (firstElement.isDirectory()) {
                subFiles = firstElement.listFiles();
            } else {
                i++;
                continue;
            }
            assert subFiles != null;
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
            System.out.println(file.getPath());
            if (!file.isDirectory()) {
                fi = addInstance(file);
                map.put(file.getAbsolutePath(), fi);
            }
        }
        oos.writeObject(map);
        oos.flush();
        oos.close();
    }

    private static void check (String rootDir) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("snapshot.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        LinkedHashMap<String, FileInfo> ref = (LinkedHashMap<String, FileInfo>) ois.readObject();
        String root = ref.entrySet().iterator().next().getKey();
        System.out.println(root);

    }


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        // enter a place for scan here
        File file = new File("C:\\PortableTex");
        scan(file);
        check("C:\\PortableTex");


    }

}
