package com.cse110.eventlit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cse110.eventlit.db.User;
import com.cse110.utils.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrganizerFeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private OnCompleteListener<User> mCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_main_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: FAB for adding new event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerFeedActivity.this, CreateEventActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = new CardFragment();
            Bundle pageType = new Bundle();
            pageType.putString("type", "feed");
            pageType.putBoolean("organizer", true);
            fragment.setArguments(pageType);
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            updateHeader(user.getDisplayName(), user.getEmail());
        }
    }

    // Updates the name/email/profile pic that is displayed in the hamburger menu
    public void updateHeader(String name, String email) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View profileView = navigationView.getHeaderView(0);
        TextView nameTextView = (TextView) profileView.findViewById(R.id.nameTextView);
        TextView emailTextView = (TextView) profileView.findViewById(R.id.emailTextView);
        nameTextView.setText(name);
        emailTextView.setText(email);
        // TODO update prof pic
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {


                            UserUtils.logOut(new OnCompleteListener<User>() {
                                @Override
                                public void onComplete(@NonNull Task<User> task) {
                                    finish();
                                }
                            });
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_scrolling, menu);
        return true;
    }

    // Three dots menu
    // TODO: Handle sorting/filtering here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            return true;
        }

        else if (id == R.id.action_filter) {

        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Open intents to other activities here
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Go to home page
        } else if (id == R.id.nav_explore) {
            Intent act = new Intent(OrganizerFeedActivity.this, ExploreActivity.class);
            act.putExtra("organizer", true);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_follow_orgs) {
            Intent act = new Intent(OrganizerFeedActivity.this, OrganizationSelectionActivity.class);
            act.putExtra("organizer", true);
            startActivity(act);
            finish();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(OrganizerFeedActivity.this, SettingsActivity.class));

            Intent act = new Intent(OrganizerFeedActivity.this, ExploreActivity.class);
            act.putExtra("organizer", true);
            startActivity(act);
            finish();

            finish();
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            UserUtils.logOut(new OnCompleteListener<User>() {
                @Override
                public void onComplete(@NonNull Task<User> task) {
                    finish();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
