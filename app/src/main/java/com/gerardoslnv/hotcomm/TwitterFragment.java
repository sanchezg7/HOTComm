package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class TwitterFragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Vivz on inflater covers more indepth
        return inflater.inflate(R.layout.fragment_twitter, container, false);
    }

}
