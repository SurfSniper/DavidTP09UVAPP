package com.monash.paindiary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.monash.paindiary.R;
import com.monash.paindiary.databinding.ActivityAppBinding;
import com.monash.paindiary.entity.PainRecord;
import com.monash.paindiary.enums.NavigationItem;
import com.monash.paindiary.helper.UserInfo;
import com.monash.paindiary.viewmodel.PainRecordViewModel;

import java.util.Date;
import java.util.Random;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding binding;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private NavController navController;
    public boolean isAlarmSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        ShowProgress(true);
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setSupportActionBar(binding.appBar.toolbar);

        //TODO : Remove in production in final
//        UserInfo.setINSTANCE("v@v.com", true);
        getSupportActionBar().setTitle("Home");
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(
                R.id.nav_home_fragment,
                R.id.nav_pain_record_view_fragment,
                R.id.nav_pain_data_entry_fragment,
                R.id.nav_report_view_fragment)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        //Sets up a NavigationView for use with a NavController.
        NavigationUI.setupWithNavController(binding.navView, navController);
        //Sets up a Toolbar for use with a NavController.
        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController, appBarConfiguration);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.btn_sign_out).setOnMenuItemClickListener(this::navBtnSignOutOnClicked);
        navigationView.getMenu().findItem(R.id.btn_dummy_records).setOnMenuItemClickListener(this::navBtnDummyDataAddOnClicked);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_email)).setText(UserInfo.getUserEmail());
        if (UserInfo.getInstance() != null) {
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_name)).setText(UserInfo.getInstance().getUserFullName());
        }

    }

    private boolean navBtnSignOutOnClicked(MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    private boolean navBtnDummyDataAddOnClicked(MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmation")
                .setMessage("This will delete all previous data and insert 10 new random data.")
                .setNegativeButton("ACCEPT", (dialog, which) -> {
                    ShowProgress(true);
                    BuildDummyData();
                })
                .setPositiveButton("DECLINE", (dialog, which) -> {
                    // Nothing
                })
                .show();
        return true;
    }

    public void ManualSelectNavigationItem(@NonNull NavigationItem navigationItem) {
        switch (navigationItem) {
            case HomeView:
                binding.navView.setCheckedItem(R.id.nav_home_fragment);
                break;
            case RecordView:
                binding.navView.setCheckedItem(R.id.nav_pain_record_view_fragment);
                break;
            case ReportView:
                binding.navView.setCheckedItem(R.id.nav_report_view_fragment);
                break;
            case MapView:
                binding.navView.setCheckedItem(R.id.nav_map_view_fragment);
                break;
            case DataEntry:
                binding.navView.setCheckedItem(R.id.nav_pain_data_entry_fragment);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.nav_home_fragment) {
            finishAffinity();
        }
        super.onBackPressed();
    }

    public void ShowProgress(boolean isShown) {
        binding.loadingPanel.setVisibility(isShown ? View.VISIBLE : View.INVISIBLE);
    }

    private void BuildDummyData() {
        new Thread(() -> {
            PainRecordViewModel viewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);
            viewModel.deleteAll();
            Random random = new Random();
            Date currentDate = new Date();
            String[] painAreaArray = new String[]{"back", "neck", "head", "knees", "hips", "abdomen", "elbows", "shoulders", "shins", "jaw", "facial"};
            String[] moodArray = new String[]{"very low", "low", "average", "good", "very good"};
            Float[] temps = new Float[]{11.0f, 18.3f, 30.4f, 22.1f, 9f, 20f, 15.5f};

            for (int i = 0; i < 10; i++) {
                PainRecord newPainRecord = new PainRecord(
                        UserInfo.getUserEmail(),
                        new Date(currentDate.getTime() - (i * 24 * 60 * 60 * 1000)).getTime(),
                        i == 5 ? i : i > 5 ? random.nextInt(5) + 6 : random.nextInt(5),
                        painAreaArray[random.nextInt(painAreaArray.length)],
                        i == 5 ? moodArray[2] : i > 5 ? moodArray[random.nextInt(2)] : moodArray[random.nextInt(2) + 3],
                        10000,
                        i == 5 ? 8000 : i > 6 ? random.nextInt(5000) + 1000 : random.nextInt(5000) + 5000,
                        temps[random.nextInt(temps.length)],
                        random.nextInt(100),
                        random.nextInt(400) + 700
                );
                viewModel.insert(newPainRecord);
            }
            setTodayEntry();
            runOnUiThread(() -> {
                ShowProgress(false);
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Success")
                        .setMessage("10 New records inserted!")
                        .setPositiveButton("CANCEL", (dialog, which) -> {
                            // Nothing
                        })
                        .show();
            });
        }).start();
    }

    private void setTodayEntry() {
        if (UserInfo.getInstance() != null) {
            PainRecordViewModel viewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);
            viewModel.findRecordByDate(new Date()).thenApply(painRecord -> {
                if (painRecord != null) {
                    UserInfo.getInstance().setTodayEntryUID(painRecord.getUid());
                }
                return painRecord;
            });
        }
    }
}