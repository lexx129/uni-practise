import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: gavriloval
 * Date: 08.09.15
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class FileInfo implements Serializable {
    public long size;
    public String hash;

    public FileInfo (long size, String hash) {
        this.size = size;
        this.hash = hash;
    }

    public String getHash (){
        return hash;
    }

    public long getSize() {
        return size;
    }


}
