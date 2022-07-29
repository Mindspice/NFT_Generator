package json;

import collection.Flags;

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

    public static class CollectionSettings {
        public String name = "";
        public List<String[]> colAttributes = new ArrayList<>();
        public String colDescription = "";
        public String id = "";
        public String filePrefix = "";
        public String namePrefix = "";
        public List<String[]> traitOpt = new ArrayList<>();
        public int startIndex;
        public String outputDirectory = "";
        public int size;
        public int width;
        public int height;
        public List<LayerSettings> layerList = new ArrayList<>();
        public NameGenSettings nameGenSettings;
        public Flags flags;
    }

    public static class NameGenSettings {
        public String nameList = "";
        public int wordCount;

    }

    public static class LayerSettings {
        public String name = "";
        public int number;
        public List<ImageSettings> imageList = new ArrayList<>();
    }

    public static class ImageSettings {
        public String name = "";
        public String file = "";
        public double weight;
        public int max;
        public int muteGroup;
    }

    public static class MintRpc {
        public int wallet_id;
        public List<String> uris;
        public String hash;
        public List<String> meta_uris;
        public String meta_hash;
        public List<String> license_uris;
        public String license_hash;
        public String royalty_address;
        public int royalty_percentage;
        public String target_address;
        public int edition_number;
        public int edition_count;
        public int fee;
    }
}


