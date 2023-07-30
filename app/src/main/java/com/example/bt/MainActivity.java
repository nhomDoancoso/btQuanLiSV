package com.example.bt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bt.Fragment.DashboardFragment;
import com.example.bt.Fragment.HomeFragment;
import com.example.bt.Fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các Fragment
        HomeFragment fragment1 = new HomeFragment();
        DashboardFragment fragment2 = new DashboardFragment();
        NotificationFragment fragment3 = new NotificationFragment();

        // Hiển thị Fragment Home mặc định
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment1)
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.action_home) {
                    selectedFragment = fragment1;
                } else if (item.getItemId() == R.id.action_dashboard) {
                    selectedFragment = fragment2;
                } else if (item.getItemId() == R.id.action_notify) {
                    selectedFragment = fragment3;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, selectedFragment)
                            .commit();
                    return true;
                }

                return false;
            }
        });

    }
}
