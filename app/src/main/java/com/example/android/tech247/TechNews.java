package com.example.android.tech247;

public class TechNews {
    // Image Thumbnail for the news
    private String mThumbnail;

    // Headline of the News
    private String mHeadline;

    // Author of the News
    private String mAuthor;

    // Section Name of the News
    private String mSection;

    // date of the News (Publish or Last modified, depending on User Preference)
    private String mPublishedDate;

    // url link to the guardian website containing content of the News
    private String mUrl;


    /**
     * Constructs a new {@link TechNews} object.
     *  @param Thumbnail is the image Thumbnail for the news
     * @param Headline of the news
     * @param Author of the news
     * @param Section the news belong to
     * @param  PublishedDate is the date it was published
     * @param url is the link to the guradian website containing more details about the news
     *
     */

    public TechNews(String Thumbnail, String Headline, String Author, String Section, String PublishedDate, String url) {
        mThumbnail = Thumbnail;
        mHeadline = Headline;
        mAuthor = Author;
        mSection = Section;
        mPublishedDate = PublishedDate;
        mUrl = url;
    }

    // Returns the image Thumbnail for the news
    public String getImageResource(){return mThumbnail;}

    // Returns the headline of the news
    public String getmHeadline(){return mHeadline;}

    // Returns the author of the news
    public String getmAuthor(){return mAuthor;}

    // Returns the section the news belong to
    public String getmSection(){return mSection;}

    // Returns the date the news was published
    public String getmPublishedDate(){return mPublishedDate;}

    // Returns the url link to the guardian website containing more details about the news
    public String getUrl(){return mUrl;}
}
