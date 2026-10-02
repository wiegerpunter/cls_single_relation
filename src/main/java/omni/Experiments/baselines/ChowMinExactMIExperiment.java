package omni.Experiments.baselines;

import omni.Config;
import omni.Experiments.Experiment;
import omni.Experiments.RunExperiments;
import omni.Experiments.parameterSetting.RamToPar;
import omni.synopses.chowMin.ChowMinAllPairs;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static omni.Experiments.baselines.ChowMinExperiment.estimateMIs;
import static omni.synopses.chowMin.ChowLiu.*;

public class ChowMinExactMIExperiment implements Experiment {
    private RunExperiments runExperiments;
    @Override
    public void run(long ram, RamToPar rtp, int repetition, Config config) throws IOException {

        HashMap<Integer, Integer> widthMap = rtp.getParamsChowMin(ram);
        int d = config.d;
        System.out.println("Running chowMin with parameters: " + widthMap.toString());

        // EXACT Mutual Information
        double[][] MIs = computeExactMIs(runExperiments, config, config.numStoredAttributes);
        ChowMinAllPairs cm = new ChowMinAllPairs(ram, config.numStoredAttributes, widthMap, d, true);
        cm.setMIs(MIs, false);
        cm.printParams();
        runExperiments.runSynopsisRamBased(cm, repetition);

        // Estimated Mutual Information

        double[][] estimatedMIs = estimateMIs(runExperiments, config, ram, widthMap, d);
        ChowMinAllPairs cm_est = new ChowMinAllPairs(ram, config.numStoredAttributes, widthMap, d, true);
        cm_est.setMIs(estimatedMIs, true);
        cm_est.printParams();
        runExperiments.runSynopsisRamBased(cm_est, repetition);

        // Print Differences
        differenceMIs(MIs, estimatedMIs);



    }

    private void differenceMIs(double[][] mIs, double[][] estimatedMIs) {
        // Difference in MIs itself:
        // 1. get total MIs
        // 2. per pair, normalize by total.
        // 3. MI - estMI
        // 4. print
        double totalExactMI = totalMI(mIs);
        double totalEstMI = totalMI(estimatedMIs);
        double[] differences = new double[mIs.length];
        double sumDifference = 0;
        for (int i = 0; i < mIs.length; i++) {
            differences[i] = mIs[i][2]/totalExactMI - estimatedMIs[i][2]/totalEstMI;
            sumDifference += Math.abs(differences[i]);
            System.out.println("MI: " + mIs[i][0] + ", " + mIs[i][1] + "=  " + mIs[i][2] +" , estimate: " + estimatedMIs[i][2] + " Difference " +  differences[i]);
        }

        System.out.println("Total absolute difference: " + sumDifference);
    }

    double totalMI(double[][] mIs) {
        double totalMI = 0;
        for (double[] d: mIs) {
            totalMI += d[2];
        }
        return totalMI;
    }


    @Override
    public void setRunExperiments(RunExperiments runExperiments) {
        this.runExperiments = runExperiments;
    }
}
