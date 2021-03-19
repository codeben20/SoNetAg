package com.sonetag;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Manage the bottom sheet creation
 * @version 1
 * @author Benjamin BOURG
 */

public class BottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    final private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String url = "http://us-central1-onefeedtest-b74c6/us-central1/postTwitterApi_newPost?idToken=";

    //Declare callback
    private OnButtonClickedListener mCallback;

    //Constructor
    public BottomSheet(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_network_layout, container, false);
        ListView lV = v.findViewById(R.id.listview_select_socials);
        lV.setAdapter(new ListAdapter(getContext()));

        Button btn = v.findViewById(R.id.button_done);
        btn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // 4 - Call the method that creating callback after being attached to parent activity
        this.createCallbackToParentActivity();
    }

    @Override
    public void onClick(View v) {
        mCallback.onButtonClicked(v);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(){
        try {
            //Parent activity will automatically subscribe to callback
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }
}
