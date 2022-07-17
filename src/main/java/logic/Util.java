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
        NAME_LIST
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
            case INPUT:
                error.setContentText("Input Of: " + input + " Is Invalid");
                break;
            case VALUE:
                error.setContentText("Incorrect Value: " + input + " | Valid Inputs: 0.0 - 1.0");
                break;
            case EMPTY_IMG:
                error.setContentText("Add Layers & Images First");
                break;
            case EMPTY:
                error.setContentText("Onne Or More Required Fields Are Empty");
                break;
            case BLOCKED:
                error.setContentText("Already Generating A Collection");
                break;
            case STALL:
                error.setContentText("Generation Stalled. Likely All Unique Combinations Have Been Exhausted");
                break;
            case LAYER:
                error.setContentText("Create A Layer First");
                break;

        }
        error.showAndWait();
    }

    public static boolean confirm(ErrorType type) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");

        switch (type) {
            case REMOVE:
                confirm.setContentText("Remove?");
                break;
            case EXIT:
                confirm.setContentText("Exit? Current Configuration Will Be Lost");
                break;
            case STOP:
                confirm.setContentText("Stop Current Collection Generation?");
                break;
        }
        Optional<ButtonType> yesNo = confirm.showAndWait();
        return yesNo.isPresent() && yesNo.get() == ButtonType.OK;
    }

    public static void exception(ErrorType type) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Exception");
        switch (type) {
            case DIR:
                error.setContentText("Invalid, Or Null Directory");
                break;
            case FILE:
                error.setContentText("Error reading Or Writing File");
                break;
            case UNKNOWN:
                error.setContentText("Unknown Error Encountered");
                break;
            case NAME_LIST:
                error.setContentText("Provided Name List Is Too Short For Enough Unique Generations");

        }
        error.showAndWait();
    }

}
