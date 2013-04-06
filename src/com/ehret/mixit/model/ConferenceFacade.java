package com.ehret.mixit.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.TypeFile;
import com.ehret.mixit.domain.people.Membre;
import com.ehret.mixit.domain.talk.Conference;
import com.ehret.mixit.domain.talk.Lightningtalk;
import com.ehret.mixit.domain.talk.Talk;
import com.ehret.mixit.ui.utils.FileUtils;
import com.ehret.mixit.ui.utils.UIUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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
import java.util.*;

/**
 * Le but de ce fichier est de s'interfacer avec le fichier Json gerant les
 * différentes conf et lightning talks.
 */
public class ConferenceFacade {
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
    private static ConferenceFacade membreFacade;

    private final static String TAG = "ConferenceFacade";

    private static Map<Long, Talk> talks = new HashMap<Long, Talk>();
    private static Map<Long, Lightningtalk> lightningtalks = new HashMap<Long, Lightningtalk>();

    /**
     * Events du calendrier qui ne sont pas envoyés par Mixit
     */
    private static Map<Long, Talk> talksSpeciaux = new HashMap<Long, Talk>();

    /**
     * Permet de vider le cache de données hormis les events speciaux
     */
    public void viderCache(){
        talks.clear();
        lightningtalks.clear();
    }

    /**
     * Constructeur prive car singleton
     */
    private ConferenceFacade() {
        //Creation de nos objets
        this.jsonFactory = new JsonFactory();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retourne le singleton
     * @return
     */
    public static ConferenceFacade getInstance(){
        if(membreFacade==null){
            membreFacade = new ConferenceFacade();
        }
        return membreFacade;
    }

    /**
     * Permet de recuperer la liste des talks
     * @param context
     * @return
     */
    public List<Talk> getTalks(Context context) {
        return Ordering.from(getComparatorDate()).compound(getComparatorConference()).sortedCopy(filtrerTalkFiltreParType(getTalkAndWorkshops(context), TypeFile.talks));
    }

    /**
     * Permet de recuperer la liste des talks
     * @param context
     * @return
     */
    public List<Talk> getWorkshops(Context context) {
        return Ordering.from(getComparatorDate()).compound(getComparatorConference()).sortedCopy(filtrerTalkFiltreParType(getTalkAndWorkshops(context), TypeFile.workshops));
    }

    /**
     * Cette méthode cherche les talks sur cette période
     * @param date
     * @return
     */
    public List<Conference> getConferenceSurPlageHoraire(Date date, Context context){
        List<Conference> confs = new ArrayList<Conference>();
        //On recupere les talks
        Collection<Talk> talks = getTalkAndWorkshops(context).values();

        //On decale la date de 1 minute pour ne pas avoir de souci de comparaison
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE,1);
        Date dateComparee = calendar.getTime();

        for(Talk talk : talks){
            if(talk.getStart()!=null && talk.getEnd()!=null && (dateComparee.before(talk.getEnd()) && dateComparee.after(talk.getStart()))){
                confs.add(talk);
            }
        }

        //On ajoute ls events particuliers
        Collection<Talk> talkSpeciaux = getEventsSpeciaux().values();
        for(Talk talk : talkSpeciaux){
            if(talk.getStart()!=null && talk.getEnd()!=null && (dateComparee.before(talk.getEnd()) && dateComparee.after(talk.getStart()))){
                confs.add(talk);
            }
        }
        return confs;
    }

