package com.gerardoslnv.hotcomm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gerardoslnv.hotcomm.R;


public class FacebookFragment extends Fragment {
    private TextView fb_TV_sessDetails;
    private LoginButton mFbLoginButton;

    private CallbackManager mFbCallBackManager;

    public FacebookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        mFbCallBackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_facebook, container, false);
        fb_TV_sessDetails = (TextView) mView.findViewById(R.id.fb_TV_sessDetails);
        mFbLoginButton = (LoginButton) mView.findViewById(R.id.fb_BTN_loginButton);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mFbCallBackManager = CallbackManager.Factory.create();

        mFbLoginButton.setFragment(this); //"this" points to our current fragment
        mFbLoginButton.registerCallback(mFbCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Login Succesful", Toast.LENGTH_SHORT).show();
                fb_TV_sessDetails.setText(
                        "UserId: "
                        + loginResult.getAccessToken().getUserId()
                        + "\n"
                        + loginResult.getAccessToken().getToken()
                );

            }

            @Override
            public void onCancel() {
                fb_TV_sessDetails.setText("Login attempt cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                fb_TV_sessDetails.setText("Login attempt failed!");
            }



        });
    }
}
