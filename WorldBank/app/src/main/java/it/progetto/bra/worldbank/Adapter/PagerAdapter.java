package it.progetto.bra.worldbank.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.progetto.bra.worldbank.MainActivity;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class PagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = "PAGER_ADAPTER";
    private List<Fragment> listFrag = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private Context mContext;


    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return listFrag.get(position);
    }

    @Override
    public int getCount() {
        return listTitle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }


    @Override
    public float getPageWidth(int position) {

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
        if (diagonalInches>=6.5){
            Log.d(TAG,"diagonalInches: "+diagonalInches);
            return(1f/2);
        }else{
            Log.d(TAG,"diagonalInches: "+diagonalInches);
            return(1f);
        }
    }

    public void addFragment(Fragment fragment, String title) {
        listFrag.add(fragment);
        listTitle.add(title);
    }

    public void removeFragment(Fragment fragment, String title) {
        listFrag.remove(fragment);
        listTitle.remove(title);
    }
}
