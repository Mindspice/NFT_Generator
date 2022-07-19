package collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import layer.Layer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Collection {

    private String name;
    private boolean sensitiveContent;
    private List<String[]> colAttributes = new ArrayList<>();
    private String colDescription;
    private String id;
    private String filePrefix;
    private String namePrefix;
    private List<String[]> traitOpt;
    private File nameList;
    private int startIndex = 1;

        /* Boolean Flags */
    private boolean randomNames;
    private boolean uniqueNames;
    private boolean indexInName;
    private boolean nameInFileName;
    private boolean mirrorColDesc;
    private boolean indexInDesc;
    private boolean colNameInDesc;
    private boolean nftNameInDesc;

    private File outputDirectory;
    private int size = 1000;
    private int width = 0;
    private int height = 0;
    private boolean duplicates = false;
    private boolean disregardBG = true;
    private final List<NFT> collectionList = new ArrayList<>();

    private NameGen nameGen;

    private final ObservableList<Layer> layerList = FXCollections.observableArrayList();

    public void resetAttributes() {
        if (colAttributes != null) colAttributes.clear();
        if (traitOpt != null) traitOpt.clear();
    }

        /* Getters */
    public String getName() {
        return name;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public int getSize() {
        return size;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<String[]> getColAttributes() {
        return colAttributes;
    }

    public List<NFT> getCollectionList() {
        return collectionList;
    }

    public File getNameList() {
        return nameList;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public boolean isDuplicates() {
        return duplicates;
    }

    public boolean isDisregardBG() {
        return disregardBG;
    }

    public boolean isNameInFileName() {
        return nameInFileName;
    }

    public boolean isSensitiveContent() {
        return sensitiveContent;
    }

    public boolean isIndexInDesc() {
        return indexInDesc;
    }

    public boolean isColNameInDesc() {
        return colNameInDesc;
    }

    public boolean isMirrorColDesc() {
        return mirrorColDesc;
    }

    public boolean isRandomNames() {
        return randomNames;
    }

    public NameGen getNameGen() {
        return nameGen;
    }

    public String getId() {
        return id;
    }

    public String getColDescription() {
        return colDescription;
    }

    public boolean isNftNameInDesc() {
        return nftNameInDesc;
    }

    public List<String[]> getTraitOpt() {
        return traitOpt;
    }

    public boolean isIndexInName() {
        return indexInName;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public boolean isUniqueNames() {
        return uniqueNames;
    }

    public ObservableList<Layer> getLayerList() {
        return layerList;
    }

    /* SETTERS */

    public void addNFT(NFT nft) {
        collectionList.add(nft);
    }


    public void addTraitOpt(String trait_type, String value) {
        if (traitOpt == null) traitOpt = new ArrayList<>();
        traitOpt.add(new String[]{trait_type,value});
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDuplicates(boolean duplicates) {
        this.duplicates = duplicates;
    }

    public void setDisregardBG(boolean disregardBG) {
        this.disregardBG = disregardBG;
    }

    public void setSensitiveContent(boolean sensitiveContent) {
        this.sensitiveContent = sensitiveContent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        colAttributes.add(new String[]{"description", description});
        this.colDescription = description;
    }

    public void setIcon(String icon) {
        colAttributes.add(new String[]{"icon", icon});
    }

    public void setBanner(String banner) {
        colAttributes.add(new String[]{"banner", banner});
    }

    public void setTwitter(String twitter) {
        colAttributes.add(new String[]{"twitter", twitter});
    }

    public void setWebsite(String website) {
        colAttributes.add(new String[]{"website", website});
    }

    public void setRandomNames(boolean randomNames) {
        this.randomNames = randomNames;
    }

    public void setUniqueNames(boolean uniqueNames) {
        this.uniqueNames = uniqueNames;
    }

    public void setIndexInName(boolean indexInName) {
        this.indexInName = indexInName;
    }

    public void setNameInFileName(boolean nameInFileName) {
        this.nameInFileName = nameInFileName;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setNameList(File nameList) {
        this.nameList = nameList;
    }

    public void setMirrorColDesc(boolean mirrorColDesc) {
        this.mirrorColDesc = mirrorColDesc;
    }

    public void setIndexInDesc(boolean indexInDesc) {
        this.indexInDesc = indexInDesc;
    }

    public void setColNameInDesc(boolean colNameInDesc) {
        this.colNameInDesc = colNameInDesc;
    }

    public void setNameGen(int wordCount) throws IOException, IllegalArgumentException {
        nameGen = new NameGen(wordCount);
    }

    public void setNftNameInDesc(boolean nftNameInDesc) {
        this.nftNameInDesc = nftNameInDesc;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public class NameGen {
        List<String> names;
        HashMap<String,Boolean> takenNames = new HashMap<>();
        public int wordCount;
        int unique;

        public NameGen(int wordCount) throws IllegalArgumentException, IOException {

            names = Files.readAllLines(Paths.get(nameList.getPath()));

            unique = (int) Math.pow(names.size(),wordCount);
            if (uniqueNames && size < unique) throw new IllegalArgumentException();

            this.wordCount = wordCount;
        }

        public String getName() {
            String name = buildName();
            int i = 0;
            while (takenNames.containsKey(name) && i <= unique) {
                name = buildName();
                i++;
                if (i == unique) throw new IllegalStateException();
            }
            takenNames.put(name, true);
            return name;
        }

        private String buildName() {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i <wordCount; ++i) {
                name.append(names.get( ThreadLocalRandom.current().nextInt(0,names.size())));
                if (i != wordCount - 1) name.append(" ");
            }
            return name.toString();
        }
    }


}
