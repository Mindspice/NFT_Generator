package generator;

import collection.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imagefile.ImageFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import json.JsonContainers;
import json.MetaFactory;
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
    @FXML
    public TextArea meta_test_window, collection_description;
    @FXML
    public RadioButton sensitive_content, name_random, name_unique, name_index, name_in_filename, desc_mirror, desc_index,
            desc_col_name, desc_nft_name;
    @FXML
    public TextField collection_name, uuid, icon_url, banner_url, twitter_url, website_url, opt_type_1, opt_value_1, opt_type_2,
            opt_value_2, opt_type_3, opt_value_3, file_prefix, name_prefix, start_index, name_list, opt_trait_type_1,
            opt_trait_value_1, opt_trait_type_2, opt_trait_value_2, opt_trait_type_3, opt_trait_value_3,word_count;
    @FXML
    public Button open_name_list, meta_test, meta_save;


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

    public static Collection collection = new Collection();
    private static MetaFactory metaFactory = new MetaFactory(collection);
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Layer layerInFocus;
    private static ImageFile imageInFocus;
    ObservableList<Layer> layerList = collection.getLayerList();

    private static FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    private static boolean stopGen = false;
    private static boolean blockGen = false;



    private Task generate;

    void initialize(URL url, ResourceBundle resourceBundle) {
        layer_table.setItems(layerList);
        layer_table_number.setCellValueFactory(new PropertyValueFactory("Number"));
        layer_table_name.setCellValueFactory(new PropertyValueFactory("Name"));
        layer_table_amount.setCellValueFactory(new PropertyValueFactory("Amount"));
        collection_name.setText(collection.getName());
        collection_name.setText(collection.getColDescription());
        sensitive_content.setSelected(collection.isSensitiveContent());
        uuid.setText(collection.getId());
        initColAttributes();
        file_prefix.setText(collection.getFilePrefix());
        name_prefix.setText(collection.getNamePrefix());
        start_index.setText(String.valueOf(collection.getStartIndex()));
        name_random.setSelected(collection.isRandomNames());
        name_unique.setSelected(collection.isUniqueNames());
        name_index.setSelected(collection.isIndexInName());
        name_in_filename.setSelected(collection.isNameInFileName());
        if (collection.getNameList() != null) name_list.setText(collection.getNameList().getPath());
        if (collection.getNameGen() != null) word_count.setText(String.valueOf(collection.getNameGen().wordCount));
        desc_mirror.setSelected(collection.isMirrorColDesc());
        desc_index.setSelected(collection.isIndexInDesc());
        desc_col_name.setSelected(collection.isColNameInDesc());
        desc_nft_name.setSelected(collection.isNftNameInDesc());
        initStaticTraits();
        if (word_count.getText().isEmpty()) word_count.setText("2");
        if (start_index.getText().isEmpty()) start_index.setText("1");

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
            File directory = directoryChooser.showDialog(Main.getStage());
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
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(Main.getStage());
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
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(Main.getStage());
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
        layerList.add(new Layer("Layer_" + layerList.size(), layerList.size(), 1));
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
        File directory = directoryChooser.showDialog(Main.getStage());
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
                if (collection.getWidth() == 0 || collection.getHeight() == 0) {
                    Util.error(Util.ErrorType.DIM, "");
                    return false;
                }
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
                    + (collection.isNameInFileName() ? ("_" + nft.getName()) : ""));

            fileName = fileName.replaceAll("\\s", "_");
            File finalImage = new File(collection.getOutputDirectory(), fileName + ".png");

            ImageIO.write(nftFile, "PNG", finalImage);
            nft.setFinalImage(finalImage.getAbsoluteFile());

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

        pw.append(gson.toJson(nft.getMetaData()));
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


    public void loadConfig(ActionEvent actionEvent) {
    }

    public void saveConfig(ActionEvent actionEvent) {
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
        layerInFocus.getImageList().add(new ImageFile(collection.getWidth(), collection.getHeight()));
    }

    ////////////////////////////
    // Collection Controller //
    //////////////////////////

    public void genRandUUID(ActionEvent actionEvent) {
        String id = UUID.randomUUID().toString();
        uuid.setText(id);
        collection.setId(id);
    }

    public void genTestMeta(ActionEvent actionEvent) {
        saveCollection(actionEvent);
        meta_test_window.clear();

        var metaFactory = new MetaFactory(collection);
        System.out.println(collection.getColAttributes().size());

        var traits = new ArrayList<String[]>();
        for (int i = 1; i < 4; ++i) {
            traits.add(new String[]{"Example Type" + i, "Example Value" + i});
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var meta = metaFactory.getMeta(42069, traits);
        meta_test_window.setText(gson.toJson(meta));
    }

    public void saveCollection(ActionEvent actionEvent) {
        collection.resetAttributes();
        collection.setName(collection_name.getText());
        collection.setDescription((collection_description.getText()));
        collection.setSensitiveContent(sensitive_content.isSelected());
        collection.setId(uuid.getText());
        collection.setIcon(icon_url.getText());
        collection.setBanner(banner_url.getText());
        collection.setTwitter(twitter_url.getText());
        collection.setWebsite(website_url.getText());

        if (!opt_type_1.getText().isEmpty() && !opt_value_1.getText().isEmpty()) {
            var attr = new String[]{opt_type_1.getText(), opt_value_1.getText()};
            collection.getColAttributes().add(attr);
        }
        if (!opt_type_2.getText().isEmpty() && !opt_value_2.getText().isEmpty()) {
            var attr = new String[]{opt_type_2.getText(), opt_value_2.getText()};
            collection.getColAttributes().add(attr);
        }
        if (!opt_type_3.getText().isEmpty() && !opt_value_3.getText().isEmpty()) {
            var attr = new String[]{opt_type_3.getText(), opt_value_3.getText()};
            collection.getColAttributes().add(attr);
        }

        collection.setFilePrefix(file_prefix.getText());
        collection.setNamePrefix(name_prefix.getText());

        if (Util.isInt(start_index.getText())) {
            collection.setStartIndex(Integer.parseInt(start_index.getText()));
        }
        collection.setRandomNames(name_random.isSelected());
        collection.setUniqueNames(name_unique.isSelected());
        collection.setIndexInName(name_index.isSelected());
        collection.setNameInFileName(name_in_filename.isSelected());

        if (!opt_trait_type_1.getText().isEmpty() && !opt_trait_value_1.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_1.getText(), opt_trait_value_1.getText());
        }
        if (!opt_trait_type_2.getText().isEmpty() && !opt_trait_value_2.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_2.getText(), opt_trait_value_2.getText());
        }

        if (!opt_trait_type_3.getText().isEmpty() && !opt_trait_value_3.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_3.getText(), opt_trait_value_3.getText());
        }

        collection.setMirrorColDesc(desc_mirror.isSelected());
        collection.setIndexInDesc(desc_index.isSelected());
        collection.setColNameInDesc(desc_col_name.isSelected());
        collection.setNftNameInDesc(desc_nft_name.isSelected());

        if (collection.getNameList() == null) {
            collection.setUniqueNames(false);
            collection.setRandomNames(false);
            name_unique.setSelected(false);
            name_random.setSelected(false);
        }

        if (name_random.isSelected()) {
            try {
                collection.setNameGen(word_count.getText().isEmpty()
                        ? 2 : Integer.parseInt(word_count.getText()));
                word_count.setText(String.valueOf(collection.getNameGen().wordCount));
            } catch (IllegalArgumentException e) {
                Util.exception(Util.ErrorType.NAME_LIST);
            } catch (IOException e) {
                Util.exception(Util.ErrorType.FILE);
            }
        }

    }

    public void OpenNameList(ActionEvent actionEvent) {
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(Main.getStage());
            collection.setNameList(file);
            name_list.setText(file.getPath());
            if (collection.getNameGen() == null) {
                collection.setNameGen(word_count.getText().isEmpty()
                        ? 2 : Integer.parseInt(word_count.getText()));
            }
            word_count.setText(String.valueOf(collection.getNameGen().wordCount));
        } catch (Exception e) {
            Util.exception(Util.ErrorType.FILE);
            e.printStackTrace();
        }
    }

    private void initColAttributes() {
        if (collection.getColAttributes() == null) return;
        for(String[] s : collection.getColAttributes()) {

            switch (s[0]) {

                case "description":
                    collection_description.setText(s[1]);
                    continue;
                case "icon":
                    icon_url.setText(s[1]);
                    continue;
                case "banner":
                    banner_url.setText(s[1]);
                    continue;
                case "website":
                    website_url.setText(s[1]);
                    continue;
                case "twitter":
                    twitter_url.setText(s[1]);
                    continue;
            }
            if (opt_type_1.getText().isEmpty()) {
                opt_type_1.setText(s[0]);
                opt_value_1.setText(s[1]);
            } else if (opt_type_2.getText().isEmpty()) {
                opt_type_2.setText(s[0]);
                opt_value_2.setText(s[1]);
            } else if (opt_type_3.getText().isEmpty()) {
                opt_type_3.setText(s[0]);
                opt_value_3.setText(s[1]);
            }
        }
    }
    private void initStaticTraits() {
        if (collection.getTraitOpt() == null) return;
        for (String[] s : collection.getTraitOpt()) {
            if (opt_trait_type_1.getText().isEmpty()) {
                opt_trait_type_1.setText(s[0]);
                opt_trait_value_1.setText(s[1]);
            } else if (opt_trait_type_2.getText().isEmpty()) {
                opt_trait_type_2.setText(s[0]);
                opt_trait_value_2.setText(s[1]);
            } else if (opt_trait_type_3.getText().isEmpty()) {
                opt_trait_type_3.setText(s[0]);
                opt_trait_value_3.setText(s[1]);
            }
        }
    }



}