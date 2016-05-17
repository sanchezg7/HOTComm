package com.gerardoslnv.hotcomm;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GerardoGPC on 5/8/2016.
 */
public class XmlHandler {

    private boolean parseComplete;
    private String mStartTag_PDF;
    private String mStartTag_Resources;
    private String mStartTag_File;
    private String mStartTag_URL;
    private String mStartTag_Filename;
    private static String mNameSpace;
    private List<HOTfile> parsedHotFiles;

    //fields for HOT File
    String mURL = null;
    String mFileName = null;
    String mType; //category of file
    int mVersion;
    int mId;


    XmlHandler() {
        parsedHotFiles = new ArrayList<>();

    }

    public void reset(){
        setParseComplete(false);
        mStartTag_PDF = "pdf";
        mStartTag_Resources = "resources";
        mStartTag_File = "file";
        mStartTag_URL = "url";
        mStartTag_Filename = "filename";
        mNameSpace = null;
        parsedHotFiles.clear();
    }


    public List<HOTfile> parsePdf(InputStream in) throws XmlPullParserException, IOException
    {
        reset();
        XmlPullParser mParser;

        //this part instantiates and kick starts the parsing in the readFeed section
        try{
            mParser = Xml.newPullParser();
            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false); //disable namespace
            mParser.setInput(in, null);
            mParser.nextTag();
            processPdfs(mParser);
        }finally {
            in.close();
        }
        setParseComplete(true);
        return parsedHotFiles;

    }

    //If it encounters the pdf tag, hand it off to the reading a file method to add to the arraylist
    //It will skip the tag if we are not interested in it
    private void processPdfs(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_Resources); //setting the start_tag
        while(parser.next() != XmlPullParser.END_TAG)
        {
            if(parser.getEventType() != XmlPullParser.START_TAG)
            {
                //"re-evaluate" until there is a START_TAG found
                continue;
            }
            String name  = parser.getName();
            if(name.equals("pdf")) //1st level depth
            {
                //scan the files
                processHotFiles(parser);
            }else {
                skip(parser); //not interested in the other tags
            }
        }
    }


    private void processHotFiles(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_PDF); //enforce the "pdf" tag
        while(parser.next() != XmlPullParser.END_TAG)
        {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue; //this skips over the start tag found
            }

            //reset fields for HOT File
            mURL = null;
            mFileName = null;
            mType = null; //category of file
            mVersion = -1;

            String tag = parser.getName(); //format <file type="band_handbook" version="1">
            if(tag.equals("file")) //2nd level depth
            {
                mType = parser.getAttributeValue(mNameSpace, "type"); /* TODO: consolidate the attribute names */
                mVersion = Integer.parseInt(parser.getAttributeValue(mNameSpace, "version"));
                mId = Integer.parseInt(parser.getAttributeValue(mNameSpace, "id"));

                processFileAttbs(parser);

                //Place holder HotFile with retrieved data
                HOTfile tempHOTFile = new HOTfile(null, mFileName, mVersion, mId, mURL, mType);
                parsedHotFiles.add(tempHOTFile);
            }
        }
    }

    private void processFileAttbs(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_File); //enforce currently at the "file" tag
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue; //this skips over the start tag found
            }

            String tag = parser.getName();
            if (tag.equals(mStartTag_URL)) {
                mURL = getURL(parser);
            } else if (tag.equals(mStartTag_Filename))
            {
                mFileName = getFileName(parser);
            }
        }

    }

    private String getURL(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_URL); //TODO: consolidate
        String URL = parser.getAttributeValue(mNameSpace, "addr");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, mNameSpace, mStartTag_URL);
        return  URL;
    }

    private String getFileName(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_Filename);
        String fileName = parser.getAttributeValue(mNameSpace, "name");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, mNameSpace, mStartTag_Filename);
        return fileName;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if(parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException("Skip Function Failed");
        }
        int depth = 1;
        while(depth != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    --depth;
                    break;
                case XmlPullParser.START_TAG:
                    ++depth;
                    break;
            }
        }
    }

    public void setStartTag(String startTag){ mStartTag_PDF = startTag;}
    public void setmNameSpace(String nameSpace){mNameSpace = nameSpace;}
    public boolean isParseComplete(){return parseComplete;}
    private void setParseComplete(boolean flag){ this.parseComplete = flag; }
}
