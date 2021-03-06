package projet.algav.benchmarck;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import projet.algav.benchmarck.trie.Hybride.HybrideBenchmark;
import projet.algav.benchmarck.trie.Patricia.PatriciaBenchmark;

import java.util.concurrent.TimeUnit;


public class BenchmarkLauncher {
    public static void runSingleShotTimeBenchmark(String className,String csvName,int nbIteration){
        Options optP = new OptionsBuilder()
                .include(className)
                .measurementIterations(nbIteration)
                .warmupIterations(nbIteration)
                .measurementTime(TimeValue.milliseconds(50))
                .warmupTime(TimeValue.milliseconds(50))
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .forks(1)
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark output/"+csvName+"_ajouter_un_mot.csv")
                .build();
        try {
            new Runner(optP).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
    public static void runAverageTimeBenchmark(String className,String csvName){
        Options opt = new OptionsBuilder()
                .include(className)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupIterations(10)
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark output/AverageTime_"+csvName+".csv")
                .build();
        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        runSingleShotTimeBenchmark(HybrideBenchmark.class.getName(),HybrideBenchmark.class.getSimpleName(),30);
        runSingleShotTimeBenchmark(PatriciaBenchmark.class.getName(),PatriciaBenchmark.class.getSimpleName(),30);
    }
}