    public  Map<Long, Talk> getEventsSpeciaux(){
        if(talksSpeciaux.isEmpty()){
            talksSpeciaux.put(90000L, createKeynoteTalk(25, 90000));
            talksSpeciaux.put(90001L, createKeynoteTalk(26, 90001));

            Talk repas = null;
            repas = createTalkHorsConf("Repas", 80000);
            repas.setStart(UIUtils.createPlageHoraire(25, 12, false));
            repas.setEnd(UIUtils.createPlageHoraire(25, 12,true));
            talksSpeciaux.put(repas.getId(), repas);
            repas = createTalkHorsConf("Repas", 80001);
            repas.setStart(UIUtils.createPlageHoraire(25, 12, true));
            repas.setEnd(UIUtils.createPlageHoraire(25, 13,false));
            talksSpeciaux.put(repas.getId(), repas);
            repas = createTalkHorsConf("Repas", 80002);
            repas.setStart(UIUtils.createPlageHoraire(26, 12, false));
            repas.setEnd(UIUtils.createPlageHoraire(26, 12,true));
            talksSpeciaux.put(repas.getId(), repas);
            repas = createTalkHorsConf("Repas", 80003);
            repas.setStart(UIUtils.createPlageHoraire(26, 12, true));
            repas.setEnd(UIUtils.createPlageHoraire(26, 13,false));
            talksSpeciaux.put(repas.getId(), repas);

            Talk pause = null;
            pause = createTalkHorsConf("Pause", 70000);
            pause.setStart(UIUtils.createPlageHoraire(25, 10, true));
            pause.setEnd(UIUtils.createPlageHoraire(25, 11,false));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70001);
            pause.setStart(UIUtils.createPlageHoraire(25, 14, false));
            pause.setEnd(UIUtils.createPlageHoraire(25, 14,true));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70002);
            pause.setStart(UIUtils.createPlageHoraire(25, 16, false));
            pause.setEnd(UIUtils.createPlageHoraire(25, 16,true));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70003);
            pause.setStart(UIUtils.createPlageHoraire(25, 17, true));
            pause.setEnd(UIUtils.createPlageHoraire(25, 18,false));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70004);
            pause.setStart(UIUtils.createPlageHoraire(26, 10, true));
            pause.setEnd(UIUtils.createPlageHoraire(26, 11,false));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70005);
            pause.setStart(UIUtils.createPlageHoraire(26, 14, false));
            pause.setEnd(UIUtils.createPlageHoraire(26, 14,true));
            talksSpeciaux.put(pause.getId(), pause);
            pause = createTalkHorsConf("Pause", 70006);
            pause.setStart(UIUtils.createPlageHoraire(26, 16, false));
            pause.setEnd(UIUtils.createPlageHoraire(26, 16,true));
            talksSpeciaux.put(pause.getId(), pause);

            Talk lit = null;
            lit = createTalkHorsConf("Lightning Talks", 100000);
            lit.setStart(UIUtils.createPlageHoraire(25, 13, false));
            lit.setEnd(UIUtils.createPlageHoraire(25, 13,true));
            talksSpeciaux.put(lit.getId(), lit);
            lit = createTalkHorsConf("Lightning Talks", 100001);
            lit.setStart(UIUtils.createPlageHoraire(26, 13, false));
            lit.setEnd(UIUtils.createPlageHoraire(26, 13,true));
            talksSpeciaux.put(lit.getId(), lit);

        }
        return talksSpeciaux;
    }

    private Talk createTalkHorsConf(String titleFormat, long id) {
        Talk repas = null;
        repas = new Talk();
        repas.setFormat(titleFormat);
        repas.setTitle(titleFormat);
        repas.setId(id);
        return repas;
    }

    private Talk createKeynoteTalk(int jour , long id) {
        Talk keynote;
        keynote = createTalkHorsConf("Keynote", id);
        keynote.setStart(UIUtils.createPlageHoraire(jour, 9, false));
        keynote.setEnd(UIUtils.createPlageHoraire(jour, 9,true));
        return keynote;
    }

    /**
     * Permet de recuperer la liste des talks
     * @param context
     * @return
     */
    private Map<Long, Talk> getTalkAndWorkshops(Context context) {
        if(talks.isEmpty()){
            InputStream is = null;
            List<Talk> talkListe = null;
            JsonParser jp = null;
            try{
                //On regarde si fichier telecharge
                File myFile = FileUtils.getFileJson(context,TypeFile.talks);
                if(myFile==null){
                    //On prend celui inclut dans l'archive
                    is = FileUtils.getRawFileJson(context, TypeFile.talks);
                }
                else{
                    is = new FileInputStream(myFile);
                }
                jp = this.jsonFactory.createJsonParser(is);
                talkListe = this.objectMapper.readValue(jp, new TypeReference<List<Talk>>() {});
                //On transforme la liste en Map
                for(Talk m : talkListe){
                    talks.put(m.getId(), m);
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Erreur lors de la recuperation des talks", e);
            }
            finally {
                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de fermer le fichier des talks", e);
                    }
                }
            }
        }
        return talks;
    }

    /**
     * Permet de recuperer la liste des talks
     * @param context
     * @return
     */
    public Map<Long, Lightningtalk> getLightningtalks(Context context) {
        if(lightningtalks.isEmpty()){
            InputStream is = null;
            List<Lightningtalk> talkListe = null;
            JsonParser jp = null;
            try{
                //On regarde si fichier telecharge
                File myFile = FileUtils.getFileJson(context, TypeFile.lightningtalks);
                if(myFile==null){
                    //On prend celui inclut dans l'archive
                    is = FileUtils.getRawFileJson(context, TypeFile.lightningtalks);
                }
                else{
                    is = new FileInputStream(myFile);
                }
                jp = this.jsonFactory.createJsonParser(is);
                talkListe = this.objectMapper.readValue(jp, new TypeReference<List<Lightningtalk>>() {});
                //On transforme la liste en Map
                for(Lightningtalk m : talkListe){
                    lightningtalks.put(m.getId(), m);
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Erreur lors de la recuperation des lightning talks", e);
            }
            finally {
                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de fermer le fichier des lightnings talks", e);
                    }
                }
            }
        }
        return lightningtalks;
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Talk getTalk(Context context, Long key) {
        return getTalkAndWorkshops(context).get(key);
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Talk getWorkshop(Context context, Long key) {
        return getTalkAndWorkshops(context).get(key);
    }

    /**
     *
     * @param context
     * @param key
     * @return
     */
    public Lightningtalk getLightningtalk(Context context, Long key) {
        return getLightningtalks(context).get(key);
    }

    /**
     * Filtre la liste des talks ou des workshops
     * @param talks
     * @param type
     * @return
     */
    private List<Talk> filtrerTalkFiltreParType(Map<Long, Talk> talks, final TypeFile type){
        return FluentIterable.from(talks.values()).filter(new Predicate<Talk>() {
            @Override
            public boolean apply(Talk input) {
                if (type.equals(TypeFile.workshops)) {
                    return "Workshop".equals(((Talk) input).getFormat());
                }
                return !"Workshop".equals(((Talk) input).getFormat());
            }
        }).toImmutableList();
    }

    /**
     * Renvoie le comparator permettant de trier des conf
     * @return
     */
    private <T extends Conference> Comparator<T> getComparatorDate(){
        return new Comparator<T>() {
            @Override
            public int compare(T m1, T m2) {
                if (m1.getStart() == null && m2.getStart() == null)
                    return 0;
                if (m1.getStart() == null)
                    return -1;
                if (m2.getStart() == null)
                    return 1;
                return  m1.getStart().compareTo(m2.getStart());
            }
        };
    }

    /**
     * Renvoie le comparator permettant de trier des conf
     * @return
     */
    private <T extends Conference> Comparator<T> getComparatorConference(){
        return new Comparator<T>() {
            @Override
            public int compare(T m1, T m2) {
                return m1.getTitle().compareTo(m2.getTitle());
            }
        };
    }

    /**
     * Renvoi la liste des membres attachés à une session
     * @param membre
     * @return
     */
    public List<Conference> getSessionMembre(Membre membre, Context context){
        List<Conference> sessions  = new ArrayList<Conference>();

        //On recherche les talks
        List<Talk> listetalks = Lists.newArrayList(getTalkAndWorkshops(context).values());
        for(Talk t : listetalks){
            for (Long idS : t.getSpeakers()){
                if(Long.valueOf(membre.getId()).equals(idS)){
                    sessions.add(t);
                }
            }
        }
        List<Lightningtalk> listelt = Lists.newArrayList(getLightningtalks(context).values());
        for(Lightningtalk t : listelt){
            for (Long idS : t.getSpeakers()){
                if(Long.valueOf(membre.getId()).equals(idS)){
                    sessions.add(t);
                }
            }
        }

        return sessions;
    }
}