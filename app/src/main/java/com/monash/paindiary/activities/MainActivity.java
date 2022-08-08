package com.monash.paindiary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.monash.paindiary.enums.FragmentEnums;
import com.monash.paindiary.R;
import com.monash.paindiary.databinding.ActivityMainBinding;
import com.monash.paindiary.fragments.SignInFragment;
import com.monash.paindiary.fragments.SignUpFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private static final SignInFragment signInFragment = new SignInFragment();
    private static final SignUpFragment signUpFragment = new SignUpFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//        getSupportActionBar().hide();

        changeFragment(FragmentEnums.SignIn);
//        byPassSignIn();
    }

    private void byPassSignIn() {
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
    }

    public void changeFragment(FragmentEnums replaceFragment) {
        ShowProgress(true);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragmentFromStack = fragmentManager.findFragmentByTag(replaceFragment.name());
        if (fragmentFromStack != null) {
            fragmentManager.popBackStack();
        } else {
            switch (replaceFragment) {
                case SignIn:
                    transaction.replace(binding.fragmentContainerView.getId(), signInFragment, FragmentEnums.SignIn.name());
                    break;
                case SignUp:
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.fade_out, R.anim.slide_down);
                    transaction.replace(binding.fragmentContainerView.getId(), signUpFragment, FragmentEnums.SignUp.name());
                    transaction.addToBackStack(null);
                    break;
            }
            transaction.commit();
        }
    }

    @Override
    protected void onPause() {
        for (Fragment fragment : fragmentManager.getFragments())
            fragmentManager.beginTransaction().remove(fragment).commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (!signInFragment.isAdded())
            changeFragment(FragmentEnums.SignIn);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (signInFragment.isVisible()) {
            finishAffinity();
        }
        super.onBackPressed();
    }

    public void ShowProgress(boolean isShown) {
        binding.loadingPanel.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }
}