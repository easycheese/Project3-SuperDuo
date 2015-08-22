package it.jaschke.alexandria.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.BarcodeException;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.NetworkManager;
import it.jaschke.alexandria.util.ScanManager;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, View.OnClickListener {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private final int LOADER_ID = 156;
    private final String EAN_CONTENT="eanContent";

    private static final int REQUEST_IMAGE_CAPTURE = 153;

    private File tempFile;

    @Bind(R.id.ean) EditText ean;
    @Bind(R.id.scan_button) ImageButton scanButton;
    @Bind(R.id.progressBar) ProgressBar progressBar;


//    @Bind(R.id.save_button) Button saveButton;
//    @Bind(R.id.delete_button) Button deleteButton;

//    @Bind(R.id.bookTitle) TextView bookTitleTextView;
//    @Bind(R.id.bookSubTitle) TextView bookSubTitleTextView;
//    @Bind(R.id.authors) TextView authorsTextView;
//    @Bind(R.id.categories) TextView categoriesTextView;

//    @Bind(R.id.bookCover) ImageView bookCoverImageView;


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

        Book book = new Book(data, ean.getText().toString());
        AlertDialog dialog = getBookFragment(book);

        dialog.show();
// Made the below a dialog fragment
//        bookTitleTextView.setText(bookTitle);
//

//        bookSubTitleTextView.setText(bookSubTitle);
//
//        String[] authorsArr = authors.split(",");
//        authorsTextView.setLines(authorsArr.length);
//        authorsTextView.setText(authors.replace(",", "\n"));
//
//        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
//            Glide.with(this).load(imgUrl)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(bookCoverImageView);
//
//            bookCoverImageView.setVisibility(View.VISIBLE);
//        }
//
//
//        categoriesTextView.setText(categories);
//
//        saveButton.setVisibility(View.VISIBLE);
//        deleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

//    private void clearFields(){
//        bookSubTitleTextView.setText("");
//        bookTitleTextView.setText("");
//        authorsTextView.setText("");
//        categoriesTextView.setText("");
//        bookCoverImageView.setVisibility(View.INVISIBLE);
//        saveButton.setVisibility(View.INVISIBLE);
//        deleteButton.setVisibility(View.INVISIBLE);
//    }

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
//        if(ean.length()<13){ //Removed to prevent field clearing unnecessarily
//            clearFields();
//            return;
//        }

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
//            clearFields();
            try {
                tempFile = ScanManager.createImageFile(getActivity());
                Intent i = ScanManager.getTakePictureIntent(getActivity(), tempFile);
                startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
            } catch (IOException e) {
                e.printStackTrace(); //TODO handle error, test on fresh device?
                Toast.makeText(getActivity(), "No lib found", Toast.LENGTH_SHORT).show();
            }

//        } else if (v == saveButton) {
//            ean.setText("");
//        } else if (v == deleteButton) {
//            Intent bookIntent = new Intent(getActivity(), BookService.class);
//            bookIntent.putExtra(BookService.EAN, ean.getText().toString());
//            bookIntent.setAction(BookService.DELETE_BOOK);
//            getActivity().startService(bookIntent);
//            ean.setText("");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            new DecodePictureTask().execute();
        }
    }
    private class DecodePictureTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            Bitmap bitmap = ScanManager.getBitmap(tempFile);
            ArrayList<String> barcodes = null;
            try {
                barcodes = ScanManager.getBarcodeData(getActivity(), bitmap);
            } catch (BarcodeException e) {
                e.printStackTrace(); //TODO thrown if play services not up to date w/link to Play Store
            }
            if (!barcodes.isEmpty()) {
                return barcodes.get(0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            tempFile.delete();

            if (result != null) {
                ean.setText(result);
            } else {
                Toast.makeText(getActivity(), getString(R.string.isbn_empty), Toast.LENGTH_SHORT).show();
                //TODO toast error
            }
        }
    }

    public AlertDialog getBookFragment(final Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.book_info_layout, null);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.save_button, null)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent bookIntent = new Intent(getActivity(), BookService.class);
                        bookIntent.putExtra(BookService.EAN, book.getIsbn());
                        bookIntent.setAction(BookService.DELETE_BOOK);
                        getActivity().startService(bookIntent);
                    }
                });

        ((TextView)dialogView.findViewById(R.id.add_book_authors)).setText(getString(R.string.book_by) + "\n" + book.getAuthorsByRows());
        ((TextView)dialogView.findViewById(R.id.add_book_category)).setText(getString(R.string.book_category) + book.getCategories());
        ((TextView)dialogView.findViewById(R.id.add_book_isbn)).setText(getString(R.string.book_isbn) +book.getIsbn());

        ImageView album = ((ImageView)dialogView.findViewById(R.id.add_book_image));

        Glide.with(getActivity()).load(book.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(album);

        builder.setTitle(book.getTitle());
        builder.setCancelable(false);
        return builder.create();
    }
}
