package com.ehret.mixit.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.ui.activity.PlanningJ1Activity;
import com.ehret.mixit.ui.activity.PlanningJ2Activity;
import com.ehret.mixit.ui.utils.TableRowBuilder;
import com.ehret.mixit.ui.utils.TextViewBuilder;
import com.ehret.mixit.ui.utils.UIUtils;

/**
 * Fragment utilise sur la page daccueil pour afficher les talks
 */
public class MenuPlanningFragment extends Fragment{

    protected TableLayout menuTableLayout;
    protected TextView titleMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu3, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dessinerMenu();
    }


    /**
     * Affiche les données à l'écran
     */
    protected void dessinerMenu(){
        Context context = getActivity().getBaseContext();

        //Mise a jour du titre
        if(titleMenu == null){
            titleMenu = (TextView) getActivity().findViewById(R.id.menuFragmentTitle3);
        }
        titleMenu.setText(context.getText(R.string.description_planning));

        //deux tableaux juxtaposer
        //Un d'une colonne pour gérer l'heure
        if(menuTableLayout==null){
            menuTableLayout = (TableLayout) getActivity().findViewById( R.id.menuFragmentTableLayout3);
        }
        menuTableLayout.removeAllViews();

        createMenu(R.color.grey,R.color.grey,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_avril), false,true, null,1);
        createMenu(R.color.grey,android.R.color.white,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_24), false,false, null,1);

        createMenu(R.color.grey, R.color.yellow3,
                "\n" + context.getString(R.string.calendrier_jeudi) + "\n",
                context.getString(R.string.calendrier_25) +"\n", false,false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startActivity(PlanningJ1Activity.class, getActivity());
            }
        }, 3);
        createMenu(R.color.grey, R.color.yellow3,
                "\n" + context.getString(R.string.calendrier_vendredi) + "\n",
                context.getString(R.string.calendrier_26) +"\n", false,false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.startActivity(PlanningJ2Activity.class, getActivity());
            }
        }, 3);
        createMenu(R.color.grey,android.R.color.white,
                context.getString(R.string.blank),
                context.getString(R.string.calendrier_27), false,false, null,1);

    }

    protected void createMenu(int color1,int color2, String nom1,String nom2,
                              boolean dernierligne,boolean upper, View.OnClickListener listener, int nbLigne) {
        TableRow tableRow = new TableRowBuilder().buildTableRow(getActivity())
                .addNbColonne(2)
                .addBackground(getResources().getColor(R.color.grey)).getView();

        TextView colorView = new TextViewBuilder()
                .buildTextView(getActivity())
                .addText(nom1)
                .addPadding(4, 0, 4)
                .addBackground(getResources().getColor(color1))
                .addNbLines(nbLigne)
                .addBold(true)
                .addNbMaxLines(nbLigne)
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();
        tableRow.addView(colorView);

        TextView textView = new TextViewBuilder()
                .buildTextView(getActivity())
                .addAlignement(Gravity.CENTER)
                .addText(nom2)
                .addBorders(true, true, dernierligne, true)
                .addPadding(4, 0, 4)
                .addNbLines(nbLigne)
                .addNbMaxLines(nbLigne)
                .addUpperCase()
                .addBold(true)
                .addBackground(getResources().getColor(color2))
                .addTextColor(getResources().getColor(android.R.color.black))
                .getView();
        textView.setAllCaps(upper);
        textView.setOnClickListener(listener);
        tableRow.addView(textView);



        menuTableLayout.addView(tableRow, TableRowBuilder.getLayoutParams());
    }

}