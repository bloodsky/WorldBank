package it.progetto.bra.worldbank.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.progetto.bra.worldbank.Callback;
import it.progetto.bra.worldbank.Entity.Search;
import it.progetto.bra.worldbank.R;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuLayout;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeViewHolder;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeSwitchListener;

import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context inContext;
    private Callback mCallback;
    private List<Search> mList;
    private Dialog mDialog;


    public SearchRecyclerAdapter(Context context, List<Search> mList) {
        this.inContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mCallback = (Callback) inContext;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        ((ViewHolder) holder).country.setText(mList.get(position).getCountry());
        ((ViewHolder) holder).topic.setText(mList.get(position).getTopic());
        ((ViewHolder) holder).indicator.setText(mList.get(position).getIndicator().substring(0,10)+"...");

        ((ViewHolder) holder).setSwipeListener(new SwipeSwitchListener() {
            @Override
            public void beginMenuClosed(SwipeMenuLayout swipeMenuLayout) {

            }

            @Override
            public void beginMenuOpened(SwipeMenuLayout swipeMenuLayout) {

            }

            @Override
            public void endMenuClosed(SwipeMenuLayout swipeMenuLayout) {

            }

            @Override
            public void endMenuOpened(SwipeMenuLayout swipeMenuLayout) {
                mCallback.notifyObject(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends SwipeViewHolder {

        TextView country, topic, indicator;
        public View view;

        public ViewHolder(View itemView) {
            super((ViewGroup) itemView, R.layout.search_view,SwipeViewHolder.NO_ID,R.layout.swipe_menu);
            country = findViewById(R.id.valueofcountry);
            topic = findViewById(R.id.valueoftopic);
            indicator = findViewById(R.id.valueofindicator);
        }

    }

}
