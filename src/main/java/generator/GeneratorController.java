package generator;

import collection.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imagefile.ImageFile;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import json.MetaFactory;
import layer.Layer;
import logic.*;
import main.Controller;
import main.Main;
import collection.*;

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

    @FXML
    public Button collection_settings, generate_start, generate_stop, generate_test;
    @FXML
    private ImageView image_window;
    @javafx.fxml.FXML
    private TableView image_table, layer_table;
    @javafx.fxml.FXML
    private TableColumn image_table_file, image_table_max, image_table_name, image_table_weight, layer_table_number,
            layer_table_name, layer_table_amount, image_table_mute;
    @javafx.fxml.FXML
    private RadioButton generate_disregard_bg, generate_no_duplicates;
    @javafx.fxml.FXML
    private ProgressBar generation_progress;
    @javafx.fxml.FXML
    private TextField collection_directory, collection_size, collection_width,
            collection_height, layer_name, layer_number, image_name, image_file, image_weight,
            image_max, mute_group;

    public static CollectionController collectionController;

    private Collection collection = Main.collection;
    private MetaFactory metaFactory = new MetaFactory(collection);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ObservableList<Layer> layerList = collection.getLayerList();
    private FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    private FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("Json files (*.json)", "*.json");
    private Layer layerInFocus;
    private ImageFile imageInFocus;
    private boolean stopGen = false;
    private boolean blockGen = false;
    private Task generate;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    public void init() {
        layer_table.setItems(layerList);
        layer_table_number.setCellValueFactory(new PropertyValueFactory("Number"));
        layer_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
        layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
        collection_width.setText(String.valueOf(collection.getWidth()));
        collection_height.setText(String.valueOf(collection.getHeight()));
        collection_size.setText(String.valueOf(collection.getSize()));;
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
        image_name.setText(imageInFocus.getName());
        image_weight.setText(Double.toString(imageInFocus.getWeight()));
        image_max.setText(Integer.toString(imageInFocus.getMax()));
        image_file.setText(imageInFocus.getFileName());
        mute_group.setText(Integer.toString(imageInFocus.getMuteGroup()));
        displayImage(imageInFocus.getImage());
    }

    /* Save functions for settings(attribute fields) */

    @javafx.fxml.FXML
    public void saveImage(ActionEvent actionEvent) {
        imageInFocus.setName(image_name.getText());

        if (logic.Util.isDouble(image_weight.getText()) && logic.Util.isInt(image_max.getText())) {
            imageInFocus.setWeight(Double.parseDouble(image_weight.getText()));
            imageInFocus.setMax(Integer.parseInt(image_max.getText()));
            imageInFocus.setMuteGroup(Integer.parseInt(mute_group.getText()));

            if (Double.parseDouble(image_weight.getText()) > 1.0 || Double.parseDouble(image_weight.getText()) < 0.0) {
                logic.Util.error(Util.ErrorType.VALUE, image_weight.getText());
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
    public void saveGenSettings(ActionEvent actionEvent) {
        if (Util.isInt(collection_size.getText()) && Util.isInt(collection_width.getText())
                && Util.isInt(collection_height.getText())) {

            collection.setSize(Integer.parseInt(collection_size.getText()));
            collection.setWidth(Integer.parseInt(collection_width.getText()));
            collection.setHeight(Integer.parseInt(collection_height.getText()));
        }
    }

    //Imports a whole directory of images to populate a layer.
    @javafx.fxml.FXML
    public void importLayerDir(ActionEvent actionEvent) {

        if (layerInFocus == null) {
            Util.error(Util.ErrorType.LAYER, "");
            return;
        }
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(Main.stage);
            File[] directoryCont = directory.listFiles();
            for (File f : directoryCont) {
                if (f.getName().endsWith(".png")) {
                    layerInFocus.addImage(f.getName(), f, 1, 0);
                }
            }
        } catch (Exception e) {
            Util.exception(Util.ErrorType.DIR);
            e.printStackTrace();
        }
        layer_table.refresh();
    }

    // Opens file chooser to replace existing image
    @javafx.fxml.FXML
    public void replaceImageFile(ActionEvent actionEvent) {


        if (imageInFocus != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                File defaultDirectory = new File(imageInFocus.getFile().getParent());
                fileChooser.setInitialDirectory(defaultDirectory);
                fileChooser.getExtensionFilters().add(pngFilter);
                File file = fileChooser.showOpenDialog(Main.stage);
                imageInFocus.setImage(ImageIO.read(file));
                imageInFocus.setFile(file);
                imageInFocus.setName(file.getName());
            } catch (Exception e) {
                Util.exception(Util.ErrorType.FILE);
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
            fileChooser.getExtensionFilters().add(pngFilter);
            File file = fileChooser.showOpenDialog(Main.stage);
            layerInFocus.addImage(file.getName(), file, 1, 0);
        } catch (Exception e) {
            Util.exception(Util.ErrorType.FILE);
            e.printStackTrace();
        }
        layer_table.refresh();
    }

    @javafx.fxml.FXML
    public void removeImage(ActionEvent actionEvent) {
        if (Util.confirm(Util.ErrorType.REMOVE)) {
            layerInFocus.getImageList().remove(image_table.getSelectionModel().getSelectedIndex());
            layer_table.refresh();
        }
    }

    @javafx.fxml.FXML
    public void newLayer(ActionEvent actionEvent) {
        layerList.add(new Layer("Layer_" + layerList.size(), layerList.size()));
        layerInFocus = layerList.get(layerList.size() - 1);
        if (layerList.size() == 1) {
            layer_table.setItems(layerList);
            layer_table_number.setCellValueFactory(new PropertyValueFactory("Number"));
            layer_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
            layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
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
        if (Util.confirm(Util.ErrorType.REMOVE)) {
            layerList.remove(layer_table.getSelectionModel().getSelectedIndex());
        }
    }

    // Sets directory to output finished collection files to
    @javafx.fxml.FXML
    public void setOutputDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(Main.stage);
        collection.setOutputDirectory(directory);
        collection_directory.setText(directory.toString());
    }

    // Generates a random test nft to image display window
    @FXML
    public void generateTest(ActionEvent actionEvent) throws IOException {
        BufferedImage nftFile = new BufferedImage(collection.getWidth(), collection.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = nftFile.getGraphics();

        HashMap<Integer, Boolean> muteTable = new HashMap<>();

        for (Layer l : layerList) {
            if (!l.getImageList().isEmpty()) {
                ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());

                // mute group
                int i = 0;
                if (image.getMuteGroup() != 0) {
                    while (muteTable.get(image.getMuteGroup()) != null) {
                        image = WeightedRandom.getWeightedRandom(l.getImageList());
                        i++;
                        if (i == collection.getSize() / 4) {
                            return;
                        }
                    }
                }
                muteTable.put(image.getMuteGroup(), true);
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
            if (collection.getWidth() == 0 || collection.getHeight() == 0) {
                Util.error(Util.ErrorType.DIM, "");
            }
            // TODO throw unique check here
            if (!layerList.isEmpty()) {
                blockGen = true;
                generate = generator();
                generation_progress.progressProperty().bind(generate.progressProperty());
                Thread gen = new Thread(generate);
                gen.setDaemon(true);
                gen.start();
            } else {
                Util.error(Util.ErrorType.EMPTY_IMG, "");
            }
        } else {
            Util.error(Util.ErrorType.BLOCKED, "");
        }
    }

    // Stops generation on next iteration
    public void generateStop(ActionEvent actionEvent) {
        if (Util.confirm(Util.ErrorType.STOP)) {
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
                int loop = 0;
                HashMap<String, Integer> dupeTable = new HashMap<>();
                List<String> layerNames = new ArrayList<>();
                for (Layer l : layerList) {
                    layerNames.add(l.getName());
                }
                try {
                    writeDataHeader(collection.getOutputDirectory(), collection.getName(), layerNames);
                } catch (FileNotFoundException e) {
                    Util.exception(Util.ErrorType.FILE);
                    e.printStackTrace();
                }

                while (iter <= collection.getSize() && !stopGen) {
                    List<String[]> traitList = new ArrayList<>();
                    BufferedImage nftFile = new BufferedImage(collection.getWidth(), collection.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics graphics = nftFile.getGraphics();
                    HashMap<Integer, Boolean> muteTable = new HashMap<>();

                    for (Layer l : layerList) {
                        if (!l.getImageList().isEmpty()) {
                            ImageFile image = WeightedRandom.getWeightedRandom(l.getImageList());
                            int i = 0;
                            // mute group
                            if (image.getMuteGroup() != 0) {
                                while (muteTable.get(image.getMuteGroup()) != null) {
                                    image = WeightedRandom.getWeightedRandom(l.getImageList());
                                    i++;
                                    if (i < collection.getSize() / 4) return false;
                                }
                            }
                            muteTable.put(image.getMuteGroup(), true);

                            int j = 0; //handles the iterations of while loop to avoid endless, /4 is kind of random but should work
                            if (image.getMuteGroup() == 0) {
                                while (image.getMax() != 0 && image.getMax() <= image.getCount() && j < collection.getSize() / 4) {
                                    image = WeightedRandom.getWeightedRandom(l.getImageList());
                                    j++;
                                }
                            }
                            graphics.drawImage(image.getImage(), 0, 0, null);
                            traitList.add(new String[]{l.getName(), image.getName()});
                            image.incCount();
                        }
                    }
                    graphics.dispose();
                    var metaData = metaFactory.getMeta(collection.getStartIndex() + iter, traitList);
                    NFT nft = new NFT(metaData, collection.getStartIndex() + iter, traitList, generate_disregard_bg.isSelected());

                    if (generate_no_duplicates.isSelected()) {
                        if (dupeTable.get(nft.getID()) == null) {
                            dupeTable.put(nft.getID(), 1);
                            writeImageFile(nft, nftFile);
                            ++iter;
                        }
                    } else {
                        writeImageFile(nft, nftFile);
                        ++iter;
                    }

                    if (loop > (iter + (collection.getSize() * 2))) {  //Check for Stall (Too many duplicate generations)
                        stopGen = true;
                        blockGen = false;
                        resetImageCount();
                        generation_progress.progressProperty().unbind();
                        Util.error(Util.ErrorType.STALL, "");
                        loop = 0;
                        return false;
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
        layerList.sort(new Comparator<Layer>() {
            @Override
            public int compare(Layer l1, Layer l2) {
                return Integer.compare(l1.getNumber(), l2.getNumber());
            }
        });
    }

    // Saves(writes) final files to disk
    private void writeImageFile(NFT nft, BufferedImage nftFile) {
        try {
            String fileName = (collection.getFilePrefix() + "_" + nft.getIndex()
                    + (collection.getFlags().nameInFileName ? ("_" + nft.getName()) : ""));

            fileName = fileName.replaceAll("\\s", "_");
            File finalImage = new File(collection.getOutputDirectory(), fileName + ".png");
            ImageIO.write(nftFile, "PNG", finalImage);

            if (writeDataFile(finalImage.getCanonicalFile().toString(), nft.getTraitList())) throw new IOException();
            if (writeJsonFile(nft, fileName)) throw new IOException();
        } catch (IOException e) {
            Util.exception(Util.ErrorType.FILE);
            e.printStackTrace();
        }
    }

    public boolean writeJsonFile(NFT nft, String fileName) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(collection.getOutputDirectory() + "/"
                + fileName + ".json"), true));

        pw.append(Util.fixPretty(gson.toJson(nft.getMetaData())));
        pw.close();

        return pw.checkError();
    }

    private boolean writeDataFile(String nftFile, List<String[]> traits) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(collection.getOutputDirectory() + "/"
                + collection.getName() + ".txt"), true));

        pw.append(nftFile);
        for (String[] s : traits) {
            pw.append(",").append(s[1]);
        }
        pw.append("\n");
        pw.close();

        return pw.checkError();
    }

    private boolean writeDataHeader(File collectionDir, String collectionName, List<String> layers) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(collectionDir + "/" + collectionName + ".txt"), true));
        pw.append("FileName,");
        for (String s : layers) {
            pw.append(",").append(s);
        }
        pw.append("\n");
        pw.close();

        return pw.checkError();
    }

    private void resetImageCount() {
        for (Layer l : layerList) {
            List<ImageFile> layerImages = l.getImageList();
            for (ImageFile i : layerImages) {
                i.resetCount();
            }
        }
    }

    public void genNoneImage(ActionEvent actionEvent) throws IOException {
        if (collection.getWidth() == 0 || collection.getHeight() == 0) {
            Util.error(Util.ErrorType.DIM, "");
            return;
        }
        if (layerInFocus == null) {
            Util.error(Util.ErrorType.LAYER, "");
            return;
        }
        var noneImage = new ImageFile();
        layerInFocus.getImageList().add((noneImage.genNone(collection.getWidth(), collection.getHeight())));
    }


    public void importConfig(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(jsonFilter);
        File file = fileChooser.showOpenDialog(Main.stage);
        Util.importSettings(file);
        collectionController.init();
        init();

    }

    public void exportConfig(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(jsonFilter);
        File file = fileChooser.showSaveDialog(Main.stage);
        Util.exportSettings(collection,file.getAbsolutePath());
    }
}