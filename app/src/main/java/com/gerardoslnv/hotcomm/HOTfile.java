package com.gerardoslnv.hotcomm;

/**
 * Created by GerardoGPC on 3/16/2016.
 */
public class HOTfile {

    private String fileName;
    private String lastModified;

    public String getFileName() {return fileName;}
    public String getLastModified() {return lastModified;}

    HOTfile(String fileName, String lastModified){
        setFileName(fileName);
        setLastModified(lastModified);
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public void setLastModified(String lastModified){
        this.lastModified = lastModified;
    }
}
