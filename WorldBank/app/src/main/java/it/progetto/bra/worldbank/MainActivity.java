package it.progetto.bra.worldbank;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import it.progetto.bra.worldbank.Adapter.PagerAdapter;
import it.progetto.bra.worldbank.Entity.Country;
import it.progetto.bra.worldbank.Entity.Indicator;
import it.progetto.bra.worldbank.Entity.Topic;
import it.progetto.bra.worldbank.Fragment.DynamicFragment;
import it.progetto.bra.worldbank.Fragment.SimpleFragment;
import com.facebook.stetho.Stetho;

import java.io.File;
import java.util.Objects;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class MainActivity extends AppCompatActivity implements Callback {

    private final static String TAG = "MAIN_ACTIVITY";

    //Variabile che regola il tasto exit
    public static int CLICK = 0;

    //Variabili private di instanza delle entity ritornate dopo un tap
    private Country finalCountryObject;
    private Topic finalTopicObject;
    private Indicator finalIndicatorObject;

    //Variabile di regolazione icone del tablayout
    public static int INTERNAL_FIRST_CHOICE = 0;

    //Instance fragment (dinamico e semplice)
    private DynamicFragment df = new DynamicFragment();
    private SimpleFragment simpleFragment = new SimpleFragment();

    //Search bar interna alla toolbar
    private SearchView searchView;

    //Variabili di gestione del main XML contenente TabLayout & ViewPager
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter viewPageAdapter;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ignoro la file URI expose
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Mostro DB in chrome
        Log.d(TAG,"Showing db in chrome!");
        Stetho.initializeWithDefaults(this);

        //Controllo lo stato della connessione
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }

        //Setting dinamico della toolbar (in base alla dimensione degli elementi gestisco il logo della WorldBank)
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.world_bank_logo);
            Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();
            Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 480, 110, true));
            toolbar.setLogo(newDrawable);
        }

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        viewPageAdapter = new PagerAdapter(getSupportFragmentManager(), this);

        //SimpleFragment -> Prima scelta (Country/Topic)
        viewPageAdapter.addFragment(simpleFragment, getString(R.string.browse_by));
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
    }


    /*
    * Metodo di interfaccia necessario alla comunicazione di una nuova istanza di oggetto
    * da parte del dynamic fragment.
    * (Quando l'utente seleziona uno stato, un argomento, un indicatore dal recycler)
    * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyObject(Object obj) {

        if (!searchView.isIconified()) {
            hideKeyboard(MainActivity.this);
        }

        if (obj instanceof Country) {

            // Caso di uscita dalla MainActivity -> END SEARCH
            if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getText()).equals(getString(R.string.browse_by_topic)) &&
                    Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.country_label))) {
                finalCountryObject = (Country) obj;
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra("onlineornot",1);
                intent.putExtra("country",finalCountryObject);
                intent.putExtra("topic",finalTopicObject);
                intent.putExtra("indicator",finalIndicatorObject);
                startActivityForResult(intent, 1);
            } else {
                // Salvo istanza di country object
                finalCountryObject = (Country) obj;
                viewPageAdapter.removeFragment(df,getString(R.string.country_label));
                viewPageAdapter.addFragment(df,getString(R.string.topic_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_topic);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                df.goWithTopic();
            }

        } else if (obj instanceof Topic) {

            //Salvo istanza di topic object
            finalTopicObject = (Topic) obj;
            Bundle bundle = new Bundle(1);
            //Gestisto ID del topic per filtrare gli indicatori relativi a quanto scelto dall'utente
            bundle.putInt("id",finalTopicObject.getId());
            viewPageAdapter.removeFragment(df,getString(R.string.topic_label));
            viewPageAdapter.addFragment(df,getString(R.string.indicator_label));
            viewPager.setAdapter(viewPageAdapter);
            tabLayout.setupWithViewPager(viewPager);
            //Gestione icone del TabLayout al momento della scelta
            if (INTERNAL_FIRST_CHOICE == 0) {
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
            } else {
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_topic));
            }
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_indicator);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            df.setArguments(bundle);
            //Procedo con gli indicatori in ogni caso di scelta.
            df.goWithIndicator();

        } else if (obj instanceof Indicator) {

            finalIndicatorObject = (Indicator) obj;
            //Caso TOPIC->INDICATOR->COUNTRY
            if (finalCountryObject == null) {
                Bundle bdl = new Bundle(1);
                viewPageAdapter.removeFragment(df,getString(R.string.indicator_label));
                viewPageAdapter.addFragment(df,getString(R.string.country_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_topic));
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_country);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                bdl.putInt("choice",1);
                df.setArguments(bdl);
                df.goWithCountry();
            //CASO COUNTRY->TOPIC->INDICATOR
            } else {
                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra("onlineornot",1);
                intent.putExtra("country",finalCountryObject);
                intent.putExtra("topic",finalTopicObject);
                intent.putExtra("indicator",finalIndicatorObject);
                startActivity(intent);
            }
        }
    }


    /*
    * Metodo di interfaccia utilizzato per comunicare LA PRIMA scelta fatta dall'utente.
    * (Instanziata dal SimpleFragment)
    * */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void notifyFirstChoice(int choice) {

        //Controllo di errori fatto in base alla dimesione del PagerAdapter
        INTERNAL_FIRST_CHOICE = choice;
        if (viewPageAdapter.getCount() == 2) {

            //Clear degli oggetti necessario alla corretto utilizzo della ricerca
            finalCountryObject = null;
            finalTopicObject = null;
            finalIndicatorObject = null;

            //Controlli di "comeback" da parte dell'utente per evitare casi d'errore nella scelta
            if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.topic_label)) && choice == 0) {
                viewPageAdapter.removeFragment(df,getString(R.string.topic_label));
                viewPageAdapter.addFragment(df,getString(R.string.country_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                df.goWithCountry();
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_country);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.country_label)) && choice == 1) {
                viewPageAdapter.removeFragment(df,getString(R.string.country_label));
                viewPageAdapter.addFragment(df,getString(R.string.topic_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                df.goWithTopic();
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_topic);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_topic));
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.indicator_label)) && choice == 0) {
                viewPageAdapter.removeFragment(df,getString(R.string.indicator_label));
                viewPageAdapter.addFragment(df,getString(R.string.country_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                df.goWithCountry();
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_topic);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.indicator_label)) && choice == 1) {
                viewPageAdapter.removeFragment(df,getString(R.string.indicator_label));
                viewPageAdapter.addFragment(df,getString(R.string.topic_label));
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                df.goWithTopic();
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_topic);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_topic));
                Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else if (Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.country_label)) && choice == 0) {
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else if (Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getText()).equals(getString(R.string.topic_label)) && choice == 1)) {
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.browse_by_topic);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            }

        //Caso di adapter a dimenione pari a 1
        } else {

            if (choice == 0) {
                Bundle bdl = new Bundle(1);
                bdl.putInt("choice",0);
                viewPageAdapter.addFragment(df,getString(R.string.country_label));
                df.setArguments(bdl);
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_country);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_country));
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            } else {
                Bundle bdl = new Bundle(1);
                bdl.putInt("choice",1);
                viewPageAdapter.addFragment(df,getString(R.string.topic_label));
                df.setArguments(bdl);
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.browse_by_topic));
                Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_topic);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            }
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_find);
        }
    }

    //Gestione soft input
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //Variazione sul back button
    @Override
    public void onBackPressed() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }


    // Metodo di override per le actionBar (TOP & BOTTOM)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TOOLBAR (TOP)
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.filteredSearch);

        // SEARCH  BUTTON legato al recycler tramite metodo filtered_search
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if ((viewPageAdapter.getCount() == 1)) {
                    searchView.setIconified(true);
                    hideKeyboard(MainActivity.this);
                    Toast.makeText(MainActivity.this, R.string.make_a_choice,Toast.LENGTH_SHORT).show();
                } else {
                    if (finalCountryObject == null && finalTopicObject == null && finalIndicatorObject == null
                            && !Objects.requireNonNull(tabLayout.getTabAt(1)).isSelected()) {
                        Toast.makeText(MainActivity.this, R.string.make_a_choice,Toast.LENGTH_SHORT).show();
                        hideKeyboard(MainActivity.this);
                    } else {
                        if (Objects.requireNonNull(tabLayout.getTabAt(1)).getText() == getString(R.string.country_label)) {
                            df.filteredSearch(newText, 0);
                        } else if (Objects.requireNonNull(tabLayout.getTabAt(1)).getText() == getString(R.string.topic_label)) {
                            df.filteredSearch(newText, 1);
                        } else if (Objects.requireNonNull(tabLayout.getTabAt(1)).getText() == getString(R.string.indicator_label)) {
                            df.filteredSearch(newText, 2);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        // BOTTOM BAR
        android.support.v7.widget.ActionMenuView bottombar = findViewById(R.id.bottombar);
        Menu bottonMenu = bottombar.getMenu();
        getMenuInflater().inflate(R.menu.menu_bottom, bottonMenu);

        for (int i = 0; i < bottonMenu.size(); i++) {
            bottonMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.saved:

                            if (ChartActivity.verifyStoragePermissions(MainActivity.this)) {
                                if (new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()).listFiles().length == 0) {
                                    Toast.makeText(MainActivity.this, R.string.no_chart, Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent1 = new Intent(MainActivity.this, SavedChartActivity.class);
                                    startActivity(intent1);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, R.string.click_again,Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case R.id.world:

                            Intent intent = new Intent(MainActivity.this,SavedSearchActivity.class);
                            startActivity(intent);
                            return true;

                        case R.id.closeapp:

                            CLICK++;
                            if (CLICK == 1) {
                                Toast.makeText(MainActivity.this, R.string.exit,Toast.LENGTH_SHORT).show();
                            } else if (CLICK == 2) {
                                CLICK = 0;
                                finish();
                                return true;
                            }
                        default:
                            return onOptionsItemSelected(item);
                    }
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.credits:

                Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.setContentView(R.layout.information_dialog);
                TextView title = mDialog.findViewById(R.id.titleText);
                TextView text = mDialog.findViewById(R.id.objectText);
                ImageView img = mDialog.findViewById(R.id.img);

                Drawable drawable = ContextCompat.getDrawable(this,R.drawable.tv_logo_new);
                Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();
                Drawable newDrawable = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap, 100, 320, true));

                title.setText(R.string.titolo);
                text.setText(R.string.credits_text);
                img.setBackground(newDrawable);
                mDialog.show();
                return true;

            case R.id.cache:

                MainActivity.this.deleteDatabase("WORLD_BANK_OPEN_DB");
                Toast.makeText(MainActivity.this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
