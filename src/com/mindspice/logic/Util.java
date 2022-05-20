package com.mindspice.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.Scanner;

public class Util {


    public static boolean isInt(String input) {
        Scanner sc = new Scanner(input);
        if (sc.hasNextInt()) {
            return true;
        } else {
            error("input", input);
            return false;
        }
    }

    public static boolean isDouble(String input) {
        Scanner sc = new Scanner(input);
        if (sc.hasNextDouble()) {
            return true;
        } else {
            error("input", input);
            return false;
        }
    }
    //Enum would be better, but I'm lazy and did it this way from the start for some reason
    public static void error(String type, String input) {

        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        switch (type) {
            case "input":
                error.setContentText("Input Of: " + input + " Is Invalid");
                break;
            case "value":
                error.setContentText("Incorrect Value: " + input + " | Valid Inputs: 0.0 - 1.0");
                break;
            case "empty":
                error.setContentText("Add Layers & Images First");
                break;
            case "blocked":
                error.setContentText("Already Generating A Collection");
                break;
            case "stall":
                error.setContentText("Generation Stalled. Likely All Unique Combinations Have Been Exhausted");
            case "layer":
                error.setContentText("Create A Layer First");

        }
        error.showAndWait();
    }

    public static boolean confirm(String type) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");

        switch (type) {
            case "remove":
                confirm.setContentText("Remove?");
                break;
            case "exit":
                confirm.setContentText("Exit? Current Configuration Will Be Lost");
                break;
            case "stop":
                confirm.setContentText("Stop Current Collection Generation?");
                break;
        }
        Optional<ButtonType> yesNo = confirm.showAndWait();
        return yesNo.isPresent() && yesNo.get() == ButtonType.OK;
    }

    public static void exception(String type) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Exception");
        switch (type) {
            case "directory":
                error.setContentText("Invalid, Or Null Directory");
                break;
            case "file":
                error.setContentText("Error reading Or Writing File");
                break;
            case "unknown":
                error.setContentText("Unknown Error Encountered");

        }
        error.showAndWait();
    }

}
