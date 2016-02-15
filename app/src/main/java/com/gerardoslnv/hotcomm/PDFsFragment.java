package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class PDFsFragment extends Fragment implements View.OnClickListener {

    Button btnHandbookSyllabus;
    Button btnDrumlineHandbook;
    Button btnCopy;
    TextView storagePathTextView;
    private String AUTH;
    private String handBookfileName = null;
    private String filePath = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdfs, container, false);
        //assign button to the fragments
        btnHandbookSyllabus = (Button) view.findViewById(R.id.btnHandbookSyllabus);
        btnDrumlineHandbook = (Button) view.findViewById(R.id.btnDrumlineHandbook);
        btnCopy = (Button) view.findViewById(R.id.btnCopy);
        storagePathTextView = (TextView) view.findViewById(R.id.storagePathTextView);
        btnHandbookSyllabus.setOnClickListener(this);
        btnDrumlineHandbook.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        String thisPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        storagePathTextView.setText(thisPath);

        handBookfileName = "hot_handbook2015.pdf";
        filePath = thisPath + "/HOT_PDF/";

        return view;
    }


    public PDFsFragment() {
        // Required empty public constructor
        //authority for the app
        //AUTH = "com.example.gerardogpc.hotcomm.fileprovider"; CONTENT PROVIDER STUFF
    }

    @Override
    public void onClick(View view){
        Activity myActivity = getActivity();
        assert myActivity != null;
        switch (view.getId())
        {   /*NOTE: Toasts in fragments require getActivity to get proper CONTEXT */
            case R.id.btnHandbookSyllabus: //syllabus button pressed
                Toast.makeText(myActivity, "Syllabus button pressed", Toast.LENGTH_SHORT).show();
                File pdf = new File(filePath + handBookfileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(pdf), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooserIntent = Intent.createChooser(intent, "Open Syllabus");
                startActivity(chooserIntent);
                break;
            case R.id.btnDrumlineHandbook:
                Toast.makeText(getActivity(), "Drumline button pressed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopy:
                CopyReadAssets(handBookfileName); //pass in the file name to open up
                break;
            default:
                Toast.makeText(myActivity, "Error", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private void CopyReadAssets(String fileName){
        Activity myActivity = getActivity();

        String tempFilePath = filePath;
        InputStream in = null;
        OutputStream out = null;

        File path = new File(tempFilePath);
        path.mkdirs();
        File outFile;

        outFile = new File (tempFilePath + fileName);
        if(!outFile.exists()){
            Toast.makeText(myActivity, "The FILE DOESNT EXIST.", Toast.LENGTH_SHORT).show();
            copyToMemory(outFile);
        }
        }
        /*
        do{
            tempFilePath += fileName;
            outFile = new File(tempFilePath);
        }while(outFile.exists() && !outFile.isDirectory());*/



    private void copyToMemory(File outFile){
        try{
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(outFile));
            BufferedInputStream is = new BufferedInputStream(getResources().openRawResource(R.raw.hot_handbook2015));
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
