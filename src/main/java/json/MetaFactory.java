package json;

import collection.Collection;

import java.util.List;

public class MetaFactory {
    private final Collection collection;
    private final JsonContainers.Collection colMeta;
    private final boolean sensitiveContent;


    public MetaFactory(Collection collection) {
        this.collection = collection;
        colMeta = new JsonContainers.Collection();
        colMeta.name = collection.getName();
        colMeta.id = collection.getId();
        sensitiveContent = collection.getFlags().sensitiveContent;

        if (collection.getColAttributes() == null) return;

        for (String[] s : collection.getColAttributes()) {
            colMeta.attributes.add(new JsonContainers.Attribute(s[0],s[1]));
        }
    }

    public JsonContainers.MetaData getMeta(int index, List<String[]> traits){
        var meta = new JsonContainers.MetaData();
        var name = new StringBuilder();
        if (collection.getNamePrefix() != null) name.append(collection.getNamePrefix()).append(" ");
        if (collection.getFlags().randomNames) {
            name.append(collection.getNameGen().getName()).append(" ");
        }
        if (collection.getFlags().indexInName) name.append("#").append(index);
        meta.name = name.toString();

        var description = new StringBuilder();
        if (collection.getFlags().colNameInDesc) description.append(collection.getName()).append(" ");
        if (collection.getFlags().nftNameInDesc) description.append(name).append(" ");
        if (collection.getFlags().indexInDesc) description.append("#").append(index);
        if (collection.getFlags().mirrorColDesc) description.append(collection.getColDescription());
        meta.description = description.toString();

        meta.sensitive_content = sensitiveContent;

        if (collection.getTraitOpt() != null) {
            if (collection.getFlags().seriesAsTrait) {
                meta.attributes.add(new JsonContainers.Trait("Series #", index + " of "+ collection.getSize()));
            }
            for (String[] s : collection.getTraitOpt()) {
                meta.attributes.add(new JsonContainers.Trait(s[0], s[1]));
            }
        }
        if (traits != null) {
            for (String[] s : traits) {
                meta.attributes.add(new JsonContainers.Trait(s[0], s[1]));
            }
        }
        //TODO attributes series number
        meta.collection = colMeta;
        return meta;
    }
}
