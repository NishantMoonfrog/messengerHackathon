package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moonfrog.cyf.view.ViewUpdateCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Globals {

    private static String key = "nishant__srinath";
    public static String name = "";

    public static JSONObject word_list;

    public static String encrypt(String text) {
      try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            String encrypted = Base64.encodeToString(cipher.doFinal(text.getBytes()), Base64.DEFAULT);
            return encrypted;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String hash) {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decrypted = cipher.doFinal(Base64.decode(hash.getBytes(), Base64.DEFAULT));
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 30, stream);
            byte[] byteArray = stream.toByteArray();
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateGIF(ArrayList<Bitmap> bitmaps) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(1000);
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        Log.e("gif", "generated");
        return bos.toByteArray();
    }

    public static int findStartIndex(String[] array, String searchText) {
        int low = 0, high = array.length - 1, mid;
        if( high < 0 ) {
            return 0;
        }

        while(low < high) {
            mid = (low + high) / 2;
            if( array[mid].compareToIgnoreCase(searchText) < 0 ) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        if( array[low].toLowerCase().startsWith(searchText) ) {
            return low;
        }
        return low + 1;
    }

    public static int findStartIndex(JSONArray array, String searchText) throws JSONException {
        int low = 0, high = array.length() - 1, mid;
        if( high < 0 ) {
            return 0;
        }

        while(low < high) {
            mid = (low + high) / 2;
            if( array.getString(mid).compareToIgnoreCase(searchText) < 0 ) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        if( array.getString(low).toLowerCase().startsWith(searchText) ) {
            return low;
        }
        return low + 1;
    }

    public static int findEndIndex(String[] array, String searchText) {
        int low = 0, high = array.length - 1, mid;
        if( high < 0 ) {
            return -1;
        }

        while(low < high) {
            mid = (low + high + 1) / 2;
            if( array[mid].toLowerCase().startsWith(searchText) || array[mid].compareToIgnoreCase(searchText) < 0 ) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        if( array[low].toLowerCase().startsWith(searchText) ) {
            return low;
        }
        return -1;
    }

    public static int findEndIndex(JSONArray array, String searchText) throws JSONException {
        int low = 0, high = array.length() - 1, mid;
        if( high < 0 ) {
            return -1;
        }

        while(low < high) {
            mid = (low + high + 1) / 2;
            if( array.getString(mid).toLowerCase().startsWith(searchText) || array.getString(mid).compareToIgnoreCase(searchText) < 0 ) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        if( array.getString(low).toLowerCase().startsWith(searchText) ) {
            return low;
        }
        return -1;
    }

    public static ArrayList<String> getFilteredList(String[] array, String searchText) {
        ArrayList<String> sliderMenu = new ArrayList<>();

        for (int i = findStartIndex(array, searchText), last = findEndIndex(array, searchText) ; i <= last ; i++) {
            sliderMenu.add(array[i]);
        }
        return sliderMenu;
    }

    public static ArrayList<String> getFilteredList(JSONArray array, String searchText) throws JSONException {
        ArrayList<String> sliderMenu = new ArrayList<>();

        for (int i = findStartIndex(array, searchText), last = findEndIndex(array, searchText) ; i <= last ; i++) {
            sliderMenu.add(array.getString(i));
        }
        return sliderMenu;
    }

    public static void makeGIF(Activity owner, int[] ids, ViewUpdateCall[] viewChanges, final String gifPath, final Runnable callback) {
        final ArrayList< Bitmap > bitmaps = new ArrayList<>();

        for(int i = 0 ; i < ids.length ; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) owner.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View v = layoutInflater.inflate(ids[i], null);
            viewChanges[i].updateView(v);

            final ViewGroup current = (ViewGroup) owner.getWindow().getDecorView().getRootView();
            current.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

            v.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = Globals.getBitmapFromView(v);
                    current.removeView(v);

                    synchronized (bitmaps) {
                        bitmaps.add(image);
                        if( bitmaps.size() < 3 ) {
                            return;
                        }
                        current.invalidate();
                        Log.e("facebook ", "Invalidated");

                        class MyTaskParams {
                            ArrayList<Bitmap> bitmaps;
                            String gifPath;
                            Runnable callback;

                            MyTaskParams(ArrayList<Bitmap> bitmaps, String path, Runnable callback) {
                                this.bitmaps = bitmaps;
                                this.gifPath = path;
                                this.callback = callback;
                            }
                        }

                        class BuildGIF extends AsyncTask<MyTaskParams, Void, Void> {
                            @Override
                            protected Void doInBackground(MyTaskParams... params) {
                                ArrayList<Bitmap> bitmaps = params[0].bitmaps;
                                String gifPath = params[0].gifPath;
                                FileOutputStream outStream = null;
                                try {
                                    outStream = new FileOutputStream(gifPath);
                                    outStream.write(Globals.generateGIF(bitmaps));
                                    outStream.close();
                                    Log.e("challenge ", "saved");
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                                params[0].callback.run();
                                return null;
                            }
                        }

                        MyTaskParams params = new MyTaskParams(bitmaps, gifPath, callback);
                        BuildGIF builder = new BuildGIF();
                        builder.execute(params);

                        // String path = MediaStore.Images.Media.insertImage(static_instance.getContentResolver(), image, "Title", null);
                    }
                }
            });
        }
    }
}