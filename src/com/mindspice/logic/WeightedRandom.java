package com.mindspice.logic;

import com.mindspice.imagefile.ImageFile;
import com.mindspice.layer.Layer;
import javafx.collections.ObservableList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class WeightedRandom {


    public static ImageFile getWeightedRandom(ObservableList<ImageFile> imageList) {
        double totalWeight = 0.0;

        for (ImageFile i : imageList) {
            totalWeight += i.getWeight();
        }

        int i = 0;

        for (double r = Math.random() * totalWeight; i < imageList.size() - 1; ++i) {
            r -= imageList.get(i).getWeight();
            if (r <= 0.0) break;
        }
        return imageList.get(i);
    }
}
