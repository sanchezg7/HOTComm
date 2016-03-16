package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PDFsFragment extends Fragment implements View.OnClickListener {

    Button btnHandbookSyllabus;
    Button btnDrumlineHandbook;
    Button btnCopy;
    TextView storagePathTextView;

    private ListView pdfsListView;

    private String AUTH;
    private String handBookfileName = null;
    private String dl_handBookFileName = null;
    private String filePath = null;
    Activity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //set Context
        myActivity = getActivity();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdfs, container, false);
        //assign button to the fragments
//        btnHandbookSyllabus = (Button) view.findViewById(R.id.btnHandbookSyllabus);
//        btnDrumlineHandbook = (Button) view.findViewById(R.id.btnDrumlineHandbook);
//        btnCopy = (Button) view.findViewById(R.id.btnCopy);
//        storagePathTextView = (TextView) view.findViewById(R.id.storagePathTextView);
//        //Listeners
//        btnHandbookSyllabus.setOnClickListener(this);
//        btnDrumlineHandbook.setOnClickListener(this);
//        btnCopy.setOnClickListener(this);

        pdfsListView = (ListView) view.findViewById(R.id.pdfsListView);

        String thisPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        storagePathTextView.setText(thisPath);

        handBookfileName = "hot_handbook2015.pdf";
        dl_handBookFileName = "dl_handbook15.pdf";
        filePath = thisPath + "/HOT_PDF/";

        ArrayList<String> myList = new ArrayList<String> ();
        //Temporary hard adding elements
        myList.add(handBookfileName);
        myList.add(dl_handBookFileName);
        //{handBookfileName, dl_handBookFileName};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myActivity,
//                android.R.layout.simple_list_item_1, items);
//      pdfsListView.setAdapter(adapter);

        //pdfsListView.setAdapter(new fileListAdapter( myActivity, myList));
        fileListAdapter mAdapter = new fileListAdapter(myActivity, myList);
        pdfsListView.setAdapter(mAdapter);

        return view;
    }



    public PDFsFragment() {
        // Required empty public constructor
        //authority for the app
        //AUTH = "com.example.gerardogpc.hotcomm.fileprovider"; CONTENT PROVIDER STUFF
    }

    @Override
    public void onClick(View view){
//        Activity myActivity = getActivity();
//        assert myActivity != null;
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
//                Toast.makeText(myActivity, "Error", Toast.LENGTH_SHORT).show();
//                break;
//        }
   }

    private void openPDF(String pdfFileName, int rsrc){
        File pdf = new File(filePath + pdfFileName);

        if(!pdf.exists()){
            Toast.makeText(myActivity, "The FILE DOESNT EXIST.", Toast.LENGTH_SHORT).show();
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


}
