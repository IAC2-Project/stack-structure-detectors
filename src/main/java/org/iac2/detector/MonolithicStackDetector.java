package org.iac2.detector;

import org.iac2.detector.componentFinders.ComponentsFinder;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MonolithicStackDetector implements DetectorInterface {
    private final int minNumberEdges;
    private final int minNumberVertices;
    private final ComponentsFinder stackFinder;

    public MonolithicStackDetector(final int minNumberVertices, final int minNumberEdges, final ComponentsFinder stackFinder) {
        this.minNumberEdges = minNumberEdges;
        this.minNumberVertices = minNumberVertices;
        this.stackFinder = stackFinder;
    }

    /**
     * Run detector and calculate the score whether monolithic stack pattern was followed.
     * @param deploymentModel Deployment model to run the detector on.
     * @return Return the computed score.
     */
    @Override
    public OutputMonolithicStackDetector calculateScore(DeploymentModel deploymentModel) {
        Map<String, Set<RootComponent>> stackComponents = this.stackFinder.find(deploymentModel);
        Map<String, Graph<RootComponent, RootRelation>> stacks = GraphAnalyzer.getStackTopologies(stackComponents, deploymentModel);
        Map<String, OutputMonolithicStackDetector.StackResult> stackResults = new HashMap<>();

        for (Map.Entry<String, Graph<RootComponent, RootRelation>> pair : stacks.entrySet()) {
            String stackID = pair.getKey();
            Graph<RootComponent, RootRelation> stackTopology = pair.getValue();
            OutputMonolithicStackDetector.StackResult stackResult = calculateStackResult(stackTopology, deploymentModel);
            stackResults.put(stackID, stackResult);
        }
        return new OutputMonolithicStackDetector(stackResults);
    }

    private OutputMonolithicStackDetector.StackResult calculateStackResult(Graph<RootComponent, RootRelation> stackTopology, DeploymentModel deploymentModel) {
        if (isGraphLargerThanMinSize(stackTopology)) {
            Set<Graph<RootComponent, RootRelation>> connectComponents = GraphAnalyzer.getConnectedComponents(stackTopology, deploymentModel);
            boolean isPseudoMonolith = GraphAnalyzer.isPseudoMonolith(connectComponents);
            Graph<RootComponent, RootRelation> largestConnectedComponent = GraphAnalyzer.getLargestGraph(connectComponents);

            double minCutWeight = GraphAnalyzer.getMinCutWeight(largestConnectedComponent);
            double minCutWeightScore = getMinCutWeightScore(minCutWeight);

            double density = GraphAnalyzer.getDensity(largestConnectedComponent);
            double densityScore = getDensityScore(density);

            double weightedScore = getCombinedScore(minCutWeightScore, densityScore);

            return new OutputMonolithicStackDetector.StackResult(isPseudoMonolith, largestConnectedComponent, minCutWeightScore, densityScore, weightedScore);
        }

        // Stack is too small
        return new OutputMonolithicStackDetector.StackResult(false, null, 0, 0, 0);
    }

    private boolean isGraphLargerThanMinSize(Graph<RootComponent, RootRelation> stack) {
        boolean hasMinimumEdges = this.minNumberEdges < stack.edgeSet().size();
        boolean hasMinimumVertices = this.minNumberVertices < stack.vertexSet().size();
        return hasMinimumEdges && hasMinimumVertices;
    }

    private double getMinCutWeightScore(double minCutWeight) {
        // Experiments showed good performance with the following function
        double minCutWeightScore;
        minCutWeightScore = Math.max(0, Math.min(((4/3.0) - minCutWeight / 3.0), 1));
        return minCutWeightScore;
    }

    private double getDensityScore(double density) {
        // Explanation:
        // In directed acyclic graphs we can reach at most a density of 0.5.
        // Therefore the density is mapped backed to the range [0, 1].
        return 2 * density;
    }

    private double getCombinedScore(double minCutWeightScore, double densityScore) {
        return (minCutWeightScore + densityScore) / 2.0;
    }
}
