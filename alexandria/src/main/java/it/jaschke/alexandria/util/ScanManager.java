package it.jaschke.alexandria.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.BarcodeException;


/**
 * Created by Kyle on 8/16/2015
 */
public class ScanManager {

    public static Intent getTakePictureIntent(Context ctx, File photoFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            return takePictureIntent;

        }

        return null; //TODO error handling
    }

    public static ArrayList<String> getBarcodeData(Context ctx, Bitmap bitmap) throws BarcodeException {
        SparseArray<Barcode> barcodes = getBarcodesFromImage(ctx, bitmap);
        return getBarcodeText(barcodes);
    }

    private static SparseArray<Barcode> getBarcodesFromImage(Context ctx, Bitmap bitmap) throws BarcodeException {

        BarcodeDetector detector =
                new BarcodeDetector.Builder(ctx.getApplicationContext())
                        .setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8)
                        .build();

        if (!detector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            throw new BarcodeException(ctx.getString(R.string.error_detector));  //TODO add error data to pull out in async
        }


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


    public static File createImageFile(Context ctx) throws IOException {
        File storageDir = ctx.getExternalCacheDir();
        String imageFileName = "TEMP";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
    public static Bitmap getBitmap(File photoFile) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
    }

}
