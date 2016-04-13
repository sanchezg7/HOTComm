package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFsFragment extends Fragment implements View.OnClickListener {

    Button btnHandbookSyllabus;
    Button btnDrumlineHandbook;
    Button btnCopy;
    TextView storagePathTextView;
    private fileListAdapter mFileListAdapter;

    //RecyclerView 101
    /*
    Need LayoutManager to make it work, no more gridviews
    Need adapter to decide how to put the elements
    To display a row, create a XML file and inflate it in code (expensive linear operation)
        Finding items for each row is also expensive
        RecycleView helps avoid expensive costs
     1: Inflate the layout (onCreateViewHolder)
     2: use the ViewHolder to populate your current row inside the BindViewHolder
     */



    private RecyclerView pdfsRecyclerView; //extends from ViewGroup (layout class), far more flexible


    private String handBookfileName = null;
    private String dl_handBookFileName = null;
    private static String filePath = null;
    static Activity  myActivity;


    public PDFsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //set Context
        myActivity = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdfs, container, false);

        //assign the recycler view
        pdfsRecyclerView = (RecyclerView) view.findViewById(R.id.list_pdf);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String thisPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //storagePathTextView.setText(thisPath);
        handBookfileName = "hot_handbook2015.pdf";
        dl_handBookFileName = "dl_handbook15.pdf";
        filePath = thisPath + "/HOT_PDF/";

        ArrayList<String> myList = new ArrayList<String> ();
        //Temporary HARD adding elements
        myList.add(handBookfileName);
        myList.add(dl_handBookFileName);

        mFileListAdapter = new fileListAdapter(getActivity(), buildData(myList));
        pdfsRecyclerView.setAdapter(mFileListAdapter);
        //pdfsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //telling it to present in a LINEAR format
        //spanCount	int: If orientation is vertical, spanCount is number of columns. If orientation is horizontal, spanCount is number of rows.
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(myActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pdfsRecyclerView.setLayoutManager(mLinearLayoutManager);
        pdfsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), pdfsRecyclerView, new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(myActivity, "onItemClick " + position , Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(myActivity, "onLongClick " + position, Toast.LENGTH_LONG).show();
            }
        }));
    }





    public static List<HOTfile> buildData(ArrayList<String> fileNames){
        List<HOTfile> data = new ArrayList<>();

        for(int i =0; i<fileNames.size(); ++i){
            HOTfile mFile = new HOTfile(fileNames.get(i), "Never"); //String fileName, String lastModified
            data.add(mFile);
        }
        return data;
    }


    @Override
    public void onClick(View view){
//        Activity getActivity = getActivity();
//        assert getActivity != null;
//        switch (view.getId())
//        {   /*NOTE: Toasts in fragments require getActivity to get proper CONTEXT */
//            case R.id.btnHandbookSyllabus: //syllabus button pressed
//                openPDF(handBookfileName, R.raw.hot_handbook2015);
//                break;
//            case R.id.btnDrumlineHandbook:
//                //Toast.makeText(getActivity(), "Drumline button pressed", Toast.LENGTH_SHORT).show();
//                openPDF(dl_handBookFileName, R.raw.dl_handbook15);
//                break;
//            default:
//                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
//                break;
//        }
   }

    private void openPDF(String pdfFileName, int rsrc){
        File pdf = new File(filePath + pdfFileName);

        if(!pdf.exists()){
            Toast.makeText(getActivity(), "The FILE DOESNT EXIST.", Toast.LENGTH_SHORT).show();
            CopyReadAssets(pdf, rsrc);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(pdf), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooserIntent = Intent.createChooser(intent, "Open PDF");
        startActivity(chooserIntent);
    }

    private void CopyReadAssets(File thisFile, int rsrc){
        String tempFilePath = filePath;
        InputStream in = null;
        OutputStream out = null;

        File path = new File(tempFilePath);
        path.mkdirs();
        copyToMemory(thisFile, rsrc);

    }

    private void copyToMemory(File outFile, int rsrc){

        try{
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(outFile));
            BufferedInputStream is = new BufferedInputStream(getResources().openRawResource(rsrc));
            copy(is, os);
        } catch (FileNotFoundException e){
            Log.e("File not found.", "FileNotFoundException");
        }
    }


    private void copy(InputStream is, OutputStream os){
        final byte[] buf = new byte[1024];
        int numBytes;

        try{

            while(-1 != (numBytes = is.read(buf))){
                os.write(buf, 0, numBytes);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e){
                Log.e("IOExcept.", "IOException");
            }
        }
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context mContext, final RecyclerView recyclerView, final ClickListener clickListener)
        {
            this.clickListener=clickListener;

            gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    //return super.onSingleTapUp(e);
                    //returning true indicates that our GestureDectector validly handled the event as we expect
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                     //super.onLongPress(e);
                    //find the child view under said cordinates
                    View mChildView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(mChildView != null && clickListener != null)
                    {
                        clickListener.onLongClick(mChildView, recyclerView.getChildLayoutPosition(mChildView));
                    }
                    //check that the view and the clicklistner is not null
                }
            });
        }

        //Helps detect whether or not we touched the view
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            //GestureDector will help decide if we there has been a long press

            View mChildView = rv.findChildViewUnder(e.getX(), e.getY());
            if(mChildView != null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onItemClick(mChildView, rv.getChildLayoutPosition(mChildView));
            }


            return false; //false by default, passing it down to children layouts
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void onItemClick(View view, int position);
        public void onLongClick(View view, int position);
    }


}
