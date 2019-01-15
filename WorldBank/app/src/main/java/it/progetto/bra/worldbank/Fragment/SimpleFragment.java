package it.progetto.bra.worldbank.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import it.progetto.bra.worldbank.Callback;
import it.progetto.bra.worldbank.R;

/*
    Developed by: Bovi Andrea, Martelletti Ambra, Pavia Roberto
    ¯\_(ツ)_/¯ It works on our machines! :D
*/

public class SimpleFragment extends android.support.v4.app.Fragment {

    private View v;
    private Callback mCallback;
    private ImageButton chooseCountry, chooseTopic;

    public SimpleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment, container, false);
        chooseCountry = v.findViewById(R.id.choose_by_country);
        chooseTopic = v.findViewById(R.id.choose_by_topic);

        chooseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.notifyFirstChoice(0);

            }
        });

        chooseTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.notifyFirstChoice(1);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+getString(R.string.implement_interface));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
