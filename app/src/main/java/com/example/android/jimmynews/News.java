package com.example.android.jimmynews;

import android.graphics.Bitmap;

/**
 * Created by utilizator12 on 31/08/2017.
 */

public class News {
    /**
     * The member variables
     */
    private String mTitle;
    private String mAuthor;
    private String mPublishDate;
    private String mCategory;
    private Bitmap mImage;
    private String mUrl;

    /**
     * The constructor
     * @param title of the article
     * @param author of the article
     * @param publishDate of the article
     * @param category of the article
     * @param image of the article
     * @param url of the article
     */
    public News(String title,
                String author,
                String publishDate,
                String category,
                Bitmap image,
                String url){

        mTitle = title;
        mAuthor = author;
        mPublishDate = publishDate;
        mCategory = category;
        mImage = image;
        mUrl = url;
    }

    /**
     * The getter methods
     */
    public String getTitle() {
        return mTitle;
    }
    public String getAuthor() { return mAuthor; }
    public String getPublishDate(){ return mPublishDate; }
    public String getCategory(){ return mCategory; }
    public Bitmap getImage(){ return mImage; }
    public String getUrl() {return mUrl; }
}
