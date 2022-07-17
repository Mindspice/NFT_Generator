package json;

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
}
