package com.ehret.mixit.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.ui.utils.UIUtils;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuMapFragment extends Fragment{
    protected TextView mapText;
    protected TextView mapDescView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Mise a jour du titre
        if(mapText == null){
            mapText = (TextView) getActivity().findViewById(R.id.mapTextView);
            mapText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                    mapIntent.setData(Uri.parse("geo:45.735362,4.878445?z=17&q=SUPINFO,+16+Rue+Jean+Desparmet,+Lyon"));
                    UIUtils.filterIntent(getActivity(), "maps", mapIntent);
                    startActivity(Intent.createChooser(mapIntent, "Venir à Mix-IT"));
                }
            });
        }

    }


}
