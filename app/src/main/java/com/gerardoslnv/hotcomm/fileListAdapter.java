package com.gerardoslnv.hotcomm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by GerardoSLnv on 3/6/2016.
 */
//A ViewHolder is the equivalent of a row in a list view
public class fileListAdapter extends RecyclerView.Adapter<fileListAdapter.fileViewHolder>{

    private LayoutInflater inflater;
    Context mContext;

    List<HOTfile> data = Collections.emptyList();

    //create a LayoutInflater to take charge of inflating each row
    public fileListAdapter(Context context, List<HOTfile> data){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public fileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //refer to LayoutInflater Tutorial user/slidenerd/videos youtube
        View view = inflater.inflate(R.layout.row_pdf_file, parent, false);
        fileViewHolder mHolder = new fileViewHolder(view); //pass in the row
        return mHolder;
    }

    //Caches the Views that have already been represented here
    @Override
    public void onBindViewHolder(fileViewHolder holder, int position) {
        //retrieve these elements and finally fill them from the appropriate index
        HOTfile mFile = data.get(position);
        holder.fName.setText(mFile.getFileName());
        holder.lastModified.setText(mFile.getLastModified());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class fileViewHolder extends RecyclerView.ViewHolder{ //implements View.OnClickListener{

        TextView fName;
        TextView lastModified;
        //represents the item of the given type (a file entry)
        public fileViewHolder(View itemView) {
            super(itemView);
            //find the XML elements each here (ex: ImageView, TextView, TextView)
            fName = (TextView) itemView.findViewById(R.id.cell_file_name);
            lastModified = (TextView) itemView.findViewById(R.id.cell_modified_date);
//            fName.setOnClickListener(this);
//            lastModified.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            Toast.makeText(mContext, "Clicked file " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
//
//        }
    }
}

