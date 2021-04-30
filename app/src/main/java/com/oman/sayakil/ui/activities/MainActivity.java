package com.oman.sayakil.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.ActivityMainBinding;
import com.oman.sayakil.ui.bottom_fragments.CycleFragment;
import com.oman.sayakil.ui.bottom_fragments.MapsFragment;
import com.oman.sayakil.ui.bottom_fragments.KeyFragment;
import com.oman.sayakil.ui.drawer_fragments.MessageFragment;
import com.oman.sayakil.ui.drawer_fragments.PaymentInformationFragment;
import com.oman.sayakil.ui.drawer_fragments.RentTimerFragment;
import com.oman.sayakil.ui.drawer_fragments.SettingsFragmentPref;
import com.oman.sayakil.ui.drawer_fragments.ContactUsFragment;
import com.oman.sayakil.ui.fragments.MemberShipFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.mainContent.toolbar);


        mToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.mainContent.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(mToggle);
        binding.drawerNavView.setNavigationItemSelectedListener(this);
        binding.mainContent.bottomNavView.setOnNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new MapsFragment())
                    .commit();
            binding.mainContent.bottomNavView.setSelectedItemId(R.id.bottom_nav_view);
        }


        mToggle.syncState();
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_profile:

                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                break;

            case R.id.nav_email:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new MessageFragment())
                        .commit();

                break;

            case R.id.nav_payment:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new PaymentInformationFragment())
                        .commit();
                break;

            case R.id.nav_member_ship:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new MemberShipFragment())
                        .commit();
                break;

            case R.id.nav_rent:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new CycleFragment())
                        .commit();
                binding.mainContent.bottomNavView.setSelectedItemId(R.id.bottom_nav_view);

                MenuItem item1 = binding.mainContent.bottomNavView.getMenu().findItem(R.id.nav_bottom_cycle);
                item1.setChecked(true);
                break;

            case R.id.nav_feedback:
                startActivity(new Intent(MainActivity.this, FeedBackActivity.class));

                break;
            case R.id.nav_statistics:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new ContactUsFragment())
                        .commit();

                break;
            case R.id.nav_rentle_timer:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new RentTimerFragment())
                        .commit();
                break;
            case R.id.nav_setting:

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new SettingsFragmentPref())
                        .commit();

                break;
            case R.id.nav_logout:


                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                    finish();
                                    Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Some Error Occure", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                break;

            case R.id.nav_share:


                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
                shareIntent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Choose"));


                break;

            case R.id.nav_bottom_cycle:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new CycleFragment())
                        .commit();
                break;

            case R.id.nav_bottom_my_location:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new MapsFragment())
                        .commit();
                break;

            case R.id.nav_bottom_key:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new KeyFragment())
                        .commit();
                break;

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }
}