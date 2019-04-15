package com.moisesmojica.myaventon.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.moisesmojica.myaventon.R;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,0, 0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();



        navigationView = findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(this);





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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent i;



        switch (item.getItemId()){
            case R.id.menu_inicio:

                i = new Intent(getApplicationContext(),InicioActivity.class);
                startActivity(i);
                break;
            case R.id.menu_perfil:
                i = new Intent(getApplicationContext(),PerfilActivity.class);
                startActivity(i);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
