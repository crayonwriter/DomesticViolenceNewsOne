package com.example.android.domesticviolencenewsone;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.net.URL;

// Create a new {@link DVArticles} object with the sectionName, webUrl, headline, byline, firstPublicationDate, and body
// from the JSON response.

public class DVArticles {

    /**
     * @link DVArticles is a class that is used for each article found, and includes
     * the following attributes of each article:
     */

    //Instantiate the states
    private String mSection;
    private String mSectionId;
    private String mUrl;
    private String mHeadline;
    private String mByline;
    private String mDate;
    private String mBody;
    private String mPageSize;
    private int mWordcount;

    /**
     * Constructor for the class
     *
     * @param section   is the category name of article in the Guardian site
     * @param sectionId is the category id of the article in the Guardian site
     * @param url       is the URL of the article on the Web
     * @param headline  is the headline of the article
     * @param byline    is the article author's name
     * @param date      is the first publication date of the article
     * @param body      is the bodytext of the article
     * @param pageSize  is the number of pages to show onscreen
     * @param wordcount is the number of words in the article
     */


    public DVArticles(String section, String sectionId, String url, String headline, String byline, String date, String body, String pageSize, int wordcount) {
        mSection = section;
        mSectionId = sectionId;
        mUrl = url;
        mHeadline = headline;
        mByline = byline;
        mDate = date;
        mBody = body;
        mPageSize = pageSize;
        mWordcount = wordcount;
    }

    //Declare the methods to get each state
    public String getSection() {
        return mSection;
    }

    public String getSectionId() {
        return mSectionId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getByline() {
        return mByline;
    }

    public String getDate() {
        return mDate;
    }

    public String getBody() {
        return mBody;
    }

    public String getWordcount() {
        return "# of words: " + mWordcount;
    }

    public String getPageSize() {
        return mPageSize;
    }
}

