package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;

public class HelpActivity extends NavigationHelperActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_help);
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_help);
    }
}
