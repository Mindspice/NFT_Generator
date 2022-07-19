package logic;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
    //Enum would be better, but I'm lazy and did it this way from the start for some reason
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
            case NAME_LIST ->error.setContentText("Provided Name List Is Too Short For Enough Unique Generations");
        }
        error.showAndWait();
    }

}
