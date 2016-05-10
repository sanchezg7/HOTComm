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
    private static String mNameSpace = null;


    XmlHandler()
    {
        mStartTag_PDF = "pdf";
        mStartTag_Resources = "resources";
        parseComplete = false;
    }


    public List<HOTfile> parsePdf(InputStream in) throws XmlPullParserException, IOException
    {
        XmlPullParser mParser;
        //this part instantiates and kick starts the parsing in the readFeed section
        try{
            mParser = Xml.newPullParser();
            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false); //disable namespace
            mParser.setInput(in, null);
            mParser.nextTag();
            return processPdfs(mParser);

        }finally {
            in.close();
        }
    }

    //If it encounters the pdf tag, hand it off to the reading a file method to add to the arraylist
    //It will skip the tag if we are not interested in it
    private List<HOTfile> processPdfs(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List availFiles = new ArrayList<HOTfile>();
        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_Resources); //setting the start_tag
        while(parser.next() != XmlPullParser.END_TAG)
        {
            if(parser.getEventType() != XmlPullParser.START_TAG)
            {
                //"re-evaluate" until there is a START_TAG found
                continue;
            }
            String name  = parser.getName();
            if(name.equals("pdf"))
            {
                //scan the files
                availFiles = processHotFiles(parser);
            }else {
                skip(parser); //not interested in the other tags
            }
        }
        return null;

    }


    private List<HOTfile> processHotFiles(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List <HOTfile> allDocs = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, mNameSpace, mStartTag_PDF); //jump to "file" tag
        while(parser.next() != XmlPullParser.END_TAG)
        {
            //field for HOT File
            String URL = null;
            String fileName = null;
            String type;
            int version;

            if(parser.getEventType() != XmlPullParser.START_TAG)
                continue; //continue until we find the desired start tag

            String tag = parser.getName();
            if(tag.equals("file"))
            {
                type = parser.getAttributeValue(mNameSpace, "type"); /* TODO: consolidate the attribute names */
                version = Integer.parseInt(parser.getAttributeValue(mNameSpace, "version"));

                URL = getURL(parser);
                fileName = getFileName(parser);
            }
        }

        //make a new instance of a HOTFile
        //populate it with the name, and version number
        //add it to the arraylist
        return allDocs;
    }

    private String getURL(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, "url"); //TODO: consolidate
        String URL = parser.getAttributeValue(mNameSpace, "addr");
        return  URL;
    }

    private String getFileName(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, mNameSpace, "filename");
        String fileName = parser.getAttributeValue(mNameSpace, "name");
        parser.require(XmlPullParser.START_TAG, mNameSpace, "filename");
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
}
