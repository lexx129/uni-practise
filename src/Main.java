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
//        LinkedHashMap<String, FileInfo> map = new LinkedHashMap<>();
//        TreeMap<String, FileInfo> map = new TreeMap<>((o1, o2) -> {
//            return o1.toLowerCase().compareTo(o2.toLowerCase());
//        });
        TreeMap<String, FileInfo> map = new TreeMap<>();
        FileInfo fi;
        FileOutputStream fos = new FileOutputStream("snapshot12.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        String rootDir = "d:\\";
//        File root = new File(rootDir);
        File[] files = rootDir.listFiles();
//        files[0] = rootDir;

        int i = 0;
        assert files != null;
        while (i < files.length) {
            File firstElement = files[i];
            File[] subFiles;
            if (firstElement.isDirectory()) {
                subFiles = firstElement.listFiles();
            } else {
                i++;
                continue;
            }
            assert subFiles != null;
            File[] temp = new File[files.length + subFiles.length];
            System.arraycopy(files, 0, temp, 0, i + 1);
            for (int k = 0; k < subFiles.length; k++)
                temp[i + 1 + k] = subFiles[k];
            System.arraycopy(files, i + 1, temp, i + 1 + subFiles.length, files.length - (i + 1));
            files = temp;
            i++;
        }
        long scanned = 0;
        for (File file : files) {
//            System.out.println(file.getPath());
            scanned++;
//            System.out.println("Files scanned: " + scanned);
            fi = addInstance(file);
            map.put(file.getAbsolutePath().toLowerCase(), fi);
        }
//        int a = 0 + 16;
        System.out.println("Total: " + scanned + "\n Snapshot creating completed!");
//        map.
        oos.writeObject(map);
        oos.flush();
        oos.close();
    }

    private static TreeMap<String, FileInfo> secondarySnapMake(File rootDir) throws IOException, NoSuchAlgorithmException {
        TreeMap<String, FileInfo> map = new TreeMap<>();
        FileInfo fi;
        //        String rootDir = "d:\\";
//        File root = new File(rootDir);
        File[] files = rootDir.listFiles();

//        files[0] = rootDir;
        int i = 0;
        assert files != null;
        while (i < files.length) {
            File firstElement = files[i];
            File[] subFiles;
            if (firstElement.isDirectory()) {
                subFiles = firstElement.listFiles();
            } else {
                i++;
                continue;
            }
            assert subFiles != null;
            File[] temp = new File[files.length + subFiles.length];
            System.arraycopy(files, 0, temp, 0, i + 1);
            for (int k = 0; k < subFiles.length; k++)
                temp[i + 1 + k] = subFiles[k];
            System.arraycopy(files, i + 1, temp, i + 1 + subFiles.length, files.length - (i + 1));
            files = temp;
            i++;
        }
        long scanned = 0;
        for (File file : files) {
//            System.out.println(file.getPath());
            scanned++;
//            System.out.println("Files scanned: " + scanned);
            fi = addInstance(file);
            map.put(file.getAbsolutePath().toLowerCase(), fi);
        }
        System.out.println("**Total files in scanplace: " + scanned);
        return map;
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

    private static void mapCompare(SortedMap<String, FileInfo> map1, TreeMap<String, FileInfo> map2) {
        Iterator<Map.Entry<String, FileInfo>> iter1 = map1.entrySet().iterator();
        while (iter1.hasNext()) {
            boolean checked = false;
            Map.Entry<String, FileInfo> entry1 = iter1.next();
//            for (Map.Entry<String, FileInfo> entry1 : sub.entrySet()) {
//               for (Map.Entry<String, FileInfo> entry2 : scanplace.entrySet()) {
            Iterator<Map.Entry<String, FileInfo>> iter2 = map2.entrySet().iterator();
            while (iter2.hasNext()) {
                if (checked)
                    break;
                Map.Entry<String, FileInfo> entry2 = iter2.next();
                if ((entry1.getKey().equals(entry2.getKey())) && (entry1.getValue().getHash().equals(entry2.getValue().getHash()))
                        && (entry1.getValue().getSize() == (entry2.getValue().getSize()))) {
                    System.out.println("File '" + entry1.getKey() + "' is OK");
//                            sub.remove(entry1.getKey());
//                            scanplace.remove(entry2.getKey());
                    iter1.remove();
                    iter2.remove();
                    checked = true;
                    break;
                }
                if ((entry1.getKey().equals(entry2.getKey())) && (!entry1.getValue().getHash().equals(entry2.getValue().getHash()))) {
                    if (new File(entry1.getKey()).isDirectory())
                        System.out.println("Folder '" + entry1.getKey() + "' is OK");
                    else
                        System.out.println("File '" + entry1.getKey() + "' was edited (hash differs)");
//                            sub.remove(entry1.getKey());
//                            scanplace.remove(entry2.getKey());
                    iter1.remove();
                    iter2.remove();
                    checked = true;
                    break;
                }
                if ((entry1.getKey().equals(entry2.getKey())) && !(entry1.getValue().getSize() == (entry2.getValue().getSize()))) {
                    System.out.println("File '" + entry1.getKey() + "' was edited (size differs)");
//                            sub.remove(entry1.getKey());
//                            scanplace.remove(entry2.getKey());
                    iter1.remove();
                    iter2.remove();
                    checked = true;
                    break;
                }
                if ((!entry1.getKey().equals(entry2.getKey())) && (entry1.getValue().getHash().equals(entry2.getValue().getHash()))
                        && (entry1.getValue().getSize() == (entry2.getValue().getSize()))) {
                    System.out.println("File '" + entry1.getKey() + "' was renamed. Now it's named as '" + entry2.getKey() + "'");
//                            sub.remove(entry1.getKey());
//                            scanplace.remove(entry2.getKey());
                    iter1.remove();
                    iter2.remove();
                    checked = true;
                }
            }
        }
        if (!map1.isEmpty()) {
            for (Map.Entry<String, FileInfo> entry1 : map1.entrySet())
                System.out.println("File '" + entry1.getKey() + "' was deleted");
        }
        if (!map2.isEmpty()) {
            for (Map.Entry<String, FileInfo> entry1 : map2.entrySet())
                System.out.println("File '" + entry1.getKey() + "' was created");
        }
    }

    private static void snapCheck(String checkPath) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        FileInputStream fis = new FileInputStream("snapshot12.out");
        ObjectInputStream ois = new ObjectInputStream(fis);
        TreeMap<String, FileInfo> ref = (TreeMap<String, FileInfo>) ois.readObject();
        String preRoot = ref.entrySet().iterator().next().getKey();
        String rootFolder = new File(preRoot).getParent().toLowerCase();
//        System.out.println(checkPath + "   " + rootFolder);
//        boolean isInner = checkPath.contains(rootFolder);
//        boolean isOuter = rootFolder.contains(checkPath);
        System.out.println("Ref size should be: " + ref.size());
        SortedMap<String, FileInfo> sub;
        if (isInner(rootFolder, checkPath)) {
//            System.out.println("**Path for checking is inner!");
//            System.out.println(ref.lastKey());
            if (rootFolder.equals(checkPath)) {
                System.out.println("**Path for checking equals with snapshot root");
                sub = ref;
            } else {
                System.out.println("**Path for checking is inner");
                sub = ref.subMap(checkPath, ref.lastKey());
//            Iterator iterator = sub.entrySet().iterator();
                String lower_grade = "";
                for (Map.Entry<String, FileInfo> entry : sub.entrySet()) {
//                String prev = lower_grade;
                    lower_grade = entry.getKey();
//                String curr_hash = entry.getValue().getHash();
                    if (!lower_grade.contains(checkPath)) {
//                    lower_grade = prev;
                        break;
                    }
                }
//            System.out.println(lower_grade);
                sub = ref.subMap(checkPath, lower_grade);
                sub.remove(sub.firstKey());
            }
//            System.out.println(ref.lastKey());
//            for (int i = ref.keySet().; i < ; i++) {
            TreeMap<String, FileInfo> scanplace = secondarySnapMake(new File(checkPath));
//            Iterator<Map.Entry<String, FileInfo>> iter1 = sub.entrySet().iterator();
            mapCompare(sub, scanplace);
        } else {
            System.out.println("**Path for checking is outer");
            File[] files = new File(checkPath).listFiles();

            int i = 0;
            assert files != null;
            while (i < files.length) {
                File firstElement = files[i];
                File[] subFiles;
                if (firstElement.isDirectory()) {
                    subFiles = firstElement.listFiles();
                } else {
                    i++;
                    continue;
                }
                assert subFiles != null;
                File[] temp = new File[files.length + subFiles.length];
                System.arraycopy(files, 0, temp, 0, i + 1);
                for (int k = 0; k < subFiles.length; k++)
                    temp[i + 1 + k] = subFiles[k];
                System.arraycopy(files, i + 1, temp, i + 1 + subFiles.length, files.length - (i + 1));
                files = temp;
                i++;
            }
            ArrayList<File> listFiles = new ArrayList<>(Arrays.asList(files));
            TreeMap<String, FileInfo> scanplace;
            File last_imp = null;
            boolean found = false;
            for (File file : listFiles) {
                if (file.getAbsolutePath().toLowerCase().equals(rootFolder)) {
                    scanplace = secondarySnapMake(file);
                    last_imp = new File(scanplace.lastKey());
                    mapCompare(ref, scanplace);
                    found = true;
                    break;
                }
                else System.out.println("File '" + file + "' was created (no info in snapshot)");
            }
            if (found) {
                System.out.println("**Last file from found piece is " + last_imp);
                for (int j = listFiles.indexOf(last_imp) + 1; j < listFiles.size(); j++) {
                    System.out.println("File '" + listFiles.get(j) + "' was created (no info in snapshot)");
                }
            } else System.out.println("**Entered scanplace doesn't intersect with snapshot root folder");
        }
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
