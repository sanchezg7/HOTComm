package com.gerardoslnv.hotcomm;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class HOTfile {

    private static int totalFiles = 0;
    private static String fullLocalPath;
    private File mFile;
    private int version;
    private String remotePath;
    private String type;

    HOTfile(Context mContext, String fileName, int version, String remotePath){
        //only make the file if context is provided
        if(mContext != null) {
            if(totalFiles == 0){
                fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                fullLocalPath += mContext.getString(R.string.str_rootDirName);
            }
            ++totalFiles;
            mFile = new File(fileName);
        }
        setVersion(version);
        //remotePath = mContext.getString(R.string.url_syllabus);
        this.remotePath = remotePath;
    }

    public boolean doesFileExist(){return mFile.exists();}

    public String getFileName() {return mFile.getName();}
    public int getVersion() {return version;}
    public String getRemotePath(){return remotePath;}
    public String getType(){return type;}
    public static String getFullLocalPath(){return fullLocalPath;}


    public void setVersion(int version){
        this.version = version;
    }
    public void setRemotePath(String remotePath){this.remotePath = remotePath;}
    public void setFile(File file){mFile = file;}
    public void setType(String type){ this.type = type;}
}
