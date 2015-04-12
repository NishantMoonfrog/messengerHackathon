package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moonfrog.cyf.utils.GenerateGifFromBitmapsAsyncTask;
import com.moonfrog.cyf.utils.GenerateGifFromBitmapsAsyncTaskParams;
import com.moonfrog.cyf.view.ViewUpdateCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Globals {

    private static String key = "nishant__srinath";
    public static String name = "";
    public static String puzzleDirectory = "puzzles";
    public static String gifPath = "/sdcard/cyfTemp/challenge.gif";

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

    public static ArrayList<String> getFilesFromDir(Context context, String dirFrom) {
        if( dirFrom.length() > 0 && dirFrom.charAt(dirFrom.length() - 1) == '/' ) {
            dirFrom = dirFrom.substring(0, dirFrom.length() - 1);
        }
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        ArrayList<String> puzzles = new ArrayList<>();
        try {
            String fileList[] = am.list(dirFrom);
     
            if (fileList != null) {
                for ( int i = 0;i<fileList.length;i++) {
                    puzzles.add(fileList[i]);
                } 
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return puzzles;
    }

    public static String getDataFromFile(Context context, String filePath) {
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String text = "";
        try {
            InputStream mInput = am.open(filePath);
            int size = mInput.available();
            byte[] buffer = new byte[size];
            mInput.read(buffer);
            mInput.close();

            // byte buffer into a string
            text = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static void challengeFriends(Context context, int[] challenge_layouts, ViewUpdateCall[] viewChanges, Runnable shareChallenge) {
        final String path = "/sdcard/cyfTemp/";
        File folder = new File(path);
        if (!folder.exists()) {
            if( !folder.mkdirs() ) {
                Log.e("Couldn't create folder ", path);
                return;
            }
        }

        final ArrayList< Bitmap > bitmaps = new ArrayList<>();
        for(int i = 0 ; i < challenge_layouts.length ; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = layoutInflater.inflate(challenge_layouts[i], null);
            viewChanges[i].updateView(v);
            if (v.getMeasuredHeight() <= 0) {
                v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            Bitmap image = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(image);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            bitmaps.add(image);
        }
        
        GenerateGifFromBitmapsAsyncTask generateGifFromBitmapsAsyncTask = new GenerateGifFromBitmapsAsyncTask();
        generateGifFromBitmapsAsyncTask.execute(new GenerateGifFromBitmapsAsyncTaskParams(bitmaps, gifPath, shareChallenge));
    }

}