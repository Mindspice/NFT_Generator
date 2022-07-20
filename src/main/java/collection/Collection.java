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

    private String name = "";
    private final List<String[]> colAttributes = new ArrayList<>();
    private String colDescription = "";
    private String id = "";
    private String filePrefix ="";
    private String namePrefix ="";
    private final List<String[]> traitOpt = new ArrayList<>();
    private File nameList;
    private int startIndex = 1;
    private Flags flags = new Flags();
    private File outputDirectory;
    private int size = 0;
    private int width = 0;
    private int height = 0;
    private final List<NFT> collectionList = new ArrayList<>();

    private NameGen nameGen;

    private final ObservableList<Layer> layerList = FXCollections.observableArrayList();

    public void resetAttributes() {
         colAttributes.clear();
        traitOpt.clear();
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

    public NameGen getNameGen() {
        return nameGen;
    }

    public String getId() {
        return id;
    }

    public String getColDescription() {
        return colDescription;
    }

    public List<String[]> getTraitOpt() {
        return traitOpt;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public ObservableList<Layer> getLayerList() {
        return layerList;
    }

    public Flags getFlags() {
        return flags;
    }

        /* SETTERS */

    public void addNFT(NFT nft) {
        collectionList.add(nft);
    }

    public void addTraitOpt(String trait_type, String value) {
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

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setNameList(File nameList) {
        this.nameList = nameList;
    }

    public void setNameGen(int wordCount) throws IOException, IllegalArgumentException {
        nameGen = new NameGen(wordCount);
    }


    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setColDescription(String colDescription) {
        this.colDescription = colDescription;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public class NameGen {
        List<String> names;
        HashMap<String,Boolean> takenNames = new HashMap<>();
        private int wordCount;
        int unique;

        public NameGen(int wordCount) throws IllegalArgumentException, IOException {
            names = Files.readAllLines(Paths.get(nameList.getPath()));
            unique = (int) Math.pow(names.size(),wordCount);
            if (flags.uniqueNames && size > unique) throw new IllegalArgumentException();
            this.wordCount = wordCount;
        }

        public String getName() {
            String name = buildName();
            int i = 0;
            while (takenNames.containsKey(name) && i <= unique) {
                name = buildName();
                i++;
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

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int i) throws IllegalArgumentException {
            wordCount = i;
            unique = (int) Math.pow(names.size(),wordCount);
            System.out.println(names.size());
            System.out.println(unique);
            if (flags.uniqueNames && size > unique) throw new IllegalArgumentException();
        }
    }
}
