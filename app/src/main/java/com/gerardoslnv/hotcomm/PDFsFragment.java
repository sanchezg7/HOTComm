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

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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


    List<HOTfile> allLoadedFiles;

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


    public void applyAllFilesToRcylVw()
    {
        mFileRecycleViewAdapter = new fileRecycleViewAdapter(getActivity(), allLoadedFiles);
        pdfsRecyclerView.setAdapter(mFileRecycleViewAdapter);

        //spanCount	int: If orientation is vertical, spanCount is number of columns. If orientation is horizontal, spanCount is number of rows.

        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(myActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); //instructing linear formatting
        pdfsRecyclerView.setLayoutManager(mLinearLayoutManager);
        pdfsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), pdfsRecyclerView, new myClickListener() {
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

        //applyAllFilesToRcylVw();
        String xmlUrl = "https://raw.githubusercontent.com/sanchezg7/HotComm/master/example_files.xml";
        String xmlFileName = "sample.xml";

        //check network settings here, only fetch url if wifi is present

        //if file available, build data
        //perform refresh based on when the user wants to
        new urlFecthXML().execute(xmlUrl, xmlFileName);
        filePath = HOTfile.getFullLocalPath(getActivity());

    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private myClickListener mClickListener;

        public RecyclerTouchListener(Context mContext, final RecyclerView recyclerView, final myClickListener mClickListener)
        {
            this.mClickListener = mClickListener;

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
                    if(mChildView != null && mClickListener != null) {
                        mClickListener.onLongClick(mChildView, recyclerView.getChildLayoutPosition(mChildView));
                    }
                }
            });
        }

        //Helps detect whether or not we touched the view
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            //GestureDector will help decide if we there has been a long press

            View mChildView = rv.findChildViewUnder(e.getX(), e.getY());
            if(mChildView != null && mClickListener != null && gestureDetector.onTouchEvent(e))
            {
                mClickListener.onClick(mChildView, rv.getChildLayoutPosition(mChildView));
            }
            return false; //false by default, passing it down to children layouts
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface myClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    private class urlFetchPDF extends AsyncTask<HOTfile, Void, HOTfile>
    {
        File mPdf;
        File fullPath_Directory;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Opening PDF File...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected HOTfile doInBackground(HOTfile... params) {
            //params[0]: current HOTFile

            String mFileName = params[0].getFileName();
            fullPath_Directory = new File(filePath);
            mPdf = new File(filePath + mFileName);


            //ensure the directory for storing, exist
            if(!fullPath_Directory.exists()){
                fullPath_Directory.mkdirs();
            }
            if(!mPdf.exists())
            {
                String targetURL = params[0].getRemotePath();
                File_Helpers.downloadRemoteFile(targetURL, mPdf); //store into mPdf file handle
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

    //xml link: https://raw.githubusercontent.com/sanchezg7/HotComm/master/example_files.xml

    private class urlFecthXML extends AsyncTask<Object, Void, Object>
    {
        //params[0]: URL
        //params[1]: desired filename

        //type "Object" allows for casting


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(myActivity, "Downloading xml...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Object doInBackground(Object... params) {

            if(params.length != 2)
            {
                Log.e(getString(R.string.app_name), "urlFetchXML failed params requirement");
                return null;
            }

            String targetURL = (String) params[0];
            String origFileName = (String) params[1];
            String tmpFileName = origFileName + ".tmp";

            //tmpFile will become original, by default
            File tmpFile =  new File (myActivity.getFilesDir(), tmpFileName);
            File_Helpers.downloadRemoteFile(targetURL, tmpFile);

            XmlHandler xmlHandler = new XmlHandler();
            List<HOTfile> tempList;

            try {
                InputStream fileIS = new FileInputStream(tmpFile);
                tempList = xmlHandler.parsePdf(fileIS);

                File origFile = new File(myActivity.getFilesDir(), origFileName);

                if(origFile.exists()) //perform comparison
                {
                    //build lists
                    List <HOTfile> origList;
                    InputStream orig_fileIS = new FileInputStream(origFile);
                    origList = xmlHandler.parsePdf(orig_fileIS);
                    //do comparison

                    allLoadedFiles = compareFiles(origList, tempList);

                }
                origFile.delete(); //get rid of old
                tmpFile.renameTo(origFile); //change filename to ORIGINAL now
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected List<HOTfile> compareFiles(List<HOTfile> orig, List<HOTfile> temp)
        {
            //check the id and the version
            for(int i = 0; i < orig.size(); ++i)
            {
                if(temp.get(i).getVersion() > orig.get(i).getVersion())
                {
                    //download updated version of file
                    File_Helpers.downloadRemoteFile(orig.get(i).getRemotePath(), orig.get(i).getmFile());
                }
            }

            int j = orig.size();
            int sz = temp.size();
            while (j != sz)
            {
                orig.add(temp.get(j));
                orig.get(j).addContext(myActivity);
                temp.remove(j);
                --sz;
            }


            return orig;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(myActivity, "Done downloading XML", Toast.LENGTH_SHORT).show();
            applyAllFilesToRcylVw();

        }



    }
}
