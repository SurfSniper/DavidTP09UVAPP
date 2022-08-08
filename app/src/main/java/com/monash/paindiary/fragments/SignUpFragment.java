package com.monash.paindiary.fragments;

import android.content.Context;
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
import com.monash.paindiary.activities.MainActivity;
import com.monash.paindiary.databinding.FragmentSignUpBinding;
import com.monash.paindiary.enums.FragmentEnums;
import com.monash.paindiary.helper.Helper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private final static String THIS_CLASS = "SignUpFragment";
    private FragmentSignUpBinding binding;
    private FirebaseAuth auth;

    public SignUpFragment() {
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
//        return inflater.inflate(R.layout.fragment_sign_up, container, false);
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.btnSignup.setEnabled(true);
        binding.btnSignIn.setEnabled(true);
        binding.btnClose.setEnabled(true);

        binding.btnClose.setOnClickListener(v ->
                ((MainActivity) getActivity()).changeFragment(FragmentEnums.SignIn));

        binding.btnSignIn.setOnClickListener(v ->
                ((MainActivity) getActivity()).changeFragment(FragmentEnums.SignIn));

        binding.btnSignup.setOnClickListener(this::btnSignUpOnClick);

        binding.editFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.inputLayoutFullName.setEndIconVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutFullName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.inputLayoutEmail.setEndIconVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editCreatePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.inputLayoutCreatePassword.setEndIconVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutCreatePassword.setErrorEnabled(false);
                binding.inputLayoutReenterPassword.setErrorEnabled(false);
                binding.inputLayoutReenterPassword.setEndIconVisible(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.editReenterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.inputLayoutReenterPassword.setEndIconVisible(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLayoutReenterPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    private void btnSignUpOnClick(View view) {
        ((MainActivity) getActivity()).ShowProgress(true);
        hideKeyboard();
        binding.btnSignup.setEnabled(false);
        binding.btnSignIn.setEnabled(false);
        binding.btnClose.setEnabled(false);
        if (inputValidationCheck()) {
            try {
                auth.createUserWithEmailAndPassword(binding.editEmail.getText().toString(), binding.editCreatePassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Registration successful, please log in", Toast.LENGTH_LONG).show();
                                ((MainActivity) getActivity()).changeFragment(FragmentEnums.SignIn);
                            } else {
                                Toast.makeText(getContext(), "Fail to create new user.", Toast.LENGTH_SHORT).show();
                                Log.e(THIS_CLASS, "btnSignUpOnClick: MESSAGE FROM FIREBASE: " + task.getException());
                            }
                            ((MainActivity) getActivity()).ShowProgress(false);
                            binding.btnSignup.setEnabled(true);
                            binding.btnSignIn.setEnabled(true);
                            binding.btnClose.setEnabled(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Registration un-successful! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(THIS_CLASS, "EXCEPTION: btnSignUpOnClick: " + e.getMessage());
                            ((MainActivity) getActivity()).ShowProgress(false);
                            binding.btnSignup.setEnabled(true);
                            binding.btnSignIn.setEnabled(true);
                            binding.btnClose.setEnabled(true);
                        });
            } catch (Exception e) {
                Toast.makeText(getContext(), "Registration un-successful! Please check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
                Log.e(THIS_CLASS, "EXCEPTION: btnSignUpOnClick: " + e.getMessage());
                ((MainActivity) getActivity()).ShowProgress(false);
                binding.btnSignup.setEnabled(true);
                binding.btnSignIn.setEnabled(true);
                binding.btnClose.setEnabled(true);
            }
        } else {
            binding.btnSignup.setEnabled(true);
            binding.btnSignIn.setEnabled(true);
            binding.btnClose.setEnabled(true);
            ((MainActivity) getActivity()).ShowProgress(false);
        }
    }

    private boolean inputValidationCheck() {
        boolean isAllValid = true;
        String fullName = binding.editFullName.getText().toString();
        String email = binding.editEmail.getText().toString();
        String createPass = binding.editCreatePassword.getText().toString();
        String reEnterPass = binding.editReenterPassword.getText().toString();

        if (fullName.isEmpty()) {
            binding.inputLayoutFullName.setEndIconVisible(false);
            binding.inputLayoutFullName.setError("Full name cannot be blank");
        }
        if (email.isEmpty()) {
            binding.inputLayoutEmail.setEndIconVisible(false);
            binding.inputLayoutEmail.setError("Email cannot be blank");
            isAllValid = false;
        } else if (!Helper.isEmailValid(email)) {
            binding.inputLayoutEmail.setEndIconVisible(false);
            binding.inputLayoutEmail.setError("Not valid email id");
            isAllValid = false;
        }
        if (createPass.isEmpty()) {
            binding.inputLayoutCreatePassword.setEndIconVisible(false);
            binding.inputLayoutCreatePassword.setError("Password cannot be blank");
            isAllValid = false;
        } else if (createPass.length() < 6 || createPass.length() > 12) {
            binding.inputLayoutCreatePassword.setEndIconVisible(false);
            binding.inputLayoutCreatePassword.setError("Password should be 6 to 12 characters long");
            isAllValid = false;
        } else if (!Helper.isPasswordValid(createPass)) {
            binding.inputLayoutCreatePassword.setEndIconVisible(false);
            binding.inputLayoutCreatePassword.setError("Password must contains digits, lower, and upper case letters");
            isAllValid = false;
        }
        if (reEnterPass.isEmpty()) {
            binding.inputLayoutReenterPassword.setEndIconVisible(false);
            binding.inputLayoutReenterPassword.setError("Password cannot be blank");
            isAllValid = false;
        } else if (!reEnterPass.equals(createPass)) {
            binding.inputLayoutReenterPassword.setEndIconVisible(false);
            binding.inputLayoutReenterPassword.setError("Password does not match");
            isAllValid = false;
        }
        return isAllValid;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}