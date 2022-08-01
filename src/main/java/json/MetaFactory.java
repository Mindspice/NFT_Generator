package json;

import collection.Collection;

import java.util.List;

public class MetaFactory {
    private final Collection collection;

    public MetaFactory(Collection collection) {
        this.collection = collection;
    }

    public JsonContainers.MetaData getMeta(int index, List<String[]> traits){

        var colMeta = new JsonContainers.Collection();
        colMeta.name = collection.getName();
        colMeta.id = collection.getId();

        if (collection.getFlags().seriesAsAttribute) {
            var attr = new JsonContainers.Attribute("Series #", index + " of "+ collection.getSize());;
            colMeta.attributes.add( attr);
        }
        for (String[] s : collection.getColAttributes()) {
            colMeta.attributes.add(new JsonContainers.Attribute(s[0],s[1]));
        }

        var meta = new JsonContainers.MetaData();
        var name = new StringBuilder();
        meta.sensitive_content = collection.getFlags().sensitiveContent;

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
        meta.collection = colMeta;
        return meta;
    }
}
