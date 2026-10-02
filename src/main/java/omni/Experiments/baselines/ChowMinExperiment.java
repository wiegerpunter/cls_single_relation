package omni.Experiments.baselines;

import omni.Config;
import omni.Experiments.Experiment;
import omni.Experiments.RunExperiments;
import omni.Experiments.parameterSetting.RamToPar;
import omni.synopses.chowMin.ChowMinAllPairs;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ChowMinExperiment implements Experiment {
    private RunExperiments runExperiments;
    @Override
    public void run(long ram, RamToPar rtp, int repetition, Config config) throws IOException {

        HashMap<Integer, Integer> widthMap = rtp.getParamsChowMin(ram);
        System.out.println("Running chowMin with widthMap: " + widthMap.toString());
        int d = config.d;

        double[][] estimatedMIs = estimateMIs(runExperiments, config, ram, widthMap, d);
        ChowMinAllPairs cm = new ChowMinAllPairs(ram, config.numStoredAttributes, widthMap, d, false);
        cm.setMIs(estimatedMIs, true);

        cm.printParams();
//        aSH ash = new aSH(ram, config.useNoCast, config.numStoredAttributes, params, repetition, config.withDeletes, config.bufferASH);
        runExperiments.runSynopsisRamBased(cm, repetition);
    }


    public static double[][] estimateMIs(RunExperiments runExperiments, Config config, long ram, HashMap<Integer, Integer> widthMap, int d) {

        ChowMinAllPairs ChowLiuMinAllPairs = new ChowMinAllPairs(ram, config.numStoredAttributes, widthMap, d, false);
        long time_passed = runExperiments.ingestData(ChowLiuMinAllPairs);
        return ChowLiuMinAllPairs.estimateMIs();
    }

    @Override
    public void setRunExperiments(RunExperiments runExperiments) {
        this.runExperiments = runExperiments;
    }
}
