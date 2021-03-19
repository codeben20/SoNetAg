package com.sonetag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Class which manages the add social account fragment
 * @version 1
 * @author Benjamin BOURG
 */
public class AddAccountFragment extends Fragment implements View.OnClickListener {

    //Declare callback
    private OnButtonClickedListener mCallback;


    public AddAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AddAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAccountFragment newInstance() {
        AddAccountFragment fragment = new AddAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_add_account, container, false);

        //Set up the listener on each social network icon
        (result.findViewById(R.id.addAccount_twitter)).setOnClickListener(this);
        (result.findViewById(R.id.addAccount_reddit)).setOnClickListener(this);
        (result.findViewById(R.id.addAccount_tumblr)).setOnClickListener(this);
        (result.findViewById(R.id.addAccount_linkedin)).setOnClickListener(this);
        (result.findViewById(R.id.addAccount_yammer)).setOnClickListener(this);

        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
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