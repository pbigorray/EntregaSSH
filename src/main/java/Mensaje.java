import java.io.File;
import java.io.Serializable;

public class Mensaje implements Serializable {
    private File f;

    public Mensaje(File f) {
        this.f = f;
    }

    public File getFile() {
        return f;
    }
}