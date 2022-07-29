package utility;

import collection.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generator.GeneratorController;
import imagefile.ImageFile;
import json.JsonContainers;
import layer.Layer;
import main.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static utility.Util.ErrorType.FILE;

public class Serialize {
    public static void exportSettings(Collection collection, String file) {
        var collectionSettings = new JsonContainers.CollectionSettings();
        collectionSettings.name = collection.getName();

        if(collection.getColAttributes() != null) {
            collectionSettings.colAttributes = new ArrayList<>();
            for (String[] s : collection.getColAttributes()) {
                collectionSettings.colAttributes.add(new String[]{s[0], s[1]});
            }
        }
        collectionSettings.colDescription = collection.getColDescription();
        collectionSettings.id = collection.getId();
        collectionSettings.filePrefix = collection.getFilePrefix();
        collectionSettings.namePrefix = collection.getNamePrefix();

        if(collection.getTraitOpt() != null) {
            for (String[] s : collection.getTraitOpt()) {
                collectionSettings.traitOpt.add(new String[]{s[0], s[1]});
            }
        }
        collectionSettings.startIndex = collection.getStartIndex();

        if (collection.getOutputDirectory() != null) {
            collectionSettings.outputDirectory = collection.getOutputDirectory().getAbsolutePath();
        }
        collectionSettings.size = collection.getSize();
        collectionSettings.width = collection.getWidth();
        collectionSettings.height = collection.getHeight();
        collectionSettings.size = collection.getSize();

        if(!collection.getLayerList().isEmpty()) {
            for (Layer l : collection.getLayerList()) {
                var layer = new JsonContainers.LayerSettings();
                layer.name = l.getName();
                layer.number = l.getNumber();

                if (!l.getImageList().isEmpty()) {
                    for (ImageFile i : l.getImageList()) {
                        var image = new JsonContainers.ImageSettings();
                        image.name = i.getName();
                        image.weight = i.getWeight();
                        image.max = i.getMax();
                        image.muteGroup = i.getMuteGroup();

                        if (i.getFile() != null) {
                            image.file = i.getFile().getAbsolutePath();
                        }
                        layer.imageList.add(image);
                    }
                }
                collectionSettings.layerList.add(layer);
            }
        }
        collectionSettings.flags = collection.getFlags();
        if (collection.getNameGen() != null) {
            var nameGen = new JsonContainers.NameGenSettings();
            nameGen.nameList = collection.getNameList().getAbsolutePath();
            nameGen.wordCount = collection.getNameGen().getWordCount();
            collectionSettings.nameGenSettings = nameGen;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try ( PrintWriter pw = new PrintWriter(new FileOutputStream(new File(file)))){
            pw.write(Util.fixPretty(gson.toJson(gson.toJson(collectionSettings))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Util.exception(FILE);
        }
    }

    public static void importSettings(File file) {
        Gson gson = new Gson();
        Collection collection = GeneratorController.collection;

        try {
            var reader = Files.newBufferedReader(Path.of(file.getAbsolutePath()));
            var collectionSettings
                    = gson.fromJson(reader, JsonContainers.CollectionSettings.class);

            collection.setName(collectionSettings.name);

            if (!collectionSettings.colAttributes.isEmpty()) {
                for (String[] s : collectionSettings.colAttributes) {
                    collection.getColAttributes().add(s);
                }
            }
            collection.setColDescription(collectionSettings.colDescription);
            collection.setId(collectionSettings.id);
            collection.setFilePrefix(collectionSettings.filePrefix);
            collection.setNamePrefix(collectionSettings.namePrefix);

            if (!collectionSettings.traitOpt.isEmpty()) {
                for (String[] s : collectionSettings.traitOpt) {
                    collection.getTraitOpt().add(s);
                }
            }
            collection.setStartIndex(collectionSettings.startIndex);
            collection.setOutputDirectory(new File(collectionSettings.outputDirectory));
            collection.setSize(collectionSettings.size);
            collection.setWidth(collectionSettings.width);
            collection.setHeight(collectionSettings.height);

            if (!collectionSettings.layerList.isEmpty()) {
                for (JsonContainers.LayerSettings ls : collectionSettings.layerList) {
                    Layer layer = new Layer(ls.name, ls.number);

                    if (!ls.imageList.isEmpty()) {
                        for (JsonContainers.ImageSettings is : ls.imageList) {
                            if (is.file.isEmpty()) {
                                var noneImage = new ImageFile();
                                noneImage.setWeight(is.weight);
                                noneImage.setMax(is.max);
                                noneImage.setMuteGroup(is.muteGroup);
                                layer.getImageList().add((noneImage.genNone(
                                        collection.getWidth(), collection.getHeight())));
                            } else {
                                ImageFile image = new ImageFile(is.name, new File(is.file), is.weight, is.max);
                                image.setMuteGroup(is.muteGroup);
                                layer.getImageList().add(image);
                            }
                        }
                    }
                    collection.getLayerList().add(layer);
                }
            }
            collection.setFlags(collectionSettings.flags);
            if (collectionSettings.nameGenSettings != null) {
                collection.setNameList(new File(collectionSettings.nameGenSettings.nameList));
                collection.setNameGen(collectionSettings.nameGenSettings.wordCount);
            }
        } catch (IOException e) {
            Util.exception(FILE);
            e.printStackTrace();
        }
    }
}
