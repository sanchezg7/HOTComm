package com.gerardoslnv.hotcomm;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class HOTfile {

    private static int totalFiles = 0;
    private static String fullLocalPath;
    private File mFile;
    private String lastModified;
    private String remotePath;

    HOTfile(Context mContext, String fileName, String lastModified){
        if(totalFiles == 0){
            fullLocalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            fullLocalPath += mContext.getString(R.string.str_rootDirName);
        }
        ++totalFiles;
        mFile = new File(fileName);
        setLastModified(lastModified);
        remotePath = mContext.getString(R.string.url_syllabus);

    }

    public boolean doesFileExist(){return mFile.exists();}

    public String getFileName() {return mFile.getName();}
    public String getLastModified() {return lastModified;}
    public String getRemotePath(){return remotePath;}
    public static String getFullLocalPath(){return fullLocalPath;}

    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }
    public void setRemotePath(String remotePath){this.remotePath = remotePath;}
    public void setFile(File file){mFile = file;}
}
