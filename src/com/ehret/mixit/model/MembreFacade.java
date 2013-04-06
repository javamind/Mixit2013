package com.ehret.mixit.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.ui.utils.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Le but de ce fichier est de s'interfacer avec le fichier Json gerant les
 * différents membres.
 */
public class MembreFacade {
    /**
     * Factory Json
     */
    private JsonFactory jsonFactory;
    /**
     * Objetc mapper permettant de faire le binding entre le JSON et les objets
     */
    private ObjectMapper objectMapper;
    /**
     * Instance du singleton
     */
    private static MembreFacade membreFacade;

    private final static String TAG = "MembreFacade";

    private static Map<Long, Membre> membres= new HashMap<Long, Membre>();;
    private static Map<Long, Membre> speaker= new HashMap<Long, Membre>();;
    private static Map<Long, Membre> staff= new HashMap<Long, Membre>();;
    private static Map<Long, Membre> sponsors= new HashMap<Long, Membre>();;

    /**
     * Permet de vider le cache de données
     */
    public void viderCache(){
        membres.clear();
        speaker.clear();
        staff.clear();
        sponsors.clear();
    }
    /**
     * Constructeur prive car singleton
     */
    private MembreFacade() {
        //Creation de nos objets
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retourne le singleton
     * @return
     */
    public static  MembreFacade getInstance(){
        if(membreFacade==null){
            membreFacade = new MembreFacade();
        }
        return membreFacade;
    }

    /**
     *
     * @param context
     * @param typeAppel
     * @return
     */
    public List<Membre> getMembres(Context context, String typeAppel){
        if(TypeFile.members.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, membres);
            return Ordering.from(getComparatorByName()).sortedCopy(Lists.newArrayList(membres.values()));
        }
        else if(TypeFile.staff.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, staff);
            return Ordering.from(getComparatorByName()).sortedCopy(Lists.newArrayList(staff.values()));
        }
        else if(TypeFile.sponsor.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, sponsors);
            return Ordering.from(getComparatorByLevel()).reverse().compound(getComparatorByName()).sortedCopy(Lists.newArrayList(sponsors.values()));
        }
        else if(TypeFile.speaker.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, speaker);
            return Ordering.from(getComparatorByName()).sortedCopy(Lists.newArrayList(speaker.values()));
        }
        return null;
    }

    /**
     * Comparaison par nom
     * @return
     */
    private Comparator<Membre> getComparatorByLevel(){
        return new Comparator<Membre>() {
            @Override
            public int compare(Membre m1, Membre m2) {
                return m1.getLevel().compareTo(m2.getLevel());
            }
        };
    }

    /**
     * Comparaison par nom
     * @return
     */
    private Comparator<Membre> getComparatorByName(){
        return new Comparator<Membre>() {
            @Override
            public int compare(Membre m1, Membre m2) {
                return m1.getLastname().compareTo(m2.getLastname());
            }
        };
    }
    /**
     * Permet de recuperer la liste des membres
     * @param context
     * @param type
     * @return
     */
    private void getMapMembres(Context context, String type, Map<Long, Membre> membres) {
        if(membres.isEmpty()){
            InputStream is = null;
            List<Membre> membreListe = null;
            JsonParser jp = null;
            try{
                //On regarde si fichier telecharge
                File myFile = FileUtils.getFileJson(context,TypeFile.getTypeFile(type));
                if(myFile==null){
                    //On prend celui inclut dans l'archive
                    is = FileUtils.getRawFileJson(context, TypeFile.getTypeFile(type));
                }
                else{
                    is = new FileInputStream(myFile);
                }
                jp = this.jsonFactory.createJsonParser(is);
                membreListe = this.objectMapper.readValue(jp, new TypeReference<List<Membre>>() {});
            }
            catch (IOException e) {
                Log.e(TAG, "Erreur lors de la recuperation des " + type, e);
            }
            finally {
                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de fermer le fichier " + type, e);
                    }
                }
            }
            //On transforme la liste en Map
            if(membreListe!=null){
                for(Membre m : membreListe){
                    membres.put(m.getId(), m);
                }
            }
        }
    }

    /**
     *
     * @param context
     * @param typeAppel
     * @param key
     * @return
     */
    public Membre getMembre(Context context, String typeAppel, Long key) {
        if(TypeFile.members.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, membres);
            return membres.get(key);
        }
        else if(TypeFile.staff.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, staff);
            return staff.get(key);
        }
        else if(TypeFile.sponsor.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, sponsors);
            return sponsors.get(key);
        }
        else if(TypeFile.speaker.name().equals(typeAppel)){
            getMapMembres(context, typeAppel, speaker);
            return speaker.get(key);
        }
        return null;
    }


}
