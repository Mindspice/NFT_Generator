package collection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class Collection {

    public String name;
    public String prefix;
    public boolean sensitiveContent;
    public String id;
    public String icon;
    public String banner;
    public String twitter;
    public String website;
    public String[] colAlt1;
    public String[] colAlt2;
    public String[] colAlt3;


    public File outputDirectory;
    public int startIndex;
    public int size;
    public int width;
    public int height;
    public boolean duplicates;
    public boolean disregardBG;
    public List<NFT> collectionList = new ArrayList<>();

    public Collection() {
    this.name = "New Collection";
    this.prefix = "newCollection";
    this.size = 1000;
    this.width = 1200;
    this.height = 1200;
    this.duplicates = false;
    this.disregardBG = true;
    }


    // GETTERS
//    public String getName() {
//        return name;
//    }
//
//    public String getPrefix() {
//        return prefix;
//    }
//
//    public File getOutputDirectory() {
//        return outputDirectory;
//    }
//
//    public int getSize() {
//        return size;
//    }
//    public int getWidth() {
//        return width;
//    }
//
//    public int getHeight() {
//        return height;
//    }
//
//    public boolean isDuplicates() {
//        return duplicates;
//    }
//
//    public boolean isDisregardBG() {
//        return disregardBG;
//    }
//
//    // SETTERS
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setPrefix(String prefix) {
//        this.prefix = prefix;
//    }
//
//    public void setOutputDirectory(File outputDirectory) {
//        this.outputDirectory = outputDirectory;
//    }
//
//    public void setSize(int size) {
//        this.size = size;
//    }
//
//    public void setWidth(int width) {
//        this.width = width;
//    }
//
//    public void setHeight(int height) {
//        this.height = height;
//    }
//
//    public void setDuplicates(boolean duplicates) {
//        this.duplicates = duplicates;
//    }
//
//    public void setDisregardBG(boolean disregardBG) {
//        this.disregardBG = disregardBG;
//    }
//
//    public void addNFT(NFT nft) {
//        collectionList.add(nft);
//    }


}
