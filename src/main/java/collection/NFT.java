package collection;

import json.JsonContainers;

import java.io.File;
import java.util.List;

public class NFT {
    private String name;
    private int index;
    private List<String[]> traitList;
    private String id;
    private File finalImage;
    private JsonContainers.MetaData metaData;

    public NFT(JsonContainers.MetaData metaData,int index, List<String[]> traits, boolean disregardDB) {
        this.metaData = metaData;
        this.index = index;
        this.name = metaData.name;
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
            sb.append(traitList.get(i)[1]);
        }
        return sb.toString();
    }

    public List<String[]> getTraitList() {
        return traitList;
    }

    public File getFinalImage() {
        return finalImage;
    }

    public void setFinalImage(File finalImage) {
        this.finalImage = finalImage;
    }
    public String getName () {
        return name;
    }

    public JsonContainers.MetaData getMetaData() {
        return metaData;
    }

    public int getIndex() {
        return index;
    }
}