package layer;


import imagefile.ImageFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public  class Layer {
    private String name;
    private int number;

    private ObservableList<ImageFile> imageList = FXCollections.observableArrayList();

    public Layer(String name, int layerNumber) {
        this.name = name;
        this.number = layerNumber;
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

    public ObservableList<ImageFile> getImageList() {
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



}



