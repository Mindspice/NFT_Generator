package utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import main.Main;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static utility.Util.ErrorType.*;


public class Util {

    public static String nullHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    private static String lastOpened;
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
        NO_FILES,
        DIM,
        URL,
        SIZE_IMAGE,
        SIZE_META,
        SIZE_DIR,
        SIZE_MISMATCH,
        NO_VALIDATION,
        INVALID_HASH,
        NO_HASH

    }

    public enum FileFilter {
        PNG(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png")),
        TXT(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")),
        JSON(new FileChooser.ExtensionFilter("Json files (*.json)", "*.json"));

        public final ExtensionFilter ext;

        FileFilter(FileChooser.ExtensionFilter ext) {
            this.ext = ext;
        }
    }


    public static boolean isInt(String input) {
        try (Scanner sc = new Scanner(input)) {
            if (sc.hasNextInt()) {
                return true;
            } else {
                error(INPUT, input);
                return false;
            }
        }
    }

    public static boolean isDouble(String input) {
        try (Scanner sc = new Scanner(input)) {
            if (sc.hasNextDouble()) {
                return true;
            } else {
                error(INPUT, input);
                return false;
            }
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
            case BLOCKED -> error.setContentText("Already Generating A Collection");
            case STALL -> error.setContentText("Generation Stalled. Likely All Unique Combinations Have Been Exhausted");
            case LAYER -> error.setContentText("Create A Layer First");
            case DIM -> error.setContentText(" Dimensions Are Not Set");
            case NO_FILES -> error.setContentText("Directory Contains No Files");
            case URL -> error.setContentText("Invalid URL, Must Be Https To Validate\n" + input);
            case SIZE_IMAGE -> error.setContentText("Mis-Matched Lengths Of Image URI Lists");
            case SIZE_META -> error.setContentText("Mis-Matched Lengths Of Meta URI Lists");
            case SIZE_MISMATCH -> error.setContentText("Length of Image And Meta URI List Are Not The Same");
            case NO_VALIDATION -> error.setContentText("Nothing To Validate, Add Lists");
            case INVALID_HASH -> error.setContentText("Invalid " + input + " Hash(s) Found");
            case SIZE_DIR -> error.setContentText("Contents Of " + input + " Directory Less Than Image URI List");
            case NO_HASH -> error.setContentText("Must validate At Least One Hash To Mint");
        }
        error.showAndWait();
    }

    public static boolean confirm(ErrorType type) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");

        switch (type) {
            case REMOVE -> confirm.setContentText("Remove?");
            case EXIT -> confirm.setContentText("Exit? Non-Exported Changes Will Be Lost");
            case STOP -> confirm.setContentText("Stop Current Collection Generation?");
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
            case NAME_LIST -> error.setContentText("Provided Name List Is Too Short\nFor Enough Unique Generations\n" +
                    "Disabled Random/Unique Names\n Fix WordList/Word Count, Re-Enable & Save");
        }
        error.showAndWait();
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
        return fixed.substring(1, fixed.length() - 1);
    }

    public static File openFile(FileFilter ff) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(ff.ext);
        if (lastOpened != null) {
            fileChooser.setInitialDirectory(new File(lastOpened));
        }
        File file = fileChooser.showOpenDialog(Main.stage);
        lastOpened = file.getParent();
        return file;
    }

    public static File saveFile(FileFilter ff) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(ff.ext);
        return fileChooser.showSaveDialog(Main.stage);
    }

    public static File openDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (lastOpened != null) {
            directoryChooser.setInitialDirectory(new File(lastOpened));
        }
        File dir = directoryChooser.showDialog(Main.stage);
        lastOpened = dir.getParent();
        return dir;
    }

    public static List<String> readToList(File file) {
        List<String> stringList = new ArrayList<>();
        try (var inputStream = new FileInputStream(file); var sc = new Scanner(inputStream)) {
            while (sc.hasNextLine()) {
                stringList.add(sc.nextLine());
            }
        } catch (IOException e) {
            Util.exception(FILE);
            e.printStackTrace();
        }
        return stringList;
    }


}


