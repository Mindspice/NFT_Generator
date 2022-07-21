package collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import json.MetaFactory;
import utility.Util;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CollectionController implements Initializable {

    @FXML
    public TextArea meta_test_window, collection_description;
    @FXML
    public RadioButton sensitive_content, name_random, name_unique, name_index, name_in_filename, desc_mirror, desc_index,
            desc_col_name, desc_nft_name, seriesAsTrait, seriesAsAttribute;
    @FXML
    public TextField collection_name, uuid, icon_url, banner_url, twitter_url, website_url, opt_type_1, opt_value_1, opt_type_2,
            opt_value_2, opt_type_3, opt_value_3, file_prefix, name_prefix, start_index, name_list, opt_trait_type_1,
            opt_trait_value_1, opt_trait_type_2, opt_trait_value_2, opt_trait_type_3, opt_trait_value_3,word_count;
    @FXML
    public Button open_name_list, meta_test, meta_save;

    Collection collection = Main.collection;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }

    public void init() {
        collection_name.setText(collection.getName());
        collection_name.setText(collection.getColDescription());
        sensitive_content.setSelected(collection.getFlags().sensitiveContent);
        uuid.setText(collection.getId());
        initColAttributes();
        file_prefix.setText(collection.getFilePrefix());
        name_prefix.setText(collection.getNamePrefix());
        start_index.setText(String.valueOf(collection.getStartIndex()));
        name_random.setSelected(collection.getFlags().randomNames);
        name_unique.setSelected(collection.getFlags().uniqueNames);
        name_index.setSelected(collection.getFlags().indexInName);
        name_in_filename.setSelected(collection.getFlags().nameInFileName);
        if (collection.getNameList() != null) name_list.setText(collection.getNameList().getPath());
        if (collection.getNameGen() != null) word_count.setText(String.valueOf(collection.getNameGen().getWordCount()));
        desc_mirror.setSelected(collection.getFlags().mirrorColDesc);
        desc_index.setSelected(collection.getFlags().indexInDesc);
        desc_col_name.setSelected(collection.getFlags().colNameInDesc);
        desc_nft_name.setSelected(collection.getFlags().nftNameInDesc);
        seriesAsTrait.setSelected(collection.getFlags().seriesAsTrait);
        seriesAsAttribute.setSelected(collection.getFlags().seriesAsAttribute);
        initStaticTraits();
        word_count.setText(collection.getNameGen() == null ? "2" : String.valueOf(collection.getNameGen().getWordCount()));
        start_index.setText(String.valueOf(collection.getStartIndex()));
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
        var meta = metaFactory.getMeta(ThreadLocalRandom.current()
                .nextInt(0,collection.getSize()), traits);
        meta_test_window.setText(gson.toJson(meta));
    }

    public void saveCollection(ActionEvent actionEvent) {
        collection.resetAttributes();
        collection.setName(collection_name.getText());
        collection.setDescription((collection_description.getText()));
        collection.getFlags().sensitiveContent = sensitive_content.isSelected();
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
        collection.getFlags().randomNames = name_random.isSelected();
        collection.getFlags().uniqueNames = name_unique.isSelected();
        collection.getFlags().indexInName = name_index.isSelected();
        collection.getFlags().nameInFileName = name_in_filename.isSelected();

        if (!opt_trait_type_1.getText().isEmpty() && !opt_trait_value_1.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_1.getText(), opt_trait_value_1.getText());
        }
        if (!opt_trait_type_2.getText().isEmpty() && !opt_trait_value_2.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_2.getText(), opt_trait_value_2.getText());
        }

        if (!opt_trait_type_3.getText().isEmpty() && !opt_trait_value_3.getText().isEmpty()) {
            collection.addTraitOpt(opt_trait_type_3.getText(), opt_trait_value_3.getText());
        }


        collection.getFlags().mirrorColDesc = desc_mirror.isSelected();
        collection.getFlags().indexInDesc = desc_index.isSelected();
        collection.getFlags().colNameInDesc = desc_col_name.isSelected();
        collection.getFlags().nftNameInDesc = desc_nft_name.isSelected();
        collection.getFlags().seriesAsTrait = seriesAsTrait.isSelected();
        collection.getFlags().seriesAsAttribute = seriesAsAttribute.isSelected();

        if (collection.getNameList() == null) {
            collection.getFlags().uniqueNames = false;
            collection.getFlags().randomNames = false;
            name_unique.setSelected(false);
            name_random.setSelected(false);
        }
        if (name_random.isSelected()) {
            try {
                if(collection.getNameGen() == null) {
                    collection.setNameGen(word_count.getText().isEmpty()
                            ? 2 : Integer.parseInt(word_count.getText()));
                }
                    collection.getNameGen().setWordCount(Integer.parseInt(word_count.getText()));
            } catch (IllegalArgumentException e) {
                Util.exception(Util.ErrorType.NAME_LIST);
                name_unique.setSelected(false);
                name_random.setSelected(false);
            } catch (IOException e) {
                Util.exception(Util.ErrorType.FILE);
            }
        }

    }

    public void OpenNameList(ActionEvent actionEvent) {
        try {
            File file = Util.openFile(Util.FileFilter.TXT);
            collection.setNameList(file);
            name_list.setText(file.getPath());
            if (collection.getNameGen() == null) {
                collection.setNameGen(word_count.getText().isEmpty()
                        ? 2 : Integer.parseInt(word_count.getText()));
            }
            word_count.setText(String.valueOf(collection.getNameGen().getWordCount()));
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

