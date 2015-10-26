package com.android.liujian.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.liujian.sunshine.utils.Utility;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String DETAIL_FRAGMENT_TAG = "df_tag";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        /*if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .add(R.id.forecast_container, new ForecastFragment())
                    .commit();
        }*/

        if(findViewById(R.id.weather_detail_container) != null){
            /** The screen's small width is bigger than 600dp, so the main layout is master-detail pattern*/
            mTwoPane = true;

            if(savedInstanceState == null){
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }

        }else{
            mTwoPane = false;
        }

        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!mTwoPane);


/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferenceLocation(this);

        if(location != null && ! location.equals(mLocation)){
            FragmentManager fm = getSupportFragmentManager();
            ForecastFragment ff = (ForecastFragment)fm.findFragmentById(R.id.fragment_forecast);
            if(ff != null){
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment)fm.findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }else if(id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void openPreferredLocationInMap(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else{
            Log.d(LOG_TAG, "Couldn't call location " + location + ", not app..");
        }
    }

    @Override
    public void onItemSelected(Uri weatherUri) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, weatherUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.weather_detail_container, df, DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{

            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(weatherUri);
            startActivity(intent);

        }
    }
}
