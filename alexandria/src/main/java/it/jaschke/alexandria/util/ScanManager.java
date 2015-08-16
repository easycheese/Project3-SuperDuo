package it.jaschke.alexandria.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;

import it.jaschke.alexandria.R;

/**
 * Created by Kyle on 8/16/2015
 */
public class ScanManager {

    public static Intent getTakePictureIntent(Context ctx) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            return takePictureIntent;
        }
        return null; //TODO error handling
    }
    public static ArrayList<String> getBarcodeResultsFromImage(Context ctx, Intent data) {
        Bitmap bitmap = getBitmapFromIntent(data);
        SparseArray<Barcode> barcodes = getBarcodes(ctx, bitmap);
        return getBarcodeText(barcodes);
    }

    private static Bitmap getBitmapFromIntent(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get("data");
    }

    private static SparseArray<Barcode> getBarcodes(Context ctx, Bitmap bitmap) {
        BarcodeDetector detector =
                new BarcodeDetector.Builder(ctx.getApplicationContext())
                        .setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8)
                        .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
         return detector.detect(frame);
    }

    private static ArrayList<String> getBarcodeText(SparseArray<Barcode> barcodes) {
        ArrayList<String> barcodeResults = new ArrayList<>();
        for(int i = 0; i < barcodes.size(); i++) {
            int key = barcodes.keyAt(i);
            Barcode barcode = barcodes.get(key);
            barcodeResults.add(barcode.rawValue);
        }
        return barcodeResults;
    }

    public static ArrayList<String> getTestImage(Context ctx) { // TODO remove
        Bitmap myBitmap = BitmapFactory.decodeResource(
                ctx.getApplicationContext().getResources(),
                R.drawable.isbn_test);
        SparseArray<Barcode> barcodes = getBarcodes(ctx, myBitmap);
        return getBarcodeText(barcodes);
    }
}
