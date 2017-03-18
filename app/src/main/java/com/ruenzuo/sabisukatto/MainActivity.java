package com.ruenzuo.sabisukatto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.ruenzuo.sabisukatto.media.MediaFragment;
import com.ruenzuo.sabisukatto.settings.SettingsFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

import static com.ruenzuo.sabisukatto.TwitterCredentials.TWITTER_KEY;
import static com.ruenzuo.sabisukatto.TwitterCredentials.TWITTER_SECRET;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, getFragment(navigation.getMenu().getItem(0).getItemId())).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, getFragment(item.getItemId())).commit();
        return true;
    }

    private Fragment getFragment(int menuId) {
        switch (menuId) {
            case R.id.navigation_media: return MediaFragment.newInstance();
            case R.id.navigation_settings: return SettingsFragment.newInstance();
            default: return null;
        }
    }

}
