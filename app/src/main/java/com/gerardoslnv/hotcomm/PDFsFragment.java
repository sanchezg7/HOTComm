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

    private static String filePath = null;
    static Activity myActivity;


    List<HOTfile> docs;

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

        //applyAllFilesToRcylVw();
        String xmlUrl = "https://raw.githubusercontent.com/sanchezg7/HotComm/master/example_files.xml";
        String xmlFileName = "sample.xml";

        //check network settings here, only fetch url if wifi is present

        //get the xml file and populate the list of pdf for selection
        new pdfHouseKeeping().execute(xmlUrl, xmlFileName);
        filePath = HOTfile.getFullLocalPath(getActivity());

    }

    private void openPDF(File mPdf)
    {
        if(!mPdf.exists()) {
            Toast.makeText(getActivity(), mPdf + " doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(mPdf), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //the new activity is not kept in the history stack
        Intent chooserIntent = Intent.createChooser(intent, "Open PDF");
        startActivity(chooserIntent);
    }

    public void applyAllFilesToRcylVw()
    {
        mFileRecycleViewAdapter = new fileRecycleViewAdapter(getActivity(), docs);
        pdfsRecyclerView.setAdapter(mFileRecycleViewAdapter);

        //spanCount	int: If orientation is vertical, spanCount is number of columns. If orientation is horizontal, spanCount is number of rows.

        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(myActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); //instructing linear formatting
        pdfsRecyclerView.setLayoutManager(mLinearLayoutManager);
        pdfsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), pdfsRecyclerView, new myClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Toast.makeText(myActivity, "onItemClick " + position , Toast.LENGTH_LONG).show();
                        openPDF(HOTfile.createFileHandle(docs.get(position)));
                        //new urlFetchPDF().execute(docs.get(position)); //vararg array new HOTfile[]{docs.get(position)}

                        return;
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        Toast.makeText(myActivity, "onLongClick " + position, Toast.LENGTH_LONG).show();
                        //Consider showing options here for deleting etc.
                    }
                })
        );
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

    //xml link: https://raw.githubusercontent.com/sanchezg7/HotComm/master/example_files.xml

    private class pdfHouseKeeping extends AsyncTask<Object, Void, Object>
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

            if(params.length < 2)
            {
                Log.e(getString(R.string.app_name), "urlFetchXML failed params requirement");
                return null;
            }

            try {
                fetchAndParseXML((String) params[0], (String) params[1]);
                ensureFilesAreDownloaded();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void ensureFilesAreDownloaded()
        {
            //Global List<HOTFile> docs

            for(int i = 0; i < docs.size(); ++i)
            {
                File mFile = HOTfile.createFileHandle(docs.get(i));
                if(!mFile.exists())
                {
                    String remotePath = docs.get(i).getRemotePath();
                    File_Helpers.downloadRemoteFile(remotePath, mFile);
                }
            }
            return;
        }


        protected void fetchAndParseXML(String targetURL, String origFileName) throws FileNotFoundException, XmlPullParserException, IOException
        {

            String tmpFileName = origFileName + ".tmp";

            //tmpFile will become original, by default
            File tmpFile =  new File (myActivity.getFilesDir(), tmpFileName);
            File_Helpers.downloadRemoteFile(targetURL, tmpFile);

            XmlHandler xmlHandler = new XmlHandler();
            List<HOTfile> tempList;

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

                docs = compareFiles(origList, tempList);

            }
            origFile.delete(); //get rid of old
            tmpFile.renameTo(origFile); //change filename to ORIGINAL now
        }


        protected List<HOTfile> compareFiles(List<HOTfile> orig, List<HOTfile> temp)
        {
            //check the id and the version
            for(int i = 0; i < orig.size(); ++i)
            {
                if(temp.get(i).getVersion() > orig.get(i).getVersion())
                {
                    //download updated version of file
                    File mFile = HOTfile.createFileHandle(orig.get(i));
                    File_Helpers.downloadRemoteFile(orig.get(i).getRemotePath(), mFile);
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
            //Toast.makeText(myActivity, "Done downloading XML", Toast.LENGTH_SHORT).show();
            applyAllFilesToRcylVw();

        }



    }
}
