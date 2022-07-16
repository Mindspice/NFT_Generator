package generator;
import collection.Collection;
import imagefile.ImageFile;
import javafx.embed.swing.SwingFXUtils;
import layer.Layer;
import logic.*;
import main.Main;
import collection.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

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

public class GeneratorController {

    public Button regenerate;
    public Button collection_settings;
    @javafx.fxml.FXML
    private ImageView image_window;
    @javafx.fxml.FXML
    private TableView image_table, layer_table;
    @javafx.fxml.FXML
    private TableColumn image_table_file,image_table_max,image_table_name, image_table_weight,layer_table_number,
            layer_table_name,layer_table_occur,layer_table_amount,image_table_mute,image_table_link;
    @javafx.fxml.FXML
    private RadioButton generate_disregard_bg, generate_no_duplicates;
    @javafx.fxml.FXML
    private ProgressBar generation_progress;
    @javafx.fxml.FXML
    private TextField collection_name,collection_directory, collection_prefix, collection_size, collection_width,
            collection_height, layer_occur_number, layer_name, layer_number, image_name, image_file, image_weight,
            image_max,link_group,mute_group ;

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
        if (layerInFocus.getImageList() != null) {
            image_table.setItems(layerInFocus.getImageList());
            image_table_file.setCellValueFactory(new PropertyValueFactory("FileName"));
            image_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
            image_table_weight.setCellValueFactory(new PropertyValueFactory("Weight"));
            image_table_max.setCellValueFactory(new PropertyValueFactory("Max"));
            image_table_mute.setCellValueFactory(new PropertyValueFactory("MuteGroup"));

        }
    }

    private void setImageInFocus() {
        image_name.setText(imageInFocus.name);
        image_weight.setText(Double.toString(imageInFocus.weight));
        image_max.setText(Integer.toString(imageInFocus.max));
        image_file.setText(imageInFocus.getFileName());
        mute_group.setText(Integer.toString(imageInFocus.muteGroup));
        displayImage(imageInFocus.image);
    }

    /* Save functions for settings(attribute fields) */

    @javafx.fxml.FXML
    public void saveImage(ActionEvent actionEvent) {
        imageInFocus.name = image_name.getText();

        if (logic.Util.isDouble(image_weight.getText()) && logic.Util.isInt(image_max.getText())) {
            imageInFocus.weight = Double.parseDouble(image_weight.getText());
            imageInFocus.max = Integer.parseInt(image_max.getText());
            imageInFocus.muteGroup = Integer.parseInt(mute_group.getText());

            if (Double.parseDouble(image_weight.getText()) > 1.0 || Double.parseDouble(image_weight.getText()) < 0.0) {
                logic.Util.error("value", image_weight.getText());
            }
        }
        image_table.refresh();
    }

    @javafx.fxml.FXML
    public void saveLayer(ActionEvent actionEvent) {
        layerInFocus.setName(layer_name.getText());

        if (Util.isInt(layer_number.getText())) {
            layerInFocus.setNumber(Integer.parseInt(layer_number.getText()));
        }
        sortLayers();
        layer_table.refresh();
    }

    @javafx.fxml.FXML
    public void saveCollection(ActionEvent actionEvent) {
        if (Util.isInt(collection_size.getText()) && Util.isInt(collection_width.getText())
                && Util.isInt(collection_height.getText())) {

            collection.size = Integer.parseInt(collection_size.getText());
            collection.width = Integer.parseInt(collection_width.getText());
            collection.height = Integer.parseInt(collection_height.getText());
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
                File defaultDirectory = new File(imageInFocus.file.getParent());
                fileChooser.setInitialDirectory(defaultDirectory);
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(Main.getStage());
                imageInFocus.image = ImageIO.read(file);
                imageInFocus.file = file;
                imageInFocus.name = file.getName();
            } catch(Exception e){
                Util.exception("file");
            }
            image_table.refresh();
            displayImage(imageInFocus.image);
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
            layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
            collection_size.setText(Integer.toString(collection.size));
            collection_width.setText(Integer.toString(collection.width));
            collection_height.setText(Integer.toString(collection.height));
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
        collection.outputDirectory = directory;
        collection_directory.setText(directory.toString());
    }

    // Generates a random test nft to image display window
    @FXML
    public void generateTest(ActionEvent actionEvent) throws IOException {
        BufferedImage nftFile = new BufferedImage(collection.width, collection.height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = nftFile.getGraphics();

        HashMap<Integer,Boolean> muteTable = new HashMap<>();


        for (Layer l : layerList) {
            if (!l.getImageList().isEmpty()) {
                ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());

                // mute group
                int i = 0;
                if (image.muteGroup != 0 ){
                    while(muteTable.get(image.muteGroup) != null) {
                        image = WeightedRandom.getWeightedRandom(l.getImageList());
                        i++;
                        if (i == collection.size / 4) {
                            return;
                        }
                    }
                }
                muteTable.put(image.muteGroup, true);

                graphics.drawImage(image.image, 0, 0, null);
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
                List<String> layerNames = new ArrayList<>();
                for (Layer l : layerList) {
                    layerNames.add(l.getName());
                }
                try {
                    writeDataHeader(collection.outputDirectory, collection.name, layerNames);
                } catch (FileNotFoundException e) {
                    Util.exception("file");
                    e.printStackTrace();
                }

                while (iter <= collection.size && !stopGen) {
                    List<String> traitList = new ArrayList<>();
                    BufferedImage nftFile = new BufferedImage(collection.width, collection.height, BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = nftFile.getGraphics();
                    HashMap<Integer,Boolean> muteTable = new HashMap<>();


                    for (Layer l : layerList) {
                        if (!l.getImageList().isEmpty()) {
                            ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());
                            int i = 0;
                            // mute group
                            if (image.muteGroup != 0  && i < collection.size / 4){
                                while(muteTable.get(image.muteGroup) != null) {
                                    image = WeightedRandom.getWeightedRandom(l.getImageList());
                                    i++;
                                }
                            }
                            muteTable.put(image.muteGroup, true);


                            int j = 0; //handles the iterations of while loop to avoid endless, /4 is kind of random but should work
                            while(image.max != 0 && image.max <= image.count && j < collection.size / 4){
                                image = WeightedRandom.getWeightedRandom(l.getImageList());
                                j++;
                            }
                            graphics.drawImage(image.image, 0, 0, null);
                            traitList.add(image.name);
                            image.incCount();
                        }
                    }

                    graphics.dispose();
                    NFT nft = new NFT(collection.prefix + "_" + iter, traitList, generate_disregard_bg.isSelected());

                    if (generate_no_duplicates.isSelected()) {
                        if (dupeTable.get(nft.id) == null) {
                            dupeTable.put(nft.id, 1);
                            writeImageFile(nft, nftFile, iter);
                            ++iter;

                        }
                    } else {
                        writeImageFile(nft, nftFile, iter);
                        ++iter;
                    }

                    if (loop > (iter + (collection.size * 2))) {  //Check for Stall (Too many duplicate generations)
                        stopGen = true;
                        blockGen = false;
                        resetImageCount();
                        generation_progress.progressProperty().unbind();
                        Util.error("stall","");
                        break;
                    }
                    ++loop;
                    updateProgress(iter, collection.size);
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
        layerList.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer l1, Layer l2) {
                return Integer.compare(l1.getNumber(), l2.getNumber());
            }
        });
    }

    // Saves(writes) final files to disk
    private void writeImageFile(NFT nft, BufferedImage nftFile, int iter) {
        try {
            File finalImage = new File(collection.outputDirectory, collection.prefix  + iter + ".png");
            ImageIO.write(nftFile, "PNG", finalImage);
            nft.finalImage = finalImage.getAbsoluteFile();

            if (writeDataFile(collection.outputDirectory, collection.name,
                    finalImage.getCanonicalFile().toString(), nft.traitList)) {
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
    private boolean writeDataHeader(File collectionDir, String collectionName, List<String> layers) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(collectionDir + "/" +  collectionName + ".txt"), true));
        pw.append("FileName,");
        for (String s : layers) {
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
