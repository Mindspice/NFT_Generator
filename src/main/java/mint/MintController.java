package mint;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import json.JsonContainers;
import utility.HashFunc;
import utility.Util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class MintController implements Initializable {
    @FXML
    public TextField img_list_1, img_list_2, img_list_3, img_list_4, img_list_5, meta_list_1, meta_list_2, meta_list_3, meta_list_4,
            meta_list_5, license_1, license_2, license_3, license_4, license_5, image_dir, meta_dir, wallet_id, royalty_amount,
            royalty_address, target_address, fee_amount, editions_amount, output_dir;
    @FXML
    public RadioButton valid_img_1, valid_img_2, valid_img_3, valid_img_4, valid_img_5, valid_meta_1, valid_meta_2, valid_meta_3,
            valid_meta_4, valid_meta_5, valid_license_1, valid_license_2, valid_license_3, valid_license_4, valid_license_5,
            valid_image_dir, valid_meta_dir, has_editions, valid_hashes;
    @FXML
    public TextArea error_console;
    @FXML
    public ProgressBar hash_progress;

    private final MintSettings mintSettings = new MintSettings();
    private final int threads = Math.min((Runtime.getRuntime().availableProcessors() / 2), 10);
    private final ExecutorService hashExecutor = Executors.newFixedThreadPool(threads);
    public URIContainer[] uriContainers;


    @FXML
    RadioButton[] metaFlags;
    @FXML
    RadioButton[] imgFlags;
    @FXML
    RadioButton[] licenseFlags;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        metaFlags = new RadioButton[]{valid_meta_1, valid_meta_2, valid_meta_3, valid_meta_4, valid_meta_5};
        imgFlags = new RadioButton[]{valid_img_1, valid_img_2, valid_img_3, valid_img_4, valid_img_5};
        licenseFlags = new RadioButton[]{valid_license_1, valid_license_2, valid_license_3, valid_license_4, valid_license_5};
    }


    public void openImg1(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        img_list_1.setText(uriFile.getAbsolutePath());
        mintSettings.imageURIs[0] = Util.readToList(uriFile);
    }

    public void openImg2(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        img_list_2.setText(uriFile.getAbsolutePath());
        mintSettings.imageURIs[1] = Util.readToList(uriFile);
    }

    public void openImg3(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        img_list_3.setText(uriFile.getAbsolutePath());
        mintSettings.imageURIs[2] = Util.readToList(uriFile);
    }

    public void openImg4(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        img_list_4.setText(uriFile.getAbsolutePath());
        mintSettings.imageURIs[3] = Util.readToList(uriFile);
    }

    public void openImg5(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        img_list_5.setText(uriFile.getAbsolutePath());
        mintSettings.imageURIs[4] = Util.readToList(uriFile);
    }

    public void openMeta1(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        meta_list_1.setText(uriFile.getAbsolutePath());
        mintSettings.metaURIs[0] = Util.readToList(uriFile);
    }

    public void openMeta2(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        meta_list_2.setText(uriFile.getAbsolutePath());
        mintSettings.metaURIs[1] = Util.readToList(uriFile);
    }

    public void openMeta3(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        meta_list_3.setText(uriFile.getAbsolutePath());
        mintSettings.metaURIs[2] = Util.readToList(uriFile);
    }

    public void openMeta4(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        meta_list_4.setText(uriFile.getAbsolutePath());
        mintSettings.metaURIs[3] = Util.readToList(uriFile);
    }

    public void openMeta5(ActionEvent actionEvent) {
        File uriFile = Util.openFile(Util.FileFilter.TXT);
        meta_list_5.setText(uriFile.getAbsolutePath());
        mintSettings.metaURIs[4] = Util.readToList(uriFile);
    }

    public void saveLicense1(ActionEvent actionEvent) {
        mintSettings.licenseURIs[0] = license_1.getText();
    }

    public void saveLicense2(ActionEvent actionEvent) {
        mintSettings.licenseURIs[1] = license_2.getText();
    }

    public void saveLicense3(ActionEvent actionEvent) {
        mintSettings.licenseURIs[2] = license_3.getText();
    }

    public void saveLicense4(ActionEvent actionEvent) {
        mintSettings.licenseURIs[3] = license_4.getText();
    }

    public void saveLicense5(ActionEvent actionEvent) {
        mintSettings.licenseURIs[4] = license_5.getText();
    }

    public void OpenImgDir(ActionEvent actionEvent) {
        File dir = Util.openDirectory();
        image_dir.setText(dir.getAbsolutePath());
        mintSettings.imageDir = dir;
    }

    public void OpenMetaDir(ActionEvent actionEvent) {
        File dir = Util.openDirectory();
        meta_dir.setText(dir.getAbsolutePath());
        mintSettings.metaDir = dir;
    }

    public void openOutputDir(ActionEvent actionEvent) {
        File dir = Util.openDirectory();
        output_dir.setText(dir.getAbsolutePath());
        mintSettings.outputDir = dir;
    }

    public void didFee(ActionEvent actionEvent) {
        fee_amount.setText(String.valueOf(615000000));
    }

    public void nonDidFee(ActionEvent actionEvent) {
        fee_amount.setText(String.valueOf(265000000));
    }

    public void checkHashes(ActionEvent actionEvent) {
        error_console.clear();
        uriContainers = createURIContainers();
        if (uriContainers == null) {
            Util.exception(Util.ErrorType.UNKNOWN);
            return;
        }
        var hashTasks = getHashTasks(uriContainers);

        hash_progress.progressProperty().bind(
                new DoubleBinding() {
                    {
                        for (Task<Void> task : hashTasks) {
                            bind(task.progressProperty());
                        }
                    }
                    @Override
                    protected double computeValue() {
                       int activeCount = (((ThreadPoolExecutor) hashExecutor).getActiveCount());
                        return (hashTasks.stream().mapToDouble(Task::getProgress).sum() - threads + activeCount)
                                / activeCount;
                    }
                });

        for (var task : hashTasks) {
            hashExecutor.execute(task);
        }

        var waitTillDone = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (((ThreadPoolExecutor) hashExecutor).getActiveCount() > 0) {
                    Thread.sleep(1000);
                }
                Platform.runLater(() -> printErrors(uriContainers));
                return null;
            }
        };
        Thread t = new Thread(waitTillDone);
        t.setDaemon(true);
        t.start();
        validateLicenses();
    }

    public void generateRpc(ActionEvent actionEvent) {

        checkHashes(new ActionEvent());
        var waitTillDone = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (((ThreadPoolExecutor) hashExecutor).getActiveCount() > 0) {
                    Thread.sleep(1000);
                }
                Platform.runLater(() -> writeRpc());
                return null;
            }
        };
        Thread t = new Thread(waitTillDone);
        t.setDaemon(true);
        t.start();

    }

    private List<Task<Void>> getHashTasks(URIContainer[] uriContainers) {
        List<Task<Void>> taskList = new ArrayList<>();
        int batchSize = uriContainers.length / threads;
        //TODO needs a fix for final task use length

        for (int i = 0; i < threads; ++i) {
            if (i == 0) {
                taskList.add(validate(uriContainers, 0, batchSize - 1));
            } else {
                taskList.add(validate(uriContainers, batchSize * i, (batchSize * (i + 1) - 1)));
            }
        }
        return taskList;
    }

    private Task<Void> validate(URIContainer[] uriList, int start, int end) {

        return new Task<>() {
            @Override
            protected Void call() {
                for (int i = start; i <= end; ++i) {
                    uriList[i].validateHashes();
                    updateProgress(i - start, (end - start));
                }
                return null;
            }
        };
    }

    private URIContainer[] createURIContainers() {
        int imgLength = 0;
        int metaLength = 0;

        for (var uriList : mintSettings.imageURIs) {
            if (uriList != null) {
                if (imgLength != 0 && uriList.size() != imgLength) {
                    Util.error(Util.ErrorType.SIZE_IMAGE, "");
                    return null;
                }
                imgLength = uriList.size();
            }
        }

        for (var uriList : mintSettings.metaURIs) {
            if (uriList != null) {
                if (metaLength != 0 && uriList.size() != metaLength) {
                    Util.error(Util.ErrorType.SIZE_META, "");
                    return null;
                }
                metaLength = uriList.size();
            }
        }

        if (metaLength == 0 && imgLength == 0) {
            Util.error(Util.ErrorType.NO_VALIDATION, "");
            return null;
        }

        var uriContainers = new URIContainer[imgLength];

        for (int i = 0; i < uriContainers.length; ++i) {
            uriContainers[i] = new URIContainer();
        }

        for (int i = 0; i < 5; ++i) {
            if (imgFlags[i].isSelected()) {
                for (int j = 0; j < imgLength; ++j) {
                    if (mintSettings.imageURIs[i] == null
                            || mintSettings.imageURIs[i].get(j) == null
                            || (mintSettings.imageURIs[i].get(j).isBlank())) continue;

                    uriContainers[j].imgURIs.add(mintSettings.imageURIs[i].get(j));
                }
            }

            if (metaFlags[i].isSelected()) {
                for (int j = 0; j < metaLength; ++j) {
                    if (mintSettings.metaURIs[i] == null
                            || mintSettings.metaURIs[i].get(j) == null
                            || (mintSettings.metaURIs[i].get(j).isBlank())) continue;

                    uriContainers[j].metaURIs.add(mintSettings.metaURIs[i].get(j));
                }
            }
        }

        if (metaLength != imgLength) {
            Util.error(Util.ErrorType.SIZE_MISMATCH, "");
            return null;
        }

        if (valid_image_dir.isSelected()) {
            List<byte[]> imgByteList = getByteList(mintSettings.imageDir, ".png");
            if (imgByteList.size() != imgLength) {
                Util.error(Util.ErrorType.SIZE_DIR, "Image");
                return null;
            }
            for (int i = 0; i < imgLength; ++i) {
                uriContainers[i].imgFile = imgByteList.get(i);
            }
        }

        if (valid_meta_dir.isSelected()) {
            List<byte[]> metaByteList = getByteList(mintSettings.metaDir, ".json");
            if (metaByteList.size() != metaLength) {
                Util.error(Util.ErrorType.SIZE_DIR, "Meta");
                return null;
            }
            for (int i = 0; i < metaLength; ++i) {
                uriContainers[i].metaFile = metaByteList.get(i);
            }
        }
        return uriContainers;
    }

    private void validateLicenses() {
        //TODO validate against null  if checked and empty

        try {
            for (int i = 0; i < mintSettings.licenseURIs.length; ++i) {
                if (licenseFlags[i].isSelected()) {
                    if (mintSettings.licenseURIs[i] == null || mintSettings.licenseURIs[i].isBlank()) continue;
                    if (mintSettings.licenseHash == null) {
                        mintSettings.licenseHash = HashFunc.getHash(HashFunc.fetchBytes(new URL(mintSettings.licenseURIs[i])));
                    } else if (!mintSettings.licenseHash.equals(
                            HashFunc.getHash(HashFunc.fetchBytes(new URL(mintSettings.licenseURIs[i]))))) {
                        error_console.appendText("INVALID LICENSE HASH FOUND!\n");
                        error_console.appendText(mintSettings.licenseURIs[i] + "\n");
                        Util.error(Util.ErrorType.INVALID_HASH, "License");
                    } else {
                        error_console.appendText("License Hashes Valid!\n");
                    }
                }
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
            Util.error(Util.ErrorType.URL, "License URL");
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    private List<byte[]> getByteList(File directory, String ext) {
        File[] directoryCont = directory.listFiles();
        List<byte[]> byteList = new ArrayList<>();

        if (directoryCont == null) {
            Util.error(Util.ErrorType.NO_FILES, "");
            return byteList;
        }
        try {
            for (File f : directoryCont) {
                if (f.getName().endsWith(ext)) {
                    byteList.add(Files.readAllBytes(Path.of(f.getPath())));
                }
            }
        } catch (IOException e) {
            Util.exception(Util.ErrorType.FILE);
            e.printStackTrace();
        }
        return byteList;
    }

    private void printErrors(URIContainer[] uriContainers) {
        boolean error = false;
        for (int i = 0; i < uriContainers.length; ++i) {
            if (uriContainers[i].error) {
                error = true;
                if (!uriContainers[i].validImg) {
                    error_console.appendText("IMAGE HASH ERROR! AT INDEX: " + (i + 1) + "\n");
                    for (String s : uriContainers[i].imgErrors) {
                        error_console.appendText(s + "\n");
                    }
                }
                if (!uriContainers[i].validMeta) {
                    error_console.appendText("META HASH ERROR! AT INDEX: " + (i + 1) + "\n");
                    for (String s : uriContainers[i].metaErrors) {
                        error_console.appendText(s + "\n");
                    }
                }
            }
        }
        if (!error) {
            error_console.appendText("Process Finished With No Meta Or Image Hash Errors!\n");
        } else {
            Util.error(Util.ErrorType.INVALID_HASH, "");
        }
    }

    private void writeRpc(){
        if (mintSettings.outputDir == null) {
            Util.exception(Util.ErrorType.DIR);
        }

        List<JsonContainers.MintRpc> mintRpcs = new ArrayList<>();

        for (int i = 0; i < uriContainers.length; ++i) {
            for (int j = 1; j <= Integer.parseInt(editions_amount.getText()); ++j) {
                var mintRpc = new JsonContainers.MintRpc();

                if (!wallet_id.getText().isBlank()) {
                    mintRpc.wallet_id = Integer.parseInt(wallet_id.getText());
                } else {
                    Util.error(Util.ErrorType.EMPTY, "");
                }

                if (uriContainers[i].imgageHashes.get(0) == null) {
                    Util.error(Util.ErrorType.NO_HASH, "");
                    return;
                } else {
                    mintRpc.hash = uriContainers[i].imgageHashes.get(0);
                }

                if (uriContainers[i].metaHashes.get(0) == null) {
                    Util.error(Util.ErrorType.NO_HASH, "");
                    return;
                }else {
                    mintRpc.meta_hash = uriContainers[i].metaHashes.get(0);
                }

                if ( mintSettings.licenseHash == null) {
                    Util.error(Util.ErrorType.NO_HASH, "");
                    return;
                } else {
                    mintRpc.license_hash = mintSettings.licenseHash;
                }

                if (!royalty_address.getText().isBlank()) {
                    mintRpc.royalty_address = royalty_address.getText();
                } else {
                    Util.error(Util.ErrorType.EMPTY, "");
                }

                if (!target_address.getText().isBlank()) {
                    mintRpc.target_address = target_address.getText();
                } else {
                    Util.error(Util.ErrorType.EMPTY, "");
                }

                mintRpc.uris = mintSettings.getImageUriIndex(i);
                mintRpc.meta_uris = mintSettings.getMetaUriIndex(i);
                mintRpc.license_uris = Arrays.stream(mintSettings.licenseURIs)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                mintRpc.royalty_percentage = (int) (Double.parseDouble(royalty_amount.getText()) * 100);
                mintRpc.edition_number = j;
                mintRpc.edition_count = Integer.parseInt(editions_amount.getText());
                mintRpc.fee = fee_amount.getText().isBlank() ? 0 : Integer.parseInt(fee_amount.getText());
                if (mintRpc.royalty_percentage >= 10000) Util.error(Util.ErrorType.INPUT, fee_amount.getText());

                mintRpcs.add(mintRpc);
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(mintSettings.outputDir + "/nft_rpc.txt", true))) {
            Gson gson = new Gson();
            for (int i = 0; i < mintRpcs.size(); ++i) {
                pw.append(gson.toJson(mintRpcs.get(i)));
                if (i < mintRpcs.size() -1) pw.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Util.exception(Util.ErrorType.FILE);
        }
        error_console.appendText("\nRPC List Saved\n");
    }

}



