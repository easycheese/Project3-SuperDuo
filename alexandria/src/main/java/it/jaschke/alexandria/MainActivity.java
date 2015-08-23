package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.fragment.About;
import it.jaschke.alexandria.fragment.AddBook;
import it.jaschke.alexandria.fragment.BookDetail;
import it.jaschke.alexandria.fragment.ListOfBooks;


public class MainActivity extends ActionBarActivity implements Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
//    private NavigationDrawerFragment navigationDrawerFragment;
    @Bind(R.id.toolbar) Toolbar toolbar;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
//    private CharSequence title;
//    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private static int navPosition=99;
    private static final String navPosition_saved = "navPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        IS_TABLET = isTablet();
//        if(IS_TABLET){ //TODO
//            setContentView(R.layout.activity_main_tablet);
//        }else {
//            setContentView(R.layout.activity_main);
//        }
        if (savedInstanceState != null) {
            navPosition = savedInstanceState.getInt(navPosition_saved);
        }

        messageReciever = new messageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);
        setupNav();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(navPosition_saved, navPosition);
        super.onSaveInstanceState(outState);
    }

    private void setupNav() {
        //Removed navigation drawer (not to guidelines)
//        navigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        title = getTitle();
//
//        // Set up the drawer.
//        navigationDrawerFragment.setUp(R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

//        toolbar.inflateMenu(R.menu.main);
        setSupportActionBar(toolbar);

        PrimaryDrawerItem item0 = new PrimaryDrawerItem().withName(R.string.books).withIdentifier(0);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(R.string.scan).withIdentifier(1);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(R.string.about).withIdentifier(2);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item0,
                        item1,
                        item2
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        onNavSelection(position);
                        return false;
                    }
                })
                .build();

        if (navPosition == 99) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            navPosition = Integer.parseInt(prefs.getString("pref_startFragment","0"));
        }

        result.setSelection(navPosition,true);


    }
    public void onNavSelection(int position) {
        navPosition = position;
        Fragment nextFragment;
        int title;
        switch (position){
            default:
            case 0:
                title = R.string.books;
                nextFragment = new ListOfBooks();
                break;
            case 1:
                title = R.string.scan;
                nextFragment = new AddBook();
                break;
            case 2:
                title = R.string.about;
                nextFragment = new About();
                break;

        }
        toolbar.setTitle(getString(title));
        addFragment(nextFragment);


    }
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//
//
//        Fragment nextFragment;
//
//        switch (position){
//            default:
//            case 0:
//                nextFragment = new ListOfBooks();
//                break;
//            case 1:
//                nextFragment = new AddBook();
//                break;
//            case 2:
//                nextFragment = new About();
//                break;
//
//        }
//
//        addFragment(nextFragment);
//    }
    private void addFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
//                .addToBackStack((String) title)
                .commit();
    }

//    public void setTitle(int titleId) {
//        title = getString(titleId);
//    }

//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(title);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        if (!navigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override // Callback from bookList
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(id, fragment)
//                .addToBackStack("Book Detail")
//                .commit();
        Intent i = new Intent(MainActivity.this, BookDetailsActivity.class);
        i.putExtras(args);
        startActivity(i);
    }

    private class messageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

//    Removed due to overriding user expected behavior
//    public void goBack(View view){
//        getSupportFragmentManager().popBackStack();
//    }


//    @Override
//    public void onBackPressed() {
//        if(getSupportFragmentManager().getBackStackEntryCount()<2){
//            finish();
//        }
//        super.onBackPressed();
//    }


}