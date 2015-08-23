package it.jaschke.alexandria.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.BookDetailsActivity;
import it.jaschke.alexandria.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;


public class BookDetail extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;

    private String ean;
    private Book book;
////    private String bookTitle;
//    private ShareActionProvider shareActionProvider;
    private View rootView;

    @Bind(R.id.delete_button) Button deleteButton;
//    @Bind(R.id.fullBookTitle) TextView fullBookTitle;
    @Bind(R.id.fullBookSubTitle) TextView fullBookSubTitle;
    @Bind(R.id.fullBookDesc) TextView fullBookDesc;
    @Bind(R.id.authors) TextView authorsTextView;
    @Bind(R.id.categories) TextView categoriesTextView;

    @Bind(R.id.fullBookCover) ImageView bookCoverImage;


    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getActivity().getIntent().getExtras();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        ButterKnife.bind(this, rootView);
        deleteButton.setOnClickListener(this);

        return rootView;
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    public Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+ book.getTitle());
        return shareIntent;
    }
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        book = new Book(data, ean);

//        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
//        fullBookTitle.setText(book.getTitle());

        BookDetailsActivity activity = (BookDetailsActivity) getActivity();
        activity.setTitle(book.getTitle());

//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+ book.getTitle());
//        activity.setShareIntent(shareIntent);

//        shareActionProvider.setShareIntent(shareIntent);

//        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        fullBookSubTitle.setText(book.getSubTitle());

//        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        fullBookDesc.setText(book.getDescription());

//        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
//        String[] authorsArr = authors.split(",");
//        authorsTextView.setLines(authorsArr.length);
//        authorsTextView.setText(authors.replace(",","\n"));
        authorsTextView.setText(book.getAuthorsByRows());
//        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(book.getImageUrl()).matches()){
            Glide.with(this).load(book.getImageUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bookCoverImage);

            bookCoverImage.setVisibility(View.VISIBLE);
        }

//        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        categoriesTextView.setText(book.getCategories());

//        if(rootView.findViewById(R.id.right_container)!=null){
//            rootView.findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
//        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
//        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){ TODO
//            getActivity().getSupportFragmentManager().popBackStack();
//        }
    }

    @Override
    public void onClick(View v) {
        if (v == deleteButton) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean);
            bookIntent.setAction(BookService.DELETE_BOOK);
            getActivity().startService(bookIntent);
//            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().finish();
        }
    }
}