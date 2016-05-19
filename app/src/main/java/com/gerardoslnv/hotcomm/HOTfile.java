package com.gerardoslnv.hotcomm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class HOTfile {

    private static int totalFiles = 0;
    private static String fullLocalPath;
    private int version;
    private int mId;
    private String remotePath;
    private String type;
    private String mFileName;


    /*Static Methods */
    public static String getFullLocalPath(){return fullLocalPath;}
    public static String getFullLocalPath(Context mContext)
    {
        fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        fullLocalPath += mContext.getString(R.string.str_rootDirName);

        return fullLocalPath;
    }

    /*Constructor*/
    HOTfile(Context mContext, String fileName, int version, int mId, String remotePath, String type){
        //only make the file if context is provided
        if(mContext != null) {
            addContext(mContext);
        }
        setVersion(version);
        setRemotePath(remotePath);
        setType(type);
        setmId(mId);
        setFileName(fileName);
    }

    public static File createFileHandle(HOTfile mHOTFile)
    {
        File mFile;

        mFile = new File(getFullLocalPath(), mHOTFile.getmFileName());
        if(!mFile.exists())
        {
            Log.e("createFileHandle", mFile + "does not exist");
        }
        return mFile;
    }

    public void addContext(Context mContext)
    {
        if(totalFiles == 0){
            fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            fullLocalPath += mContext.getString(R.string.str_rootDirName);
        }
        ++totalFiles;
    }

    /*Getters*/
    public String getmFileName() {return mFileName;}
    public int getVersion() {return version;}
    public String getRemotePath(){return remotePath;}
    public String getType(){return type;}
    public int getmId() {return mId;}

    /*Setters*/
    public void setVersion(int version){this.version = version;}
    public void setRemotePath(String remotePath){this.remotePath = remotePath;}
    public void setType(String type){ this.type = type;}
    public void setmId(int mId) {this.mId = mId;}
    public void setFileName(String fileName){this.mFileName = fileName;}
}
