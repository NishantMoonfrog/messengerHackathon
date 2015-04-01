package com.moonfrog.cyf;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;
import com.moonfrog.cyf.AnimatedGifEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Globals {

    private static String key = "nishant__srinath";
    public static String name = "";
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
            if( array[mid].toLowerCase().startsWith(searchText) || array[mid].compareToIgnoreCase(searchText) > 0 ) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        if( array[low].toLowerCase().startsWith(searchText) ) {
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

    public static ArrayList<ListAdapter.ListElement> getFilteredList(String[] array, String searchText) {
        ArrayList<ListAdapter.ListElement> sliderMenu = new ArrayList<>();

        for (int i = findStartIndex(array, searchText), last = findEndIndex(array, searchText) ; i <= last ; i++) {
            sliderMenu.add(new ListAdapter.ListElement(array[i], ""));
        }
        return sliderMenu;
    }
}