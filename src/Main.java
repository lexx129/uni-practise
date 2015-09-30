import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private static String getDirHash(File file) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        byte[] b = file.getAbsolutePath().getBytes();
        instance.reset();
        instance.update(b);
        return new BigInteger(instance.digest()).toString(16);
    }

    private static boolean checkHash(File checkTarget, String true_hash) throws NoSuchAlgorithmException {
        String curr_hash = getHash(checkTarget);
        return true_hash.equals(curr_hash);

    }

    private static FileInfo addInstance(File file) throws NoSuchAlgorithmException, IOException {
//        File curr_file = new File(path);
        String curr_hash;
        if (file.isDirectory()) {
            curr_hash = getDirHash(file);
        } else {
            curr_hash = getHash(file);
        }
        return new FileInfo(file.length(), curr_hash);
    }

    private static void snapMake(File rootDir) throws IOException, NoSuchAlgorithmException {
        TreeMap<String, FileInfo> map = new TreeMap<>();
        FileInfo fi;
        FileOutputStream fos = new FileOutputStream("snapshot.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        String rootDir = "d:\\";
//        File root = new File(rootDir);
        File[] files = rootDir.listFiles();
//        files[0] = rootDir;

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
        long scanned = 0;
        for (File file : files) {
            System.out.println(file.getPath());
            scanned++;
//            System.out.println("Files scanned: " + scanned);
            fi = addInstance(file);
            map.put(file.getAbsolutePath(), fi);
        }
//        int a = 0 + 16;
        System.out.println("Total: " + scanned);
        oos.writeObject(map);
        oos.flush();
        oos.close();
    }

    private static boolean isInner(String root, String target) {
        return target.contains(root);
    }


    private static int findAndCheck(String curr_path, String curr_hash, Long curr_size) throws NoSuchAlgorithmException {
        File temp = new File(curr_path);
        boolean name = temp.getAbsolutePath().toLowerCase().equals(curr_path);
        boolean hash = checkHash(temp, curr_hash);
        boolean size = (curr_size.equals(temp.length()));
        if (temp.exists()) {
            if (name && hash && size) {
                System.out.println("File + " + temp.getAbsolutePath() + " is absolutly OK");
                return 1;
            }
            if (!name && hash && size) {
                System.out.println("File + " + temp.getAbsolutePath() + " was renamed");
                return 2;
            }
            if (name && !hash && size) {
                System.out.println("File + " + temp.getAbsolutePath() + " was changed (different hash)");
                return 3;
            }
            if (name && hash && !size) {


            }
        }

        return 0;
    }

    private static void snapCheck(String checkPath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("snapshot.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        TreeMap<String, FileInfo> ref = (TreeMap<String, FileInfo>) ois.readObject();
        String preRoot = ref.entrySet().iterator().next().getKey();
        String rootFolder = new File(preRoot).getParent().toLowerCase();
//        System.out.println(checkPath + "   " + rootFolder);
//        boolean isInner = checkPath.contains(rootFolder);
//        boolean isOuter = rootFolder.contains(checkPath);
        System.out.println("Ref size should be: " + ref.size());
        if (isInner(rootFolder, checkPath)) {
            System.out.println("**Path for checking is inner!");
            System.out.println(ref.lastKey());
            SortedMap<String, FileInfo> sub = ref.subMap(checkPath, ref.lastKey());
//            Iterator iterator = sub.entrySet().iterator();
            String lower_grade = "";
            for (Map.Entry<String, FileInfo> entry : sub.entrySet()) {
                String prev = lower_grade;
                lower_grade = entry.getKey();
//                String curr_hash = entry.getValue().getHash();
                if (!lower_grade.contains(checkPath)) {
                    lower_grade = prev;
                    break;
                }
            }
            System.out.println(lower_grade);
            sub = ref.subMap(checkPath, lower_grade);
//            System.out.println(ref.lastKey());
//            for (int i = ref.keySet().; i < ; i++) {

        }
//        System.out.println(b);
//        System.out.println("-----\r\n" + ololo);
//        boolean b = ref.containsKey("C:\\SwSetup\\sp45820");
//        System.out.println(b);

    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Режим работы: \n 1 - Создание снимка указанного расположения " +
                "\n 2 - Проверка по уже существующему снимку");
        String type = bufferedReader.readLine();
        switch (type) {
            case "1":
                System.out.println("Enter a place for scanning: ");
                String rootPlace = bufferedReader.readLine();
                File rootFile = new File(rootPlace);
                snapMake(rootFile);
//            snapCheck(rootFile);
                break;
            case "2":
                System.out.println("Enter a place to check: ");
                String checkPlace = bufferedReader.readLine();
                snapCheck(checkPlace);
                break;
            case "3":
                File temp1 = new File("C:\\SwSetup\\SoftPaq\\SP54421\\autorun45.inf");
                System.out.println(getHash(temp1));
                break;
//            6e072801d977bfe94f38c2cad4cf3b3e419e8cb2
            default:
//                System.out.println(type);
                System.err.println("Invalid working mode number");
                System.exit(0);
        }
        // enter a place for snapMake here
//        File file = new File("C:\\SwSetup");


//        System.out.println(getDirHash(temp));
//        System.out.println(temp.length());
//        System.out.println(getHash(temp1));
//        System.out.println(temp1.length());
//        snapCheck("C:\\SwSetup\\SP58970");
//        6e072801d977bfe94f38c2cad4cf3b3e419e8cb2

    }

}
