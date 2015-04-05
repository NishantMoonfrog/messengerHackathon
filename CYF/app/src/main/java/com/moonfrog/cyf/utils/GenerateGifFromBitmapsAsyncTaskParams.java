package com.moonfrog.cyf.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by srinath on 06/04/15.
 */
public class GenerateGifFromBitmapsAsyncTaskParams {
    ArrayList<Bitmap> bitmaps;
    String gifPath;
    Runnable callback;

    public GenerateGifFromBitmapsAsyncTaskParams(ArrayList<Bitmap> bitmaps, String path, Runnable callback) {
        this.bitmaps = bitmaps;
        this.gifPath = path;
        this.callback = callback;
    }
}
