package it.jaschke.alexandria.model;

import android.database.Cursor;

import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by Kyle on 8/17/2015.
 */
public class Book {
    private String title;
    private String subTitle;
    private String authors;
    private String imageUrl;
    private String categories;
    private String isbn;
    private String description;

    public Book(Cursor data, String isbn) {
        categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        imageUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        subTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        title = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        description = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategories() {
        return categories;
    }

    public String getAuthorsByRows() {

        return authors.replace(",", "\n");
    }

    public String getIsbn() {
        return isbn;
    }

    public String getDescription() {
        return description;
    }
}
