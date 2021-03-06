package projet.algav.benchmarck.trie.Patricia;

import org.openjdk.jmh.annotations.Benchmark;
import projet.algav.benchmarck.trie.AllFilesBenchmarkContainer;
import projet.algav.benchmarck.trie.AllWordsBenchmarkContainer;
import projet.algav.trie.Patricia;

import java.util.Iterator;


/**
 * Created by Yassine on 03/12/2016.
 */
public class PatriciaBenchmark {
    /*    public void benchmark_ajouter_par_fichier_apres_construction(AllFilesBenchmarkContainer fc) {
            for (String s : fc.motQuiNExistePasEnAnglais)
                fc.getPatricia().ajouter(s);
        }

        public Patricia benchmark_ajouter_par_fichier(AllFilesBenchmarkContainer fc) {
            Iterator<String> iterator = fc.getIterator();
            while (iterator.hasNext())
                fc.getPatricia().ajouter(iterator.next());
            return fc.getPatricia();
        }*/
    public void benchmark_recherche(AllWordsBenchmarkContainer wc) {
        wc.patricia.recherche(wc.motAChercher);
    }

    public void benchmark_supprimer_ensemble_de_mots(AllWordsBenchmarkContainer wc) {
        for (String s : wc.motsASupprimer)
            wc.patricia.supprimer(s);
    }
    public void benchmark_supprimer_un_mot(AllWordsBenchmarkContainer wc) {
        wc.patricia.supprimer(wc.motASupprimer);
    }

    @Benchmark
    public void benchmark_ajouter_unmotQuiNExistePasEnAnglais(AllWordsBenchmarkContainer wc) {
        wc.patricia.ajouter(wc.motQuiNExistePasEnAnglais);
    }

    public void benchmark_ajouter_tout_les_mots(AllWordsBenchmarkContainer wc) {
        while (wc.iterator.hasNext())
            wc.patricia.ajouter(wc.iterator.next());
    }
}
