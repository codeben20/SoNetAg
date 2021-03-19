package com.sonetag;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * This class sets up the create post page
 * @version 1
 * @author Benjamin BOURG
 */
public class CreatePostFragment extends Fragment implements View.OnClickListener {
    //Declare callback
    private OnButtonClickedListener mCallback;

    //Constructor
    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * @return A new instance of fragment CreatePostFragment.
     */
    public static CreatePostFragment newInstance() {
        return new CreatePostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_create_post, container, false);

        //Add listener to the different elements
        (result.findViewById(R.id.createPost_publish)).setOnClickListener(this);
        (result.findViewById(R.id.createPost_image)).setOnClickListener(this);
        (result.findViewById(R.id.createPost_delete)).setOnClickListener(this);

        return result;
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