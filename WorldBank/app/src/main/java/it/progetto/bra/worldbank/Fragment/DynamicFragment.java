package it.progetto.bra.worldbank.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import it.progetto.bra.worldbank.Adapter.RecyclerAdapter;
import it.progetto.bra.worldbank.Callback;
import it.progetto.bra.worldbank.Entity.Country;
import it.progetto.bra.worldbank.Entity.Indicator;
import it.progetto.bra.worldbank.Entity.MyObject;
import it.progetto.bra.worldbank.Entity.Topic;
import it.progetto.bra.worldbank.Pattern.MySingleton;
import it.progetto.bra.worldbank.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class DynamicFragment extends android.support.v4.app.Fragment {

    private ProgressBar pbar;
    private RecyclerView recyclerView;

    private List<MyObject> listCountry = new ArrayList<>();
    private List<MyObject> listTopic = new ArrayList<>();
    private List<MyObject> listIndicator = new ArrayList<>();

    private RecyclerAdapter adapter;

    public DynamicFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Callback mCallback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+getString(R.string.implement_interface));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dynamic_fragment, container, false);
        recyclerView = v.findViewById(R.id.RecyclerView);
        pbar = v.findViewById(R.id.progress_bar);

        if (Objects.requireNonNull(getArguments()).getInt("choice") == 0) {
            initRecycler(listCountry);
            if (listCountry.size() == 0) {
                onlineCountryJob();
            }
        } else {
            initRecycler(listTopic);
            if (listTopic.size() == 0) {
                onlineTopicJob();
            }
        }
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void goWithTopic() {
        pbar.setVisibility(View.VISIBLE);
        initRecycler(listTopic);
        onlineTopicJob();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void goWithIndicator() {
        pbar.setVisibility(View.VISIBLE);
        initRecycler(listIndicator);
        onlineIndicatorJob();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void goWithCountry() {
        pbar.setVisibility(View.VISIBLE);
        initRecycler(listCountry);
        onlineCountryJob();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initRecycler(List<MyObject> list) {
        adapter = new RecyclerAdapter(getActivity(),list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void onlineCountryJob() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), getString(R.string.no_connect),Toast.LENGTH_SHORT).show();
        } else {
            String URL = "http://api.worldbank.org/v2/countries?per_page=304&format=json";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listCountry.clear();
                        try {
                            JSONArray array = (JSONArray) response.get(1);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject country = array.getJSONObject(i);
                                final Gson gson = new Gson();
                                new Country();
                                Country c;
                                c = gson.fromJson(country.toString(), Country.class);
                                MyObject obj = new MyObject(c);
                                listCountry.add(obj);
                            }
                            adapter.notifyDataSetChanged();
                            pbar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        }
    }

    private void onlineTopicJob() {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), getString(R.string.no_connect),Toast.LENGTH_SHORT).show();
        } else {
            String URL = "http://api.worldbank.org/v2/topics?format=json";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listTopic.clear();
                        try {
                            JSONArray array = (JSONArray) response.get(1);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject topics = array.getJSONObject(i);
                                final Gson gson = new Gson();
                                new Topic();
                                Topic t = gson.fromJson(topics.toString(), Topic.class);
                                MyObject obj = new MyObject(t);
                                listTopic.add(obj);
                            }
                            adapter.notifyDataSetChanged();
                            pbar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
            }
    }

    private void onlineIndicatorJob() {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), getString(R.string.no_connect),Toast.LENGTH_SHORT).show();
        } else {
            String URL = "http://api.worldbank.org/v2/topics/" + Objects.requireNonNull(getArguments()).getInt("id") + "/indicators?per_page=4000&format=json";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    listIndicator.clear();
                    try {
                        JSONArray array = (JSONArray) response.get(1);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject indicators = array.getJSONObject(i);
                            final Gson gson = new Gson();
                            new Indicator();
                            Indicator I = gson.fromJson(indicators.toString(), Indicator.class);
                            MyObject obj = new MyObject(I);
                            listIndicator.add(obj);
                        }
                        adapter.notifyDataSetChanged();
                        pbar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
        }
    }

    /*
    * Callback method --> ricerca stringa utente nel recycler // in base al tipo di oggetto.
    * */
    public void filteredSearch(String newText, int choice) {

        newText = newText.toLowerCase();
        ArrayList<MyObject> newList = new ArrayList<>();

        switch (choice) {

            case 0:

                for (MyObject obj : listCountry) {
                    String name = obj.getCountry().getName().toLowerCase();
                    if (name.contains(newText)) {
                        newList.add(obj);
                    }
                }
                adapter.setFilter(newList);
                pbar.setVisibility(View.INVISIBLE);
                break;

            case 1:

                for (MyObject obj : listTopic) {
                    String name = obj.getTopic().getValue().toLowerCase();
                    if (name.contains(newText)) {
                        newList.add(obj);
                    }
                }
                adapter.setFilter(newList);
                pbar.setVisibility(View.INVISIBLE);
                break;

            case 2:

                for (MyObject obj : listIndicator) {
                    String name = obj.getIndicator().getName().toLowerCase();
                    if (name.contains(newText)) {
                        newList.add(obj);
                    }
                }
                adapter.setFilter(newList);
                pbar.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
