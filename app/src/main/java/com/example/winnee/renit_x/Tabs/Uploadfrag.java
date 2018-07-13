package com.example.winnee.renit_x.Tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.winnee.renit_x.UI.Postrent;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.admin.Ads;

/**
 * A simple {@link Fragment} subclass.
 */
public class Uploadfrag extends Fragment {


    private  View rootview ;
    public static Uploadfrag newInstance() {

        Bundle args = new Bundle();

        Uploadfrag fragment = new Uploadfrag();
        fragment.setArguments(args);
        return fragment;
    }
    public Uploadfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_uploadfrag, container, false);

        Button upload = (Button)  rootview.findViewById(R.id.uploadarent_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               startActivity(new Intent(getActivity(), Postrent.class));
                startActivity(new Intent(getActivity(), Ads.class));
            }
        });

        return rootview;
    }

}
