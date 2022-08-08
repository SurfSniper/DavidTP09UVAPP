package com.monash.paindiary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.monash.paindiary.R;
import com.monash.paindiary.activities.AppActivity;
import com.monash.paindiary.activities.MainActivity;
import com.monash.paindiary.databinding.FragmentSignInBinding;
import com.monash.paindiary.enums.FragmentEnums;
import com.monash.paindiary.helper.Helper;
import com.monash.paindiary.helper.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {
    private final static String THIS_CLASS = "SignInFragment";
    private FragmentSignInBinding binding;
    private FirebaseAuth auth;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.btnSignIn.setEnabled(true);



        binding.btnSignIn.setOnClickListener(this::btnSignInOnClicked);





        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).ShowProgress(false);
    }

    private void btnSignInOnClicked(View view) {
        ((MainActivity) getActivity()).ShowProgress(true);
        hideKeyboard();
        binding.btnSignIn.setEnabled(false);
        Intent intent = new Intent(getActivity(), AppActivity.class);
        startActivity(intent);
        ((MainActivity) getActivity()).ShowProgress(false);
        binding.btnSignIn.setEnabled(true);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }



    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}