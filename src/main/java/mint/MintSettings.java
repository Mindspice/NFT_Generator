package mint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MintSettings {
    public final List<String>[] imageURIs = new ArrayList[5];
    public final List<String>[] metaURIs = new ArrayList[5];
    public final String[] licenseURIs = new String[5];
    public String licenseHash;
    public File imageDir;
    public File metaDir;
    public File outputDir;

    public List<String> getImageUriIndex(int i) {
        List<String> uris = new ArrayList<>();
        for (List<String> s : imageURIs) {
            if (s != null) uris.add(s.get(i));
        }
        return uris;
    }

    public List<String> getMetaUriIndex(int i) {
        List<String> uris = new ArrayList<>();
        for (List<String> s : metaURIs) {
            if (s != null) uris.add(s.get(i));
        }
        return uris;
    }



}