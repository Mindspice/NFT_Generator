package logic;

import collection.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imagefile.ImageFile;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import json.JsonContainers;
import layer.Layer;
import main.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static logic.Util.ErrorType.*;

public class Util {

    public enum ErrorType {
        INPUT,
        VALUE,
        EMPTY,
        EMPTY_IMG,
        BLOCKED,
        STALL,
        LAYER,
        DIR,
        FILE,
        UNKNOWN,
        CONFIRM,
        REMOVE,
        EXIT,
        STOP,
        NAME_LIST,
        DIM,
    }

    public static boolean isInt(String input) {
        Scanner sc = new Scanner(input);
        if (sc.hasNextInt()) {
            return true;
        } else {
            error(INPUT, input);
            return false;
        }
    }

    public static boolean isDouble(String input) {
        Scanner sc = new Scanner(input);
        if (sc.hasNextDouble()) {
            return true;
        } else {
            error(INPUT, input);
            return false;
        }
    }

    public static void error(ErrorType type, String input) {

        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        switch (type) {
            case INPUT -> error.setContentText("Input Of: " + input + " Is Invalid");
            case VALUE -> error.setContentText("Incorrect Value: " + input + " | Valid Inputs: 0.0 - 1.0");
            case EMPTY_IMG -> error.setContentText("Add Layers & Images First");
            case EMPTY -> error.setContentText("One Or More Required Fields Are Empty");
            case BLOCKED ->error.setContentText("Already Generating A Collection");
            case STALL -> error.setContentText("Generation Stalled. Likely All Unique Combinations Have Been Exhausted");
            case LAYER -> error.setContentText("Create A Layer First");
            case DIM -> error.setContentText(" Dimensions Are Not Set");
        }
        error.showAndWait();
    }

    public static boolean confirm(ErrorType type) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");

        switch (type) {
            case REMOVE -> confirm.setContentText("Remove?");
            case EXIT ->confirm.setContentText("Exit? Current Configuration Will Be Lost");
            case STOP ->confirm.setContentText("Stop Current Collection Generation?");
        }
        Optional<ButtonType> yesNo = confirm.showAndWait();
        return yesNo.isPresent() && yesNo.get() == ButtonType.OK;
    }

    public static void exception(ErrorType type) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Exception");
        switch (type) {
            case DIR -> error.setContentText("Invalid, Or Null Directory");
            case FILE -> error.setContentText("Error reading Or Writing File");
            case UNKNOWN -> error.setContentText("Unknown Error Encountered");
            case NAME_LIST ->error.setContentText("Provided Name List Is Too Short For Enough Unique Generations \n" +
                    "Disabled Unique Names, Fix WordList/Word Count, Re-enable & Save");
        }
        error.showAndWait();
    }

    public static void exportSettings(Collection collection, String file) {
        var collectionSettings = new JsonContainers.CollectionSettings();
//        collectionSettings.name = collection.getName();

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
        System.out.println(gson.toJson(collectionSettings));
        try {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(file)));

            pw.write(fixPretty(gson.toJson(gson.toJson(collectionSettings))));
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Util.exception(FILE);
        }
    }

    public static void importSettings(File file) {
        Gson gson = new Gson();

        Collection collection = Main.collection;
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
            collection.setDescription(collectionSettings.colDescription);
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

    public static String fixPretty(String json) {
        String fixed = json
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t")
                .replaceAll("\\\\b", "\b")
                .replaceAll("\\\\r", "\r")
                .replaceAll("\\\\f", "\f")
                .replaceAll("\\\\'", "'")
                .replaceAll("\\\\\"", "\"");
        return fixed.substring(1,fixed.length() - 1);
    }
}
