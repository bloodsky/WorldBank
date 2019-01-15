package it.progetto.bra.worldbank;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class SavedChartActivity extends AppCompatActivity {

    private static final String TAG = "SavedChartActivity";
    private Slider slider;
    final List<Slide> slideList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_chart);

        android.support.v7.widget.Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.world_bank_logo);
            Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();
            Drawable newDrawable = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap, 480, 110, true));
            toolbar.setLogo(newDrawable);
        }

        // instanza di slider
        slider = findViewById(R.id.slider);

        //select list directory

        sliderInit();
        slider.addSlides(slideList);

        slider.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                if (ChartActivity.verifyStoragePermissions(SavedChartActivity.this)) {
                    final CharSequence[] items = {getString(R.string.share_saved_local_chart),getString(R.string.show_in_gallery),getString(R.string.delete_this_chart)};
                    final ArrayList<Integer> seletedItems = new ArrayList<>();

                    AlertDialog dialog = new AlertDialog.Builder(SavedChartActivity.this)
                            .setTitle(R.string.alert_title)
                            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                    if (isChecked) {
                                        seletedItems.add(indexSelected);
                                    } else if (seletedItems.contains(indexSelected)) {
                                        seletedItems.remove(Integer.valueOf(indexSelected));
                                    }
                                }
                            }).setPositiveButton(getString(R.string.ok_alert), new DialogInterface.OnClickListener() {
                                File file = new File(slideList.get(position).getImageUrl());
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    if (seletedItems.contains(0)) { // SHARE
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
                                        startActivity(Intent.createChooser(share, getString(R.string.share_title)));
                                    } else if (seletedItems.contains(1)) { // SHOW IN GALLERY
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse("file://"+file),"image/*");
                                        startActivity(intent);
                                    } else if (seletedItems.contains(2)) { // DELETE
                                        boolean deleted = file.delete();
                                        if (deleted) {
                                            slider.removeAllViews();
                                            sliderInit();
                                        } else {
                                            Log.d(TAG,"Can't delete this file!");
                                        }
                                    }
                                }
                            }).setNegativeButton(getString(R.string.cancel_alert), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //Toast.makeText(SavedChartActivity.this,"",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,"nothing choosed!");
                                }
                            }).create();
                    dialog.show();
                } else {
                    Log.d(TAG,"No permission!!");
                }
            }
        });
    }

    public void sliderInit() {
        slideList.clear();
        File[] files = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()).listFiles();
        int i = 0;
        for (File file : files) {
            if (file.isFile()) {
                slideList.add(new Slide(i, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + file.getName(), getResources().getDimensionPixelSize(R.dimen.cardview_default_elevation)));
                i++;
            }
        }
        slider.addSlides(slideList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chart_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                finish();
                return true;
            case R.id.informationboutchart:
                Toast.makeText(SavedChartActivity.this, getString(R.string.no_info),Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
