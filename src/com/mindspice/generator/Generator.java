package com.mindspice.generator;

import com.mindspice.Main;
import com.mindspice.collection.Collection;
import com.mindspice.collection.NFT;
import com.mindspice.imagefile.ImageFile;
import com.mindspice.layer.Layer;
import com.mindspice.logic.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Generator {

    @javafx.fxml.FXML
    private ImageView image_window;
    @javafx.fxml.FXML
    private TableView image_table, layer_table;
    @javafx.fxml.FXML
    private TableColumn image_table_file,image_table_max,image_table_name, image_table_weight,layer_table_number,
            layer_table_name,layer_table_occur,layer_table_amount;
    @javafx.fxml.FXML
    private RadioButton generate_disregard_bg, generate_no_duplicates;
    @javafx.fxml.FXML
    private ProgressBar generation_progress;
    @javafx.fxml.FXML
    private TextField collection_name,collection_directory, collection_prefix, collection_size, collection_width,
            collection_height, layer_occur_number, layer_name, layer_number, image_name, image_file, image_weight, image_max ;

    Collection collection = new Collection();
    Layer layerInFocus;
    ImageFile imageInFocus;
    ObservableList<Layer> layerList = FXCollections.observableArrayList();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    boolean stopGen = false;
    boolean blockGen = false;



    Task generate;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        layer_table.setItems(layerList);
        layer_table_number.setCellValueFactory(new PropertyValueFactory("Number"));
        layer_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
        layer_table_occur.setCellValueFactory(new PropertyValueFactory("Occurrence"));
        layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
    }

    /* JavaFX mouse events of image/layer table selection*/

    public void selectLayer(MouseEvent mouseEvent) {
        layerInFocus = layerList.get(layer_table.getSelectionModel().getSelectedIndex());
        setLayerInFocus();
    }

    public void selectImage(MouseEvent mouseEvent) {
        imageInFocus = (ImageFile) layerInFocus.getImageList().get(image_table.getSelectionModel().getSelectedIndex());
        setImageInFocus();
    }

    /* Setters for attribute field focus for editing selected layer or image */

    private void setLayerInFocus() {
        layer_name.setText(layerInFocus.getName());
        layer_number.setText(Integer.toString(layerInFocus.getNumber()));
        layer_occur_number.setText(Integer.toString(layerInFocus.getOccurrence()));
        if (layerInFocus.getImageList() != null) {
            image_table.setItems(layerInFocus.getImageList());
            image_table_file.setCellValueFactory(new PropertyValueFactory("FileName"));
            image_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
            image_table_weight.setCellValueFactory(new PropertyValueFactory("Weight"));
            image_table_max.setCellValueFactory(new PropertyValueFactory("Max"));
        }
    }

    private void setImageInFocus() {
        image_name.setText(imageInFocus.getName());
        image_weight.setText(Double.toString(imageInFocus.getWeight()));
        image_max.setText(Integer.toString(imageInFocus.getMax()));
        image_file.setText(imageInFocus.getFileName());
        displayImage(imageInFocus.getImage());
    }

    /* Save functions for settings(attribute fields) */

    @javafx.fxml.FXML
    public void saveImage(ActionEvent actionEvent) {
        imageInFocus.setName(image_name.getText());

        if (Util.isDouble(image_weight.getText()) && Util.isInt(image_max.getText())) {
            imageInFocus.setWeight(Double.valueOf(image_weight.getText()));
            imageInFocus.setMax(Integer.valueOf(image_max.getText()));
            if (Double.valueOf(image_weight.getText()) > 1.0 || Double.valueOf(image_weight.getText()) < 0.0) {
                Util.error("value", image_weight.getText());
            }
        }
        image_table.refresh();
    }

    @javafx.fxml.FXML
    public void saveLayer(ActionEvent actionEvent) {
        layerInFocus.setName(layer_name.getText());

        if (Util.isInt(layer_number.getText()) && Util.isInt(layer_occur_number.getText())) {
            layerInFocus.setNumber(Integer.valueOf(layer_number.getText()));
            layerInFocus.setOccurrence(Integer.valueOf(layer_occur_number.getText()));
        }
        sortLayers();
        layer_table.refresh();
    }

    @javafx.fxml.FXML
    public void saveCollection(ActionEvent actionEvent) {
        collection.setName(collection_name.getText());
        collection.setPrefix(collection_prefix.getText().trim());
        if (Util.isInt(collection_size.getText()) && Util.isInt(collection_width.getText())
                && Util.isInt(collection_height.getText())) {

            collection.setSize(Integer.valueOf(collection_size.getText()));
            collection.setWidth(Integer.valueOf(collection_width.getText()));
            collection.setHeight(Integer.valueOf(collection_height.getText()));
        }

    }

    //Imports a whole directory of images to populate a layer. 
    @javafx.fxml.FXML
    public void importLayerDir(ActionEvent actionEvent)  {

        if (layerInFocus == null) {
            Util.error("layer", "");
            return;
        }

        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(Main.getStage());
            File[] directoryCont = directory.listFiles();

            for (File f  :directoryCont) {
                if (f.getName().endsWith(".png")) {
                    layerInFocus.addImage(f.getName(), f, 1, 0);
                }
            }
        } catch(Exception e) {
            Util.exception("directory");
            e.printStackTrace();
        }
        layer_table.refresh();
    }

    // Opens file chooser to replace existing image
    @javafx.fxml.FXML
    public void replaceImageFile(ActionEvent actionEvent)  {

        
        if (imageInFocus != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                File defaultDirectory = new File(imageInFocus.getFile().getParent());
                fileChooser.setInitialDirectory(defaultDirectory);
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(Main.getStage());
                imageInFocus.setImage(ImageIO.read(file));
                imageInFocus.setFile(file);
                imageInFocus.setName(file.getName());
            } catch(Exception e){
                Util.exception("file");
            }
            image_table.refresh();
            displayImage(imageInFocus.getImage());
        }
    }


    // Opens file chooser to add a single image to layer
    @javafx.fxml.FXML
    public void importImage(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(Main.getStage());
            layerInFocus.addImage(file.getName(), file, 1, 0);
        } catch(Exception e)  {
            Util.exception("file");
            e.printStackTrace();
        }
        layer_table.refresh();
    }

    @javafx.fxml.FXML
    public void removeImage(ActionEvent actionEvent) {
        if (Util.confirm("remove")) {
            layerInFocus.getImageList().remove(image_table.getSelectionModel().getSelectedIndex());
            layer_table.refresh();
        }
    }

    @javafx.fxml.FXML
    public void newLayer(ActionEvent actionEvent) {
        layerList.add(new Layer("Layer_" + layerList.size(), layerList.size(), 1));
        layerInFocus = layerList.get(layerList.size() - 1);
        if (layerList.size() == 1) {
            layer_table.setItems(layerList);
            layer_table_number.setCellValueFactory(new PropertyValueFactory("Number"));
            layer_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
            layer_table_occur.setCellValueFactory(new PropertyValueFactory("Occurrence"));
            layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
            collection_name.setText(collection.getName());
            collection_prefix.setText(collection.getPrefix());
            collection_size.setText(Integer.toString(collection.getSize()));
            collection_width.setText(Integer.toString(collection.getWidth()));
            collection_height.setText(Integer.toString(collection.getHeight()));
        }
        sortLayers();
        setLayerInFocus();
        layer_table.refresh();
    }

    @javafx.fxml.FXML
    public void deleteLayer(ActionEvent actionEvent) {
        if(Util.confirm("remove")) {
            layerList.remove(layer_table.getSelectionModel().getSelectedIndex());
        }
    }

    // Sets directory to output finished collection files to
    @javafx.fxml.FXML
    public void setOutputDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(Main.getStage());
        collection.setOutputDirectory(directory);
        collection_directory.setText(directory.toString());
    }

    // Generates a random test nft to image display window
    @FXML
    public void generateTest(ActionEvent actionEvent) throws IOException {
        ArrayList<Layer> nftLayers = new ArrayList<>();
        BufferedImage nftFile = new BufferedImage(collection.getWidth(), collection.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = nftFile.getGraphics();
        Random random = new Random();
        int rng = random.nextInt(1000);  // random int selected to simulate layer occurrence.

        for (Layer l : layerList) {
            if (rng % l.getOccurrence() == 0) {
                nftLayers.add(l);
            }
        }

        for (Layer l : nftLayers) {
            if (!l.getImageList().isEmpty()) {
                ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());
                graphics.drawImage(image.getImage(), 0, 0, null);
            }
        }
        graphics.dispose();
        displayImage(nftFile);
    }

    // Check for existing generation and start the generator task if not.
    @javafx.fxml.FXML
    public void generateStart(ActionEvent actionEvent) {
        if (!blockGen) {
            if (!layerList.isEmpty()) {
                blockGen = true;
                generate = generator();
                generation_progress.progressProperty().bind(generate.progressProperty());
                Thread gen = new Thread(generate);
                gen.setDaemon(true);
                gen.start();
            } else{
                Util.error("empty", "");
            }
        } else {
            Util.error("blocked", "");
        }
    }
    // Stops generation on next iteration
    public void generateStop(ActionEvent actionEvent) {
        if (Util.confirm("stop")) {
            stopGen = true;
            blockGen = false;
            resetImageCount();
            generation_progress.progressProperty().unbind();
        }
    }

    private Task generator() {

        return new Task() {
            @Override
            protected Object call() {
                stopGen = false;
                int iter = 1;
                int loop = 1;
                HashMap<String, Integer> dupeTable = new HashMap<>();

                while (iter <= collection.getSize() && !stopGen) {
                    ArrayList<Layer> nftLayers = new ArrayList<>();
                    List<String> traitList = new ArrayList<>();
                    BufferedImage nftFile = new BufferedImage(collection.getWidth(), collection.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = nftFile.getGraphics();

                    for (Layer l : layerList) {
                        if (iter % l.getOccurrence() == 0 && !layerList.isEmpty()) {
                            nftLayers.add(l);
                        }
                    }

                    for (Layer l : nftLayers) {
                        if (!l.getImageList().isEmpty()) {
                            ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());
                            int i = 0; //handles the iterations of while loop to avoid endless, /4 is kind of random but should work
                            while(image.getMax() != 0 && image.getMax() <= image.getCount() && i < collection.getSize() / 4) {
                                image = WeightedRandom.getWeightedRandom(l.getImageList());
                                i++;
                            }
                            graphics.drawImage(image.getImage(), 0, 0, null);
                            traitList.add(l.getName() + ":" + image.getName());
                            image.incCount();
                        }
                    }

                    graphics.dispose();
                    NFT nft = new NFT(collection.getPrefix() + "_" + iter, traitList, generate_disregard_bg.isSelected());

                    if (generate_no_duplicates.isSelected()) {
                        if (dupeTable.get(nft.getID()) == null) {
                            dupeTable.put(nft.getID(), 1);
                            writeImageFile(nft, nftFile, iter);
                            ++iter;

                        }
                    } else {
                        writeImageFile(nft, nftFile, iter);
                        ++iter;
                    }
                    System.out.println(loop);
                    if (loop > (iter + (collection.getSize() * 2))) {  //Check for Stall (Too many duplicate generations)
                        stopGen = true;
                        blockGen = false;
                        resetImageCount();
                        break;
                    }
                    ++loop;
                    updateProgress(iter, collection.getSize());
                }

                resetImageCount();
                blockGen = false;
                return true;
            }
        };
    }

    /* Helper functions */

    // Draws passed image to the image display window
    private void displayImage(BufferedImage bImage) {
        Image image = SwingFXUtils.toFXImage(bImage, null);
        image_window.setImage(image);
    }

    private void sortLayers() {
        Collections.sort(layerList, new Comparator<Layer>() {
            @Override
            public int compare(Layer l1, Layer l2) {
                return Integer.valueOf(l1.getNumber()).compareTo(Integer.valueOf(l2.getNumber()));
            }
        });
    }

    // Saves(writes) final files to disk
    private void writeImageFile(NFT nft, BufferedImage nftFile, int iter) {
        try {
            File finalImage = new File(collection.getOutputDirectory(), collection.getPrefix()  + iter + ".png");
            ImageIO.write(nftFile, "PNG", finalImage);
            nft.setFinalImage(finalImage.getAbsoluteFile());

            if (writeDataFile(collection.getOutputDirectory(), collection.getName(),
                    finalImage.getCanonicalFile().toString(), nft.getTraitList())) {
                throw new IOException();
            }
            System.out.println(finalImage.getAbsoluteFile());
        } catch(IOException e) {
            Util.exception("file");
            e.printStackTrace();
        }
    }

    private boolean writeDataFile(File collectionDir, String collectionName, String nftFile, List<String> traits) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(collectionDir + "/" +  collectionName + ".txt"), true));
        pw.append(nftFile);
        for (String s : traits) {
            pw.append( "," + s);
        }
        pw.append("\n");
        pw.close();

        return pw.checkError();
    }

    private void resetImageCount() {
        for (Layer l : layerList) {
            List <ImageFile> layerImages = l.getImageList();
            for (ImageFile i : layerImages) {
                i.resetCount();
            }
        }
    }

}
