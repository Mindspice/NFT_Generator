package collection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CollectionController {

    @FXML
    public TextArea meta_test_window, collection_description;
    @FXML
    public TextField collection_name;
    @FXML
    public RadioButton sensitive_content, name_random, name_unique, name_index, name_in_filename, desc_mirror, desc_index, desc_col_name,
            desc_nft_name;
    @FXML
    public TextField uuid;
    @FXML
    public TextField icon_url, banner_url, twitter_url, website_url, opt_type_1, opt_value_1, opt_type_2, opt_value_2, opt_type_3,
            opt_value_3, file_prefix, start_index, name_list, opt_trait_type_1, opt_trait_value_1, opt_trait_type_2, opt_trait_value_2,
            opt_trait_type_3, opt_trait_value_3;
    @FXML
    public Button open_name_list, meta_test, meta_save;



    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


}

