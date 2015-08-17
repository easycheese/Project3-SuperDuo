package it.jaschke.alexandria.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.NetworkManager;
import it.jaschke.alexandria.util.ScanManager;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, View.OnClickListener {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private final int LOADER_ID = 1;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private static final int REQUEST_IMAGE_CAPTURE = 153;
    private String mScanFormat = "Format:";

    private String mScanContents = "Contents:";

    private File tempFile;

    @Bind(R.id.ean) EditText ean;
    @Bind(R.id.scan_button) Button scanButton;
    @Bind(R.id.save_button) Button saveButton;
    @Bind(R.id.delete_button) Button deleteButton;

    @Bind(R.id.bookTitle) TextView bookTitleTextView;
    @Bind(R.id.bookSubTitle) TextView bookSubTitleTextView;
    @Bind(R.id.authors) TextView authorsTextView;
    @Bind(R.id.categories) TextView categoriesTextView;

    @Bind(R.id.bookCover) ImageView bookCoverImageView;

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);

        ean.addTextChangedListener(this);
        scanButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        bookTitleTextView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        bookSubTitleTextView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        authorsTextView.setLines(authorsArr.length);
        authorsTextView.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            Glide.with(this).load(imgUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bookCoverImageView);

            bookCoverImageView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        categoriesTextView.setText(categories);

        saveButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        bookSubTitleTextView.setText("");
        bookTitleTextView.setText("");
        authorsTextView.setText("");
        categoriesTextView.setText("");
        bookCoverImageView.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String ean =s.toString();
        //catch isbn10 numbers
        if(ean.length()==10 && !ean.startsWith("978")){
            ean="978"+ean;
        }
        if(ean.length()<13){
            clearFields();
            return;
        }

        if (NetworkManager.hasNetworkConnection(getActivity())) {
            //Once we have an ISBN, start a book intent
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean);
            bookIntent.setAction(BookService.FETCH_BOOK);
            getActivity().startService(bookIntent);
            AddBook.this.restartLoader();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_network_connection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {

        if (v == scanButton) {
            // TODO
            // This is the callback method that the system will invoke when your button is
            // clicked. You might do this by launching another app or by including the
            //functionality directly in this app.
            // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
            // are using an external app.
            //when you're done, remove the toast below.
//            Context context = getActivity();
//            CharSequence text = "This button should let you scan a book for its barcode!";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();



            try {
                tempFile = ScanManager.createImageFile(getActivity());
                Intent i = ScanManager.getTakePictureIntent(getActivity(), tempFile);
                startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
            } catch (IOException e) {
                e.printStackTrace(); //TODO handle error
            }


//            ArrayList<String> test = ScanManager.getTestImage(getActivity());
//            int x=0;
//            x++;

        } else if (v == saveButton) {
            ean.setText("");
        } else if (v == deleteButton) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean.getText().toString());
            bookIntent.setAction(BookService.DELETE_BOOK);
            getActivity().startService(bookIntent);
            ean.setText("");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int f=4;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            ArrayList<String> barcodes = ScanManager.getBarcodeResultsFromImage(getActivity(), data);// TODO async this

            Bitmap bitmap = ScanManager.getBitmap(tempFile);
            ArrayList<String> barcodes = ScanManager.getBarcodeResultsFromImage(getActivity(), bitmap);
            int x=0;
            x++;
        }
    }
}
