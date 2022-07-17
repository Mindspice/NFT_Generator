package collection;

import java.io.File;
import java.util.List;

public class NFT {
    private String name;
    private List<String> traitList;
    private String id;
    private File finalImage;

    public NFT(String name, List<String> traits, boolean disregardDB) {
        this.name = name;
        this.traitList = traits;
        if (disregardDB) {
            id = genId(1);
        } else {
            id = genId(0);
        }
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    private String genId(int index) {
        StringBuilder sb = new StringBuilder();

        for (int i = index; i < traitList.size(); ++i) {
            sb.append(traitList.get(i));
        }
        return sb.toString();
    }

    public List getTraitList() {
        return traitList;
    }

    public File getFinalImage() {
        return finalImage;
    }

    public void setFinalImage(File finalImage) {
        this.finalImage = finalImage;
    }
}