import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class OutputMonolithicStackDetector implements DetectorOutputInterface {
    public static class StackResult {
        public final boolean pseudoMonolith;
        public final Graph<RootComponent, RootRelation> largestConnectedComponent;
        public final double minCutWeightScore;
        public final double densityScore;
        public final double score;

        public StackResult(boolean pseudoMonolith, Graph<RootComponent, RootRelation> largestConnectedComponent, double minCutWeightScore, double densityScore, double score) {
            this.pseudoMonolith = pseudoMonolith;
            this.largestConnectedComponent = largestConnectedComponent;
            this.minCutWeightScore = minCutWeightScore;
            this.densityScore = densityScore;
            this.score = score;
        }

        public boolean isPseudoMonolith() {
            return pseudoMonolith;
        }

        public Graph<RootComponent, RootRelation> getLargestConnectedComponent() {
            return largestConnectedComponent;
        }

        public double getMinCutWeightScore() {
            return minCutWeightScore;
        }

        public double getDensityScore() {
            return densityScore;
        }

        public double getScore() {
            return score;
        }
    }

    private Map<String, StackResult> results;
    public OutputMonolithicStackDetector(Map<String, StackResult> results) {
        this.results = results;
    }

    @Override
    public double getScore() {
        Stream<StackResult> resultsStream = results.values().stream();
        Stream<Double> probabilities = resultsStream.map(result -> result.getScore());
        return probabilities.max(Double::compare).get();
    }

    /**
     * Write a report into the buffered writer object.
     * @param bw Target of the report.
     * @throws IOException
     */
    @Override
    public void write(BufferedWriter bw) throws IOException {
        bw.write("Monolithic Stack Detektor");
        bw.newLine();
        bw.write("The stack with the highest overall score has an overall score of " + getScore() + ".");
        bw.newLine();
        bw.newLine();
        for (Map.Entry<String, StackResult> result : results.entrySet()) {
            writeResult(result.getKey(), result.getValue(), bw);
            bw.newLine();
        }
    }

    private void writeResult(String stackID, StackResult result, BufferedWriter bw) throws IOException {
        bw.write("Results for stack with id " + stackID + ":");
        bw.newLine();
        if (result.isPseudoMonolith()) {
            bw.write("The stack with id " + stackID + " is a Pseudo-Monolith.");
            bw.newLine();
            bw.write("Results are printed for the largest connected component.");
            bw.newLine();
        }
        bw.newLine();
        writeStack(result.largestConnectedComponent, bw);
        bw.write("The overall score is " + result.getScore() + ".");
        bw.newLine();
        bw.write("The score of the density metric is " + result.densityScore + ".");
        bw.newLine();
        bw.write("The score of the min-cut metric is " + result.minCutWeightScore + ".");
        bw.newLine();
    }

    private void writeStack(Graph<RootComponent, RootRelation> stack, BufferedWriter bw) throws IOException {
        bw.write("The stack contains the following components: ");
        bw.newLine();
        for (RootComponent component : stack.vertexSet()) {
            bw.write(component.getName() + " (" + component.getType().replace("_", " ") + ")");
            bw.newLine();
        }
        bw.newLine();

        bw.write("The components of the stack have the following relations:");
        bw.newLine();
        for (RootComponent component : stack.vertexSet()) {
            for (RootRelation relation : component.getRelations()) {
                bw.write(component.getName() + " " + relation.getName().replace("_", " ") + " " + relation.getTarget());
                bw.newLine();
            }
        }
        bw.newLine();
    }

    public Map<String, Double> getAllWeightedProbabilities(){
        HashMap<String, Double> probabilitiesMap = new HashMap<>();
        results.forEach((stackID, result) -> probabilitiesMap.put(stackID, result.getScore()));
        return probabilitiesMap;
    }
}
