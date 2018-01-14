package com.android.netisade.pit;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        PitViewGroup PVG = findViewById(R.id.pit_viewgroup);
        PVG.init(context);//Position the number axis and place 5 new points in random places.
    }

    /***
     * This function is used in case of screen rotation,
     * Do not init the viewGroup again, but adjust the points to the new orientation.
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Context context = getApplicationContext();
        PitViewGroup PVG = findViewById(R.id.pit_viewgroup);
        PVG.onRotate(context);
    }
}
