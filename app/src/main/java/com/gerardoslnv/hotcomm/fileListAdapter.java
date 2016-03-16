package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GerardoSLnv on 3/6/2016.
 */
public class fileListAdapter extends ArrayAdapter {

    private Activity mActivity;
    private ArrayList<String> listOfFiles;

    private static LayoutInflater inflater = null;

    public fileListAdapter(Activity mActivity, ArrayList<String> mList){
        super(mActivity, R.layout.pdf_listview_cell, mList);
        this.mActivity = mActivity;
        this.listOfFiles = mList;

        inflater = (LayoutInflater) this.mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View cellView, ViewGroup parent) {
        fileListCell cell;

        if(cellView == null){ //need to initialize the view
            cellView = inflater.inflate(R.layout.pdf_listview_cell, null);
            cell = new fileListCell();

            //assign the layout ids to the class elements
            cell.fileName = (TextView) cellView.findViewById(R.id.cell_file_name);
            cell.lastModified = (TextView) cellView.findViewById(R.id.cell_modified_date);

            //allow for reuse of cell
            cellView.setTag(cell);
        } else{
            //reuse the existing cell since it already exists and proceed to updating the information
            cell = (fileListCell) cellView.getTag();
        }

        //populate the contents of a single cell
        cell.fileName.setText(listOfFiles.get(position));
        //figure out how to do multiple elements (maybe a class, and having to maintain it somehow)

        return cellView;
    }


    //template for the file cell
    private class fileListCell{
        private TextView fileName;
        private TextView lastModified;
    }
}

