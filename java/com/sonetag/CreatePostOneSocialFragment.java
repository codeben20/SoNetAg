package com.sonetag;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * This class sets up the create post page for one social network display
 * @version 1
 * @author Benjamin BOURG
 */
public class CreatePostOneSocialFragment extends Fragment implements View.OnClickListener {
    //Declare callback
    private OnButtonClickedListener mCallback;

    public CreatePostOneSocialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * @return A new instance of fragment CreatePostFragment.
     */
    public static CreatePostOneSocialFragment newInstance() {
        return new CreatePostOneSocialFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_create_post_one_social, container, false);

        //Add the listeners to the different elements
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