package com.moisesmojica.myaventon.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.moisesmojica.myaventon.Fragment.InicioFragment;
import com.moisesmojica.myaventon.Fragment.PerfilFragment;
import com.moisesmojica.myaventon.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navview);
        setFragmentByDefault();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              boolean fragmentTransaction = false;

                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.menu_inicio:
                        fragment = new InicioFragment();
                        fragmentTransaction = true;
                        break;

                    case R.id.menu_perfil:
                        fragment = new PerfilFragment();
                        fragmentTransaction = true;
                        break;
                }

                if (fragmentTransaction){
                    changeFragment(fragment,item);
                    drawerLayout.closeDrawers();
                }

                return true;
            }
        });

    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return   true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragmentByDefault(){
        changeFragment(new InicioFragment(),navigationView.getMenu().getItem(0));

    }

    private void changeFragment(Fragment fragment, MenuItem item){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame,fragment)
                .commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());

    }
}
