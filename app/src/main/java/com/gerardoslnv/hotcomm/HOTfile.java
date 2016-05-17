package com.gerardoslnv.hotcomm;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class HOTfile {

    private static int totalFiles = 0;
    private static String fullLocalPath;
    private File mFile;
    private int version;
    private int mId;
    private String remotePath;
    private String type;

    HOTfile(Context mContext, String fileName, int version, int mId, String remotePath, String type){
        //only make the file if context is provided
        if(mContext != null) {
            addContext(mContext);
        }
        setVersion(version);
        setRemotePath(remotePath);
        setType(type);
        setmId(mId);
        mFile = new File(fileName);
    }

    public void addContext(Context mContext)
    {
        if(totalFiles == 0){
            fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            fullLocalPath += mContext.getString(R.string.str_rootDirName);
        }
        ++totalFiles;
    }

    public boolean doesFileExist(){return mFile.exists();}

    public String getFileName() {return mFile.getName();}
    public File getmFile(){ return mFile; }
    public int getVersion() {return version;}
    public String getRemotePath(){return remotePath;}
    public String getType(){return type;}
    public static String getFullLocalPath(){return fullLocalPath;}
    public static String getFullLocalPath(Context mContext)
    {
        fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        fullLocalPath += mContext.getString(R.string.str_rootDirName);

        return fullLocalPath;
    }


    public void setVersion(int version){
        this.version = version;
    }
    public void setRemotePath(String remotePath){this.remotePath = remotePath;}
    public void setFile(File file){mFile = file;}
    public void setType(String type){ this.type = type;}
    public int getmId() {return mId;}
    public void setmId(int mId) {this.mId = mId;}
}
