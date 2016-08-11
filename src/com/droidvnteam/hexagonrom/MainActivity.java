package com.droidvnteam.hexagonrom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.lang.System;

import com.droidvnteam.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_EXTRA_INIT_FRAGMENT = "init_fragment";
    public static final String INIT_FRAGMENT_HALO = "halo";

    private static final String NAV_ITEM_ID = "navItemId";
    static final String TAG = MainActivity.class.getSimpleName();


    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private int id;
    private long startTime;
    private boolean secondBack=false;

    private View mView;

    @Override
    protected void onStart() {
	    super.onStart();

	    new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
	            try {
                    boolean canGainSu = SuShell.canGainSu(getApplicationContext());
                    return canGainSu;
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage(), e);
                    Snackbar.make(mView, R.string.cannot_get_su_start,
                            Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                    return true; // I want to start the app regardles of having root or not
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                setProgressBarIndeterminateVisibility(false);

                if (!result) {
                    Snackbar.make(mView, R.string.cannot_get_su,
                            Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                    finish();
                }
            }
        }.execute();
    }

		

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mView = (View) findViewById(R.id.drawer_layout);

        Fragment fragment;
        String title = null;
        String fragmentExtra = getIntent().getStringExtra(INTENT_EXTRA_INIT_FRAGMENT);
        if (INIT_FRAGMENT_HALO.equals(fragmentExtra)) {
            fragment = new HaloFragment();
            title = getString(R.string.halo_settings_title);
        } else {
            if (title != null) {
                Log.w(TAG, "Unknown init fragment: " + fragmentExtra);
            }
            fragment = new AboutFragment();
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_main, fragment);
        tx.commit();
        if (title != null) {
            setTitle(title);
        }


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


   }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*
            **If back was pressed the first time,only show snackbar
            **else exit the app provided the user presses back
            **the second time within 2.5 seconds of the first
            */
            if(!secondBack) {
            Snackbar.make(mView, R.string.app_exit_toast,
                    Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
            secondBack=!secondBack;
            startTime = System.currentTimeMillis();
            } else if( System.currentTimeMillis()-startTime < 2500)
            super.onBackPressed();
            else {
            secondBack=!secondBack;
            onBackPressed();
           }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // update highlighted item in the navigation menu
        item.setChecked(true);
        id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;

        switch (id) {
            case R.id.nav_display_animations:
                fragmentClass = DisplayAnimationsActivity.class;
                break;
            case R.id.nav_statusbar:
                fragmentClass = StatusBarFragment.class;
                break;
            case R.id.nav_notif_drawer:
                fragmentClass = NotificationsFragment.class;
                break;
            case R.id.nav_recents:
                fragmentClass = RecentsPanelFragment.class;
                break;
            case R.id.nav_lockscreen:
                fragmentClass = LockscreenFragment.class;
                break;
            case R.id.nav_advanced:
                fragmentClass = MultiShitFragment.class;
                break;
            case R.id.nav_halo:
                fragmentClass = HaloFragment.class;
                break;
            case R.id.nav_transparency_porn:
                fragmentClass = TransparencyPornFragment.class;
                break;
            case R.id.nav_more:
                fragmentClass = VariousShitFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = AboutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RelativeLayout contentMain = (RelativeLayout) findViewById(R.id.content_main);
        contentMain.removeAllViewsInLayout();
        contentMain.invalidate();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle(item.getTitle());
        mDrawer.closeDrawers();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, id);
    }
}
