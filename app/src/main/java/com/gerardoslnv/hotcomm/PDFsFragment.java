package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PDFsFragment extends Fragment {

    private fileRecycleViewAdapter mFileRecycleViewAdapter;
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

    SharedPreferences pdfsFragPrefs;

    private static String filePath = null;
    static Activity myActivity;


    ArrayList<HOTfile> allFiles;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allFiles = new ArrayList<> ();
        //Temporary HARD adding elements
        allFiles = buildData(fileList, allFiles); //Apr 16 consider adding SQLite Database for saving existing files
        filePath = HOTfile.getFullLocalPath();


        mFileRecycleViewAdapter = new fileRecycleViewAdapter(getActivity(), allFiles);
        pdfsRecyclerView.setAdapter(mFileRecycleViewAdapter);
        //pdfsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //telling it to present in a LINEAR format
        //spanCount	int: If orientation is vertical, spanCount is number of columns. If orientation is horizontal, spanCount is number of rows.
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(myActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pdfsRecyclerView.setLayoutManager(mLinearLayoutManager);
        pdfsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), pdfsRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(myActivity, "onItemClick " + position , Toast.LENGTH_LONG).show();
                new urlFetchPDF().execute(allFiles.get(position)); //vararg array new HOTfile[]{allFiles.get(position)}
                return;
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(myActivity, "onLongClick " + position, Toast.LENGTH_LONG).show();
            }
        }));
    }

    public static ArrayList<HOTfile> buildData(ArrayList<String> fileNames, ArrayList<HOTfile> allFiles){
        for(int i = 0; i < fileNames.size(); ++i)
        {
            allFiles.add(new HOTfile(myActivity, fileNames.get(i), "Just Now"));
        }
//        allFiles.add(new HOTfile(myActivity, "dl_handbook2015.pdf", "Yesterday"));
//        allFiles.add(new HOTfile(myActivity, "hot_handbook2015", "Just Now"));
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
                downloadPDF(targetURL, mPdf);
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

        private void downloadPDF(String remoteFilePath, File targetFile)
        {
            try {
                URL fileURL = new URL(remoteFilePath);
                HttpURLConnection mHttpConn = (HttpURLConnection) fileURL.openConnection();
                InputStream inputStream = new BufferedInputStream(mHttpConn.getInputStream());

                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                copy(inputStream, outputStream);
            }catch (IOException e){
                e.printStackTrace();
            }
            return;
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
            Log.i("copy function", "File copied over");
        }
    }

}
