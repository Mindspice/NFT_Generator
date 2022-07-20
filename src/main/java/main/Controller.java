package main;

import collection.CollectionController;
import generator.GeneratorController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private CollectionController collectionTabController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GeneratorController.collectionController = collectionTabController;
    }
}
