package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PDFsFragment extends Fragment {

    private fileRecycleViewAdapter mFileRecycleViewAdapter;

    /*//RecyclerView 101
    Need LayoutManager to make it work, no more gridviews
    Need adapter to decide how to put the elements
    To display a row, create a XML file and inflate it in code (expensive linear operation)
        Finding items for each row is also expensive
        RecycleView helps avoid expensive costs
     1: Inflate the layout (onCreateViewHolder)
     2: use the ViewHolder to populate your current row inside the BindViewHolder
     */
    private RecyclerView pdfsRecyclerView; //extends from ViewGroup (layout class), far more flexible

    //SharedPreferences pdfsFragPrefs;

    private static String filePath = null;
    static Activity myActivity;


    ArrayList<HOTfile> allLoadedFiles;

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
//  SharedPreferences Attempt
//    public void saveSharedPreferences (SharedPreferences mSp, ArrayList<HOTfile> myFiles){
//
//        String mKey = getResources().getString(R.string.SP_pdfFileList);
//        Set<HOTfile> fileSet = new HashSet<HOTfile> (Arrays.asList(myFiles));
//
//        mSp = getActivity().getSharedPreferences(mKey, Context.MODE_PRIVATE);
//        SharedPreferences.Editor mSpFileListEditor = mSp.edit();
//        try{
//            mSpFileListEditor.putString(mKey, ObjectSerializer.)
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        return;
//    }

    ArrayList<HOTfile> initialSetupAllFiles()
    {
        ArrayList<HOTfile> temp = new ArrayList<>();

        return temp;
    }


    public void applyAllFilesToRcylVw()
    {
        mFileRecycleViewAdapter = new fileRecycleViewAdapter(getActivity(), allLoadedFiles);
        pdfsRecyclerView.setAdapter(mFileRecycleViewAdapter);

        //spanCount	int: If orientation is vertical, spanCount is number of columns. If orientation is horizontal, spanCount is number of rows.

        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(myActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); //instructing linear formatting
        pdfsRecyclerView.setLayoutManager(mLinearLayoutManager);
        pdfsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), pdfsRecyclerView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(myActivity, "onItemClick " + position , Toast.LENGTH_LONG).show();
                        new urlFetchPDF().execute(allLoadedFiles.get(position)); //vararg array new HOTfile[]{allLoadedFiles.get(position)}
                        return;
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        Toast.makeText(myActivity, "onLongClick " + position, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allLoadedFiles = new ArrayList<> ();
        filePath = HOTfile.getFullLocalPath();

        // Build the arraylist from the original xml file, if it exists
        // Check if there are any new elements in the XML for new versions or new files
        //      Start async task to download the xml, if settings allow
        // If successful downloading, build an arraylist of the new xml file
        // Compare the new arraylist with the original arraylist
        // If new version is greater for a file, download it from the URL
        // If a new file is encountered, download it from the URL
        // Refresh the recycler view

        applyAllFilesToRcylVw();

    }

    //needs to ARRAYS<HOTFile> and compare new with original. Update them accordingly
    public static ArrayList<HOTfile> updateData(ArrayList<String> fileNames, ArrayList<HOTfile> allFiles)
    {
        for(int i = 0; i < fileNames.size(); ++i)
        {
            allFiles.add(new HOTfile(myActivity, fileNames.get(i), "1"));
        }
//        allLoadedFiles.add(new HOTfile(myActivity, "dl_handbook2015.pdf", "Yesterday"));
//        allLoadedFiles.add(new HOTfile(myActivity, "hot_handbook2015", "Just Now"));
        return allFiles;
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
                    //returning true indicates that our GestureDetector validly handled the event as we expect
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                     //super.onLongPress(e);
                    //find the child view under said coordinates
                    View mChildView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(mChildView != null && clickListener != null) {
                        clickListener.onLongClick(mChildView, recyclerView.getChildLayoutPosition(mChildView));
                    }
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
                clickListener.onClick(mChildView, rv.getChildLayoutPosition(mChildView));
            }
            return false; //false by default, passing it down to children layouts
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    private class urlFetchPDF extends AsyncTask<HOTfile, Void, HOTfile>
    {
        File mPdf;
        File fullPath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Opening File...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected HOTfile doInBackground(HOTfile... params) {
            //params[0]: the path to the directory
            //params[1]: the filename desired to save

            String mFileName = params[0].getFileName();
            fullPath = new File(filePath);
            mPdf = new File(filePath + mFileName);


            //ensure the directory for storing, exist
            if(!fullPath.exists()){
                fullPath.mkdirs();
            }
            if(!mPdf.exists())
            {
                String targetURL = params[0].getRemotePath();
                File_Helpers.downloadPDF(targetURL, mPdf);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HOTfile hotFile) {
            super.onPostExecute(hotFile);
            openPDF();

        }

        private void openPDF()
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(mPdf), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //the new activity is not kept in the history stack
            Intent chooserIntent = Intent.createChooser(intent, "Open PDF");
            startActivity(chooserIntent);
        }



    }

}
