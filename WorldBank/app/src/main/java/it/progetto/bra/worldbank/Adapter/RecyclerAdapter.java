package it.progetto.bra.worldbank.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.progetto.bra.worldbank.Callback;
import it.progetto.bra.worldbank.Entity.MyObject;
import it.progetto.bra.worldbank.R;
import com.jadebyte.devmike.persistantrecycleradapter.library.PersistentRecyclerAdapter;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeViewHolder;

import java.util.ArrayList;
import java.util.List;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class RecyclerAdapter extends PersistentRecyclerAdapter<MyObject, RecyclerAdapter.ViewHolder> {

    /*
    * Persistenza + incapsulamento recycler tramite MyObject
    * */

    private List<MyObject> mlist;
    private Context inContext;
    private Callback mCallback;

    private Dialog mDialog;

    public RecyclerAdapter(Context context, List<MyObject> list) {
        super(list);
        inContext = context;
        mlist = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);

        mDialog = new Dialog((Context) mCallback);
        mDialog.setContentView(R.layout.information_dialog);

        viewHolder.Name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TextView title = mDialog.findViewById(R.id.titleText);
                TextView text = mDialog.findViewById(R.id.objectText);
                ImageView img = mDialog.findViewById(R.id.img);

                if (mlist.get(viewHolder.getAdapterPosition()).getCountry() != null) {
                    title.setText(mlist.get(viewHolder.getAdapterPosition()).getCountry().getName());
                    text.setText(mlist.get(viewHolder.getAdapterPosition()).getCountry().getCapitalCity());
                    img.setBackgroundResource(R.drawable.ic_country);
                    mDialog.show();
                } else if (mlist.get(viewHolder.getAdapterPosition()).getTopic() != null) {
                    title.setText(mlist.get(viewHolder.getAdapterPosition()).getTopic().getValue());
                    text.setText(mlist.get(viewHolder.getAdapterPosition()).getTopic().getSourceNote());
                    img.setBackgroundResource(R.drawable.ic_topic);
                    mDialog.show();
                } else if (mlist.get(viewHolder.getAdapterPosition()).getIndicator() != null) {
                    title.setText(mlist.get(viewHolder.getAdapterPosition()).getIndicator().getName());
                    text.setText(mlist.get(viewHolder.getAdapterPosition()).getIndicator().getSourceNote());
                    img.setBackgroundResource(R.drawable.ic_indicator);
                    mDialog.show();
                }
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    /*
    * Prendo la callback per richiamare la Main Activity dalla onBind
    * */

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mCallback = (Callback) inContext;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position, MyObject obj) {

        if (mlist.get(position).getCountry() != null) {
            viewHolder.Name.setText(mlist.get(position).getCountry().getName());
        } else if (mlist.get(position).getTopic() != null) {
            viewHolder.Name.setText(mlist.get(position).getTopic().getValue());
        } else if (mlist.get(position).getIndicator() != null) {
            viewHolder.Name.setText(mlist.get(position).getIndicator().getName());
        }

        viewHolder.Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlist.get(position).getCountry() != null) {
                    mlist.get(position).getCountry().setClicked(true);
                    mCallback.notifyObject(mlist.get(position).getCountry());
                } else if (mlist.get(position).getTopic() != null) {
                    mlist.get(position).getTopic().setClicked(true);
                    mCallback.notifyObject(mlist.get(position).getTopic());
                } else if (mlist.get(position).getIndicator() != null) {
                    mlist.get(position).getIndicator().setClicked(true);
                    mCallback.notifyObject(mlist.get(position).getIndicator());
                }
            }
        });
    }

    public class ViewHolder extends SwipeViewHolder {

        TextView Name;
        //SWIPE UNUSED --> BACK TO VIEWHOLDER!

        public ViewHolder(View itemView) {
            super((ViewGroup) itemView, R.layout.item_view,SwipeViewHolder.NO_ID,R.layout.swipe_menu);
            Name = findViewById(R.id.nameofentry);
        }
    }


    /*
    * Cerco l'input inserito dall'utente!
    * */

    public void setFilter(ArrayList<MyObject> newList) {
        mlist = new ArrayList<>();
        mlist.addAll(newList);
        notifyDataSetChanged(); // refresh
    }
}
