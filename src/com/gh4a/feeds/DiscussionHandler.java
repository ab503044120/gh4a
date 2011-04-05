package com.gh4a.feeds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.gh4a.Constants;
import com.gh4a.holder.Feed;

public class DiscussionHandler extends DefaultHandler {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private List<Feed> mFeeds;
    private Feed mFeed;
    private StringBuilder mBuilder;
    private boolean author;
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        mBuilder.append(ch, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        mFeeds = new ArrayList<Feed>();
        mBuilder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        
        if (localName.equalsIgnoreCase("entry")) {
            mFeed = new Feed();
        }
        
        if (mFeed != null) {
            if (localName.equalsIgnoreCase("author")){
                author = true;
            }
            
            if (localName.equalsIgnoreCase("link")){
                String url = attributes.getValue(2);
                String[] urlPart = url.split("/");
                if (urlPart.length > 4) {
                    String id = url.substring(url.indexOf(urlPart[5]));
                    url = url + ".atom?discussion_id=" + id;
                    mFeed.setLink(url);
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (mFeed != null) {
            if (localName.equalsIgnoreCase("title")) {
                String title = mBuilder.toString().trim();
                mFeed.setTitle(title);
            }
            else if (localName.equalsIgnoreCase("content")) {
                mFeed.setContent(mBuilder.toString().trim());
            }
            else if (localName.equalsIgnoreCase("name") && author) {
                mFeed.setAuthor(mBuilder.toString().trim());
                author = false;
            }
            else if (localName.equalsIgnoreCase("published")) {
                try {
                    mFeed.setPublished(sdf.parse(mBuilder.toString().trim()));
                }
                catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                author = false;
            }
            else if (localName.equalsIgnoreCase("entry")) {
                mFeeds.add(mFeed);
            }
        }
        mBuilder.setLength(0);
    }
    
    public List<Feed> getFeeds() {
        return mFeeds;
    }
}