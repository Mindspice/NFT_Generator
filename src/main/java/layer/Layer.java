package layer;


import imagefile.ImageFile;
import logic.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public  class Layer {
    private String name;
    private int number;
    private int occurrence;
    private int amount;
    public ObservableList<ImageFile> imageList = FXCollections.observableArrayList();




    public Layer(String name, int layerNumber, int occurrence) {
        this.name = name;
        this.number = layerNumber;
        this.occurrence = occurrence;
    }
    public void addImage(String name, File image, double weight, int max) throws IOException {
            imageList.add(new ImageFile(name, image, weight, max));
    }

    public ImageFile getImage(String name) {
        for (ImageFile i : imageList) {
            if (i.getName().equals(name)) {
                return i;
            }
        }
        return null;
    }

    public String getName(){
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public ObservableList getImageList() {
        return imageList;
    }

    public int getAmount() {
        return imageList.size();
    }


    public void setName(String name) {
            this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

}



