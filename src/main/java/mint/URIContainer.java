package mint;

import utility.HashFunc;
import utility.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URIContainer {
    public List<String> imgURIs = new ArrayList<>();
    public List<String> metaURIs = new ArrayList<>();
    public byte[] imgFile;
    public byte[] metaFile;
    public List<String> imgErrors = new ArrayList<>();
    public List<String> metaErrors = new ArrayList<>();
    List<String> imgageHashes = new ArrayList<>();
    List<String> metaHashes = new ArrayList<>();

    public boolean error;
    public boolean validImg;
    public boolean validMeta;

    public void validateHashes() {
        for (var i : imgURIs) {
            System.out.println(i.toString());
        }
        System.out.println();
        validateImg();
        validateMeta();
        System.out.println(error);

        if (error) {
            for(int i = 0; i < 3; ++i) {
                if (!reValidate()) break;
            }
        }
    }

    public boolean reValidate() {
        error = false;
        if (!validImg) {
            imgErrors.clear();
            validateImg();
        }
        if (!validMeta){
            metaErrors.clear();
            validateMeta();
        }
        return error;
    }

    private void validateImg() {
        if (imgURIs.isEmpty()) {
            validImg = true;
            return;
        }

        try {
            if (imgFile != null) {
                imgageHashes.add(HashFunc.getHash(imgFile));
            }
            for (String uri : imgURIs) {
                imgageHashes.add(HashFunc.getHash(HashFunc.fetchBytes(new URL(uri))));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Util.error(Util.ErrorType.URL, "s");
        } catch (Exception e) {
            e.printStackTrace();
            Util.exception(Util.ErrorType.UNKNOWN);
        }

        String imgHash = imgageHashes.get(0);
        for (int i = 1; i < imgageHashes.size(); ++i) {
            if (!imgHash.equals(imgageHashes.get(i))) {
                imgErrors.add(imgFile == null ? imgURIs.get(i) : imgURIs.get(i-1));
                error = true;
                validImg = false;
            }
        }
        if (error) return;
        validImg = true;
    }

    private void validateMeta() {
        if (metaURIs.isEmpty()) {
            validMeta = true;
            return;
        }

        try {
            if (metaFile != null) {
                metaHashes.add(HashFunc.getHash(metaFile));
            }
            for (String uri : metaURIs) {
                metaHashes.add(HashFunc.getHash(HashFunc.fetchBytes(new URL(uri))));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Util.error(Util.ErrorType.URL, "s");
        } catch (Exception e) {
            Util.exception(Util.ErrorType.UNKNOWN);
        }

        String metaHash = metaHashes.get(0);
        for (int i = 1; i < metaHashes.size(); ++i) {
            if (!metaHash.equals(metaHashes.get(i))) {
                metaErrors.add(metaFile == null ? metaURIs.get(i) : metaURIs.get(i-1));
                error = true;
                validMeta = false;
            }
            if (error) return;
            validMeta = true;
        }
    }
}
