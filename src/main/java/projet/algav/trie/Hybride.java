package projet.algav.trie;

import com.google.gson.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Hybride implements Trie, Cloneable, Serializable {
    private static final char motVide = 'Ø';
    static int cpt = 0;
    int position = 0;
    Hybride inf, sup, eq;
    char valeur;
    private Hybride pere;

    public Hybride() {
        valeur = motVide;
    }

    Hybride(Hybride pere) {
        valeur = motVide;
        this.pere = pere;
    }

    @Override
    public void ajouter(String mot) {
        if (mot.length() == 0)
            return;
        if (estVide()) {
            valeur = mot.charAt(0);
            if (mot.length() == 1) {
                cpt++;
                position = cpt;
            } else {
                if (eq == null)
                    eq = new Hybride(this);
                eq.ajouter(mot.substring(1));
            }
        } else {
            char premier = mot.charAt(0);
            if (premier < valeur) {
                if (inf == null)
                    inf = new Hybride(this);
                inf.ajouter(mot);
            } else if (premier > valeur) {
                if (sup == null)
                    sup = new Hybride(this);
                sup.ajouter(mot);
            } else if (premier == valeur) {
                if (eq == null && mot.length() != 1)
                    eq = new Hybride(this);
                if (mot.length() == 1) {
                    cpt++;
                    position = cpt;
                } else
                    eq.ajouter(mot.substring(1));
            }
        }
    }

    @Override
    public boolean estVide() {
        return valeur == motVide;
    }

    @Override
    public boolean recherche(String mot) {
        if (mot.isEmpty() || this.estVide())
            return false;
        if (mot.length() == 1) {
            if (mot.charAt(0) == this.valeur && this.position != 0)
                return true;
            if (inf != null)
                if (mot.charAt(0) == inf.valeur && inf.position != 0)
                    return true;
            if (sup != null)
                if (mot.charAt(0) == sup.valeur && sup.position != 0)
                    return true;
            if (mot.charAt(0) < valeur)
                return inf != null && inf.recherche(mot);
            if (mot.charAt(0) > valeur)
                return sup != null && sup.recherche(mot);
        } else {
            char p = mot.charAt(0);
            if (p == valeur) {
                return eq != null && eq.recherche(mot.substring(1));
            }
            if (p < valeur) {
                return inf != null && inf.recherche(mot);
            }
            if (p > valeur) {
                return sup != null && sup.recherche(mot);
            }

        }
        return false;
    }

    @Override
    public int comptageMots() {
        int nbMots = 0;
        if (this.position != 0)
            nbMots++;
        if (eq != null)
            nbMots += eq.comptageMots();
        if (inf != null)
            nbMots += inf.comptageMots();
        if (sup != null)
            nbMots += sup.comptageMots();
        return nbMots;
    }

    @Override
    public List<String> listeMots() {
        List<String> mots = new ArrayList<>();
        String val = String.valueOf(valeur);
        if (inf != null)
            mots.addAll(inf.listeMots());
        if (position != 0)
            mots.add(val);
        if (eq != null)
            mots.addAll(ajouterPrefixe(val, eq.listeMots()));
        if (sup != null)
            mots.addAll(sup.listeMots());
        return mots;
    }

    @Override
    public int comptageNil() {
        int nbNils = 0;
        if (eq != null)
            nbNils += eq.comptageNil();
        else
            nbNils++;
        if (inf != null)
            nbNils += inf.comptageNil();
        else
            nbNils++;
        if (sup != null)
            nbNils += sup.comptageNil();
        else
            nbNils++;
        return nbNils;
    }

    @Override
    public JsonElement toNotCollapsibleJSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonSerializer<Hybride> serializer = new JsonSerializer<Hybride>() {
            @Override
            public JsonElement serialize(Hybride hybride, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", valeur + (position != 0 ? "," + position : "  "));
                if (pere != null) {
                    jsonObject.addProperty("parent", pere.valeur + (pere.position != 0 ? "," + pere.position : "  "));
                } else {
                    jsonObject.addProperty("parent", "null");
                }
                JsonArray children = new JsonArray();
                for (Hybride h : hybride.getSousArbres())
                    if (h != null)
                        children.add(h.toNotCollapsibleJSON());
                    else {
                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("name", motVide);
                        children.add(tmp);
                    }
                jsonObject.add("children", children);
                if (pere == null) {
                    JsonArray tmp = new JsonArray();
                    tmp.add(jsonObject);
                    return tmp;
                } else {
                    return jsonObject;
                }
            }
        };
        gsonBuilder.registerTypeAdapter(Hybride.class, serializer);
        Gson customGson = gsonBuilder.create();
        return customGson.toJsonTree(this);
    }

    @Override
    public JsonElement toCollapsibleJSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonSerializer<Hybride> serializer = new JsonSerializer<Hybride>() {
            @Override
            public JsonElement serialize(Hybride hybride, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", valeur + (position != 0 ? "," + position : "  "));
                JsonArray children = new JsonArray();
                for (Hybride h : hybride.getSousArbres())
                    if (h != null)
                        children.add(h.toCollapsibleJSON());
                    else {
                        JsonObject tmp = new JsonObject();
                        tmp.addProperty("name", motVide);
                        children.add(tmp);
                    }
                jsonObject.add("children", children);
                return jsonObject;
            }
        };
        gsonBuilder.registerTypeAdapter(Hybride.class, serializer);
        Gson customGson = gsonBuilder.create();
        return customGson.toJsonTree(this);
    }

    private int longueurDeLabranche() {
        int lvl = 0;
        Hybride pere = this.pere;
        while (pere != null) {
            pere = pere.pere;
            lvl++;
        }
        return lvl;
    }

    private List<String> ajouterPrefixe(String mot, List<String> list) {
        List<String> res = new ArrayList<>();
        for (String suffixe : list) {
            res.add(mot + suffixe);
        }
        return res;
    }

    @Override
    public int hauteur() {
        int infH = 0, supH = 0, eqH = 0;
        if (this.estVide())
            return 0;
        if (inf != null)
            infH = inf.hauteur();
        if (eq != null)
            eqH = eq.hauteur();
        if (sup != null)
            supH = sup.hauteur();

        return Integer.max(Integer.max(infH, eqH), supH) + 1;

    }

    @Override
    public int largeur() {
        if (this.isLeaf())
            return 3;
        int cpt = 0;
        if (inf != null)
            cpt += inf.largeur();
        if (eq != null)
            cpt += eq.largeur();
        if (sup != null)
            cpt += sup.largeur();
        return cpt;
    }

    private List<Hybride> getFeuilles() {
        List<Hybride> feuilles = new ArrayList<>();
        if (inf == null && eq == null && sup == null) {
            feuilles.add(this);
            return feuilles;
        } else {
            if (inf != null)
                feuilles.addAll(inf.getFeuilles());
            if (eq != null)
                feuilles.addAll(eq.getFeuilles());
            if (sup != null)
                feuilles.addAll(sup.getFeuilles());
        }

        return feuilles;

    }

    @Override
    public double profondeurMoyenne() {
        int prof = 0;
        int nbrFeuille = (this.getFeuilles()).size();
        for (Hybride feuille : this.getFeuilles()) {
            prof += feuille.longueurDeLabranche();
        }
        return (double) prof / (double) nbrFeuille;
    }

    @Override
    public int prefixe(String mot) {
        if (mot.isEmpty() || this.estVide())
            return 0;
        char premier = mot.charAt(0);
        if (mot.length() == 1) {
            if (premier == this.valeur) {
                if (eq != null)
                    if (position != 0)
                        return eq.comptageMots() + 1;
                    else
                        return eq.comptageMots();
            } else if (premier < valeur) {
                if (inf != null)
                    return inf.prefixe(mot);
            } else if (premier > valeur) {
                if (sup != null)
                    return sup.prefixe(mot);
            }
        } else {
            if (premier == valeur) {
                if (eq != null)
                    return eq.prefixe(mot.substring(1));
            } else if (premier < valeur) {
                if (inf != null)
                    return inf.prefixe(mot);
            } else if (premier > valeur)
                if (sup != null)
                    return sup.prefixe(mot);
        }
        return 0;
    }

    @Override
    public boolean supprimer(String mot) {
        if (mot.isEmpty() || this.estVide())
            return false;
        char p = mot.charAt(0);
        if (mot.length() == 1) {
            if (p == this.valeur && this.position != 0) {
                this.position = 0;
                if (isLeaf()) {
                    this.couperLesBranchesInutiles();
                }
                return true;
            }
            Hybride[] fils = {inf, sup};
            for (Hybride h : fils) {
                if (h != null)
                    if (p == h.valeur && h.position != 0) {
                        h.position = 0;
                        if (isLeaf()) {
                            this.couperLesBranchesInutiles();
                        }
                        return true;
                    }
            }
            for (Hybride h : fils) {
                if (h != null) {
                    if (p < valeur)
                        return inf.supprimer(mot);
                    if (p > valeur) {
                        return sup.supprimer(mot);
                    }
                }
            }
            return false;
        } else {
            if (p == valeur) {
                return eq != null && eq.supprimer(mot.substring(1));
            }
            if (p < valeur) {
                return inf != null && inf.supprimer(mot);
            }
            if (p > valeur) {
                return sup != null && sup.supprimer(mot);
            }
        }
        return false;
    }

    /**
     * Fonction qui fait une copie attribut par attribut d'un Hybride-Trie
     *
     * @return un clone de l'Hybride-Trie courant
     */
    @Override
    public Object clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Hybride hybride = new Hybride();
        hybride.valeur = this.valeur;
        hybride.position = this.position;
        if (eq != null)
            hybride.eq = (Hybride) this.eq.clone();
        if (inf != null)
            hybride.inf = (Hybride) this.inf.clone();
        if (sup != null)
            hybride.sup = (Hybride) this.sup.clone();
        if (pere != null)
            hybride.pere = (Hybride) this.pere.clone();

        return hybride;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof Hybride) {
            boolean res = true;
            if (valeur == ((Hybride) o).valeur && position == ((Hybride) o).position) {
                Hybride[] sa = getSousArbres();
                for (int i = 0; i < sa.length; i++) {
                    res = res && sa[i] != null ? sa[i].equals(((Hybride) o).getSousArbres()[i]) : ((Hybride) o).getSousArbres()[i] == null;
                }
                return res;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        int lb = this.longueurDeLabranche();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lb; i++) {
            sb.append("|\t");
        }
        sb.append(valeur).append(position != 0 ? "," + position : "  ").append("\n");
        Hybride[] fils = getSousArbres();
        for (Hybride h : fils) {
            if (h == null) {
                for (int i = 0; i < lb + 1; i++) {
                    sb.append("|\t");
                }
                sb.append("∅\n");
            } else
                sb.append(h.toString());
        }
        return sb.toString();
    }

    /**
     * @param hybride Le Hybride-trie à fusionner
     * @return le resultat de la fusion
     * @deprecated ne marche pas tres bien à évité
     */
    public Hybride fusion(Hybride hybride) {
        if (hybride != null) {
            if (estVide()) {
                this.initialisationClone(hybride);
                return this;
            }
            if (this.valeur == hybride.valeur) {
                if (eq == null) {
                    eq = hybride.eq;
                    if (eq != null)
                        eq.pere = this;
                } else eq.fusion(hybride.eq);
                if (inf == null) {
                    inf = hybride.inf;
                    if (inf != null)
                        inf.pere = this;
                } else inf.fusion(hybride.inf);
                if (sup == null) {
                    sup = hybride.sup;
                    if (sup != null)
                        sup.pere = this;
                } else sup.fusion(hybride.sup);
            } else if (this.valeur > hybride.valeur) {
                if (inf == null) {
                    inf = hybride;
                    inf.pere = this;
                    if (sup != null)
                        sup.fusion(hybride.sup);
                    else
                        sup = hybride.sup;
                    if (eq != null)
                        eq.fusion(hybride.eq);
                    else
                        eq = hybride.eq;
                    inf.sup = null;
                } else inf.fusion(hybride);
            } else if (this.valeur < hybride.valeur) {
                if (sup == null) {
                    sup = hybride;
                    sup.pere = this;
                    if (inf != null)
                        inf.fusion(hybride.inf);
                    else
                        inf = hybride.inf;
                    if (eq != null)
                        eq.fusion(hybride.eq);
                    else
                        eq = hybride.eq;
                    sup.inf = null;
                } else sup.fusion(hybride);
            }
        }
        return this;
    }

    public Patricia toPatricia() {
        Patricia p = new Patricia();
        if (this.estVide())
            return p;
        else {
            List<String> patriciaNode = this.getPatriciaNode();
            ArrayList<Hybride> sousArbres = new ArrayList<>();
            for (String cle : patriciaNode)
                sousArbres.add(getSubHybride(cle));
            for (int i = 0; i < sousArbres.size(); i++) {
                Hybride h = sousArbres.get(i);
                if (h != null) {
                    if (h.eq != null)
                        p.ajouterCleValeur(patriciaNode.get(i), h.eq.toPatricia());
                    else
                        p.ajouterCleValeur(patriciaNode.get(i), new Patricia());
                    if (h.position != 0)
                        p.node.get(patriciaNode.get(i)).ajouterCleValeur(Patricia.finMot, new Patricia());
                }
            }
        }
        return p;
    }

    public void ajouterMotPuisEquilibre(String mot) {
        if (mot != null && !mot.isEmpty()) {
            this.ajouter(mot);
            if (!isEquilibre())
                this.equilibre();
        }
    }

    void setPereDansFils() {
        if (eq != null)
            eq.pere = this;
        if (inf != null)
            inf.pere = this;
        if (sup != null)
            sup.pere = this;
    }

    private void initialisationClone(Hybride hybride) {
        this.valeur = hybride.valeur;
        this.pere = (Hybride) hybride.pere.clone();
        this.eq = (Hybride) hybride.eq.clone();
        this.inf = (Hybride) hybride.inf.clone();
        this.sup = (Hybride) hybride.sup.clone();
    }

    private void initialisation(Hybride hybride) {
        this.valeur = hybride.valeur;
        this.position = hybride.position;
        this.eq = hybride.eq;
        this.inf = hybride.inf;
        this.sup = hybride.sup;
    }

    private boolean isLeaf() {
        boolean videEq = true;
        boolean videInf = true;
        boolean videSup = true;

        if (eq != null)
            videEq = eq.estVide();
        if (inf != null)
            videInf = inf.estVide();
        if (sup != null)
            videSup = sup.estVide();
        return videEq && videInf && videSup;
    }

    private Hybride[] getSousArbres() {
        return new Hybride[]{inf, eq, sup};
    }

    private void couperLesBranchesInutiles() {
        Hybride pere = this.pere;
        Hybride self = this;
        while (pere != null && pere.comptageMots() == 0) {
            self = self.pere;
            pere = pere.pere;
        }
        if (pere == null) {
            //on est a la racine donc on reinitialise l'arbre
            self.position = 0;
            self.valeur = motVide;
            self.inf = null;
            self.eq = null;
            self.inf = null;
        } else if (self.position == 0) {
            //on coupe la branche inutile
            self.supprimerDansPere();
        }
    }

    private void supprimerDansPere() {
        if (pere == null)
            return;
        Hybride[] sousArbresPere = pere.getSousArbres();
        for (int i = 0; i < sousArbresPere.length; i++) {
            if (sousArbresPere[i] == this) {
                switch (i) {
                    case 0:
                        pere.inf = null;
                        break;
                    case 1:
                        pere.eq = null;
                        break;
                    case 2:
                        pere.sup = null;
                        break;
                }
            }
        }
    }

    private void equilibre() {
        int hauteurInf = this.inf == null ? 0 : this.inf.hauteur();
        int hauteurSup = this.sup == null ? 0 : this.sup.hauteur();

        if (hauteurInf - hauteurSup >= 2) {
            int hauteurInfSup = (this.inf.sup == null) ? 0 : this.inf.sup.hauteur();
            int hauteurInfInf = (this.inf.inf == null) ? 0 : this.inf.inf.hauteur();
            if (hauteurInfSup - hauteurInfInf >= 2)
                this.inf.rotationGauche();
            else if (hauteurInfSup - hauteurInfInf <= -2)
                this.inf.rotationDroite();
            this.rotationDroite();

        } else if (hauteurInf - hauteurSup <= -2) {
            int hauteurSupInf = (this.sup.inf == null) ? 0 : this.sup.inf.hauteur();
            int hauteurSupSup = (this.sup.sup == null) ? 0 : this.sup.sup.hauteur();
            if (hauteurSupInf - hauteurSupSup >= 2)
                this.sup.rotationDroite();
            else if (hauteurSupInf - hauteurSupSup <= -2)
                this.sup.rotationGauche();
            this.rotationGauche();
        }
    }

    private void rotationGauche() {
        Hybride h = new Hybride(this);
        if (this.sup == null)
            this.sup = new Hybride();
        h.valeur = this.valeur;
        if (this != null)
            h.position = this.position;
        h.eq = this.eq;
        h.inf = this.inf;
        h.sup = this.sup.inf;
        Hybride h2 = new Hybride(this);
        if (this.sup == null)
            this.sup = new Hybride();
        h2.valeur = this.sup.valeur;
        if (this.sup != null)
            h2.position = this.sup.position;
        h2.eq = this.sup.eq;
        h2.inf = h;
        h2.sup = this.sup.sup;

        this.initialisation(h2);

    }

    private void rotationDroite() {
        Hybride h = new Hybride(this);
        h.valeur = this.valeur;
        if (this.inf == null)
            this.inf = new Hybride();
        if (this != null)
            h.position = this.position;
        h.eq = this.eq;
        h.inf = this.inf.sup;
        h.sup = this.sup;
        Hybride h2 = new Hybride(this);
        if (this.inf == null)
            this.inf = new Hybride();
        h2.valeur = this.inf.valeur;
        if (this.inf != null)
            h2.position = this.inf.position;
        h2.eq = this.inf.eq;
        h2.inf = this.inf.inf;
        h2.sup = h;

        this.initialisation(h2);
    }

    boolean isEquilibre() {
        int hauteur_inf = (this.inf == null) ? 0 : this.inf.hauteur();
        int hauteur_sup = (this.sup == null) ? 0 : this.sup.hauteur();
        if (Math.abs(hauteur_inf - hauteur_sup) <= 2)
            return true;
        return false;
    }

    private List<String> getPatriciaNode() {
        List<String> listMotsRacine = new ArrayList<>();
        listMotsRacine.add(this.getEqPrefixe());
        if (this.inf != null)
            listMotsRacine.addAll(this.inf.getPatriciaNode());

        if (this.sup != null)
            listMotsRacine.addAll(this.sup.getPatriciaNode());

        return listMotsRacine;
    }

    private String getEqPrefixe() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.valeur);
        if (this.eq != null && this.position == 0) {
            Hybride tmp = this.eq;
            if (tmp.sup == null && tmp.inf == null)
                sb.append(tmp.valeur);
            while (tmp.sup == null && tmp.inf == null && tmp.eq != null && tmp.position == 0) {
                if (tmp.eq.sup == null && tmp.eq.inf == null)
                    sb.append(tmp.eq.valeur);
                tmp = tmp.eq;
            }
        }
        return sb.toString();
    }

    private Hybride getSubHybride(String mot) {
        if (mot.isEmpty() || this.estVide())
            return null;
        if (mot.length() == 1) {
            if (mot.charAt(0) == this.valeur)
                return this;
            if (inf != null)
                if (mot.charAt(0) == inf.valeur)
                    return this.inf;
            if (sup != null)
                if (mot.charAt(0) == sup.valeur)
                    return this.sup;
            if (mot.charAt(0) < valeur)
                return inf != null ? inf.getSubHybride(mot) : null;
            if (mot.charAt(0) > valeur)
                return sup != null ? sup.getSubHybride(mot) : null;
        } else {
            char p = mot.charAt(0);
            if (p == valeur) {
                return eq != null ? eq.getSubHybride(mot.substring(1)) : null;
            }
            if (p < valeur) {
                return inf != null ? inf.getSubHybride(mot) : null;
            }
            if (p > valeur) {
                return sup != null ? sup.getSubHybride(mot) : null;
            }
        }
        return null;
    }
}
