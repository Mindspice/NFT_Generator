package collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generator.GeneratorController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import json.JsonContainers;
import json.MetaFactory;
import logic.Util;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CollectionController implements Initializable {

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


    Collection collection = GeneratorController.collection;

    public void initialize(URL url, ResourceBundle resourceBundle) {

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
<<<<<<< Updated upstream
    public GeneratorController genCon;

    public void backToGen(ActionEvent actionEvent) {
        saveCollection(actionEvent);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Generator_GUI.fxml"));
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("SpiceGen Generator");

            Main.scene.setRoot(root);
            stage.show();
            genCon.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

=======
}
>>>>>>> Stashed changes
