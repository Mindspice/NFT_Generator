package json;

import collection.NFT;
import layer.Layer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonContainers {

    public static class MetaData {
        public final String format = "CHIP-0007";
        public String name;
        public String description;
        public final String minting_tool = "SpiceGen_v1 | https://mindspice.io";
        public boolean sensitive_content;
        public List<Trait> attributes = new ArrayList<>();
        public Collection collection;
    }

    public static class Collection {
        public String name;
        public String id;
        public List<Attribute> attributes = new ArrayList<>();
    }

    public static class Trait {
        public String trait_type;
        public String value;

        public Trait(String trait_type, String value) {
            this.trait_type = trait_type;
            this.value = value;
        }
    }

    public static class Attribute {
        public String type;
        public String value;

        public Attribute(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // Serialization containers
    public static class CollectionSettings {
        public String name;
        public boolean sensitiveContent;
        public List<String[]> colAttributes;
        public String colDescription;
        public String id;
        public String filePrefix;
        public String namePrefix;
        public List<String[]> traitOpt;
        public int startIndex;
        public boolean randomNames;
        public boolean uniqueNames;
        public boolean indexInName;
        public boolean nameInFileName;
        public boolean mirrorColDesc;
        public boolean indexInDesc;
        public boolean colNameInDesc;
        public boolean nftNameInDesc;
        public String outputDirectory;
        public int size;
        public int width;
        public int height;
        public List<LayerSettings> layerList;
    }

    public static class NameGenSettings {
        public String nameList;
        public int wordCount;

    }
    
    public static class LayerSettings {
        public String name;
        public int number;
        public List<ImageSettings> imageList;
    }

    public static class ImageSettings {
        public String name;
        public String file;
        public double weight;
        public int max;
        public int muteGroup;
    }
}
