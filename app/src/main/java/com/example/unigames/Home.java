package com.example.unigames;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.unigames.Fragments.GameFragment;
import com.example.unigames.Fragments.HomeFragment;
import com.example.unigames.Fragments.ProfileFragment;
import com.example.unigames.Fragments.ProfileGoogleFragment;
import com.example.unigames.Fragments.SettingsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;

    GameFragment gameFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getSupportActionBar().setTitle("");
        Drawable burger = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_action_foreground, this.getTheme());

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(burger);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        toggle.syncState();
        updateNavHeader();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavHeader();

        //getSupportActionBar().setTitle("Home");

        //getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        updateNavHeader();

        if (id == R.id.nav_home) {
            updateNavHeader();
            getSupportActionBar().setTitle("");

            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        } else if (id == R.id.nav_profile) {

            for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if(user.getProviderId().equals("password")) {
                    updateNavHeader();
                    getSupportActionBar().setTitle("Profile");
                    //Toast.makeText(this,currentUser.getProviderId(),Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();

                } else {
                    updateNavHeader();
                    getSupportActionBar().setTitle("Profile");
                    //Toast.makeText(this,currentUser.getProviderId(),Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileGoogleFragment()).commit();

                }
            }
        } else if (id == R.id.nav_settings) {
            updateNavHeader();
            getSupportActionBar().setTitle("Favourites");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingsFragment()).commit();


        }else if (id == R.id.nav_signout) {

            mAuth.signOut();
            mGoogleSignInClient.signOut();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhot = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // Usamos Glide para cargar la imagen
        //primero necesitamos importar la libreria en los dos build.gradle en el de aplicacion linea 8 y en el de app

        //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhot);
        Picasso.get().load(currentUser.getPhotoUrl()).into(navUserPhot);
    }


}
