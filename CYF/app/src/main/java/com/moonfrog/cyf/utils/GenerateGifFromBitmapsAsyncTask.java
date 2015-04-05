package com.moonfrog.cyf.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.moonfrog.cyf.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by srinath on 06/04/15.
 */
public class GenerateGifFromBitmapsAsyncTask extends AsyncTask<GenerateGifFromBitmapsAsyncTaskParams, Void, Void> {
    protected Void doInBackground(GenerateGifFromBitmapsAsyncTaskParams... params) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(1000);
        encoder.start(bos);
        for (Bitmap bitmap : params[0].bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        Log.e("GGFBAT", "Generated");

        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(params[0].gifPath);
            outStream.write(bos.toByteArray());
            outStream.close();
            Log.e("GGFBAT ", "Saved");
        } catch(Exception e){
            e.printStackTrace();
        }
        params[0].callback.run();
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        return;
    }

    protected void onPostExecute(Long result) {
        Log.e("GGFBAT", "Completed");
    }
}
