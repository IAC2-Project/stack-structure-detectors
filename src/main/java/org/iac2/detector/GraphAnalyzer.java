package org.iac2.detector;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class GraphAnalyzer {
    /**
     * Determine the graph with the most vertices and edges.
     * Note: If there are multiple of the same size, then the first one found of this size will be returned.
     * @param graphs Set of graphs
     * @return Graph with the most vertices and edges.
     */
    public static Graph<RootComponent, RootRelation> getLargestGraph(Set<Graph<RootComponent, RootRelation>> graphs) {
        Graph<RootComponent, RootRelation> largestGraph = graphs.iterator().next();
        for (Graph<RootComponent, RootRelation> graph : graphs) {
            boolean hasMoreVerticesThanLargestGraph = graph.vertexSet().size() >= largestGraph.vertexSet().size();
            boolean hasMoreEdgesThanLargestGraph = graph.edgeSet().size() >= largestGraph.edgeSet().size();
            if (hasMoreVerticesThanLargestGraph) {
                if(hasMoreEdgesThanLargestGraph) {
                    largestGraph = graph;
                }
            }
        }
        return largestGraph;
    }

    /**
     * Calculate the weight of the minimal cut.
     * @param graph Graph to be used for calculation.
     * @return The weight of the minimal cut.
     */
    public static double getMinCutWeight(Graph<RootComponent, RootRelation> graph) {
        AsUndirectedGraph undirectedGraph = new AsUndirectedGraph(graph);
        StoerWagnerMinimumCut minimumCut = new StoerWagnerMinimumCut(undirectedGraph);
        return minimumCut.minCutWeight();
    }

    /**
     * Determine if the graph is a pseudo monolith.
     * Note: A graph is a pseudo monolith if it consists of more than 1 connected component.
     * @param graphs All connected components of a graph.
     * @return true if the graph is a pseudo monolith
     */
    public static boolean isPseudoMonolith(Set<Graph<RootComponent, RootRelation>> graphs) {
        boolean pseudoMonolith = graphs.size() > 1;
        return pseudoMonolith;
    }

    /**
     * Calculate all connected components of the given stack.
     * @param stack Components of the stack.
     * @param deploymentModel Deployment model containing all the components
     * @return Set of all connected components
     */
    public static Set<Graph<RootComponent, RootRelation>> getConnectedComponents(Graph<RootComponent, RootRelation> stack, DeploymentModel deploymentModel) {
        ConnectivityInspector connectedComponentsInspector = new ConnectivityInspector(stack);
        List<Set<RootComponent>> connectedComponentsVertices = connectedComponentsInspector.connectedSets();
        Set<Graph<RootComponent, RootRelation>> connectedComponents = getInducedSubgraphs(connectedComponentsVertices, deploymentModel);
        return connectedComponents;
    }

    /**
     * Calculate the topologies of the given components of the stacks from the deployment model.
     * @param stackComponents Components of the stacks
     * @param deploymentModel Deployment model containing all the components
     * @return Topologies of each stack.
     */
    public static HashMap<String, Graph<RootComponent, RootRelation>> getStackTopologies(Map<String, Set<RootComponent>> stackComponents, DeploymentModel deploymentModel) {
        HashMap<String, Graph<RootComponent, RootRelation>> stacksFromStackComponents = new HashMap<>();
        for (Map.Entry<String, Set<RootComponent>> componentsFromOneStack : stackComponents.entrySet()) {
            Graph<RootComponent, RootRelation> stack = getInducedSubgraph(componentsFromOneStack.getValue(), deploymentModel);
            stacksFromStackComponents.put(componentsFromOneStack.getKey(), stack);
        }

        return stacksFromStackComponents;
    }

    private static Graph<RootComponent, RootRelation> getInducedSubgraph(Set<RootComponent> rootComponents, DeploymentModel deploymentModel) {
        Graph<RootComponent, RootRelation> topology = new DirectedMultigraph<>(RootRelation.class);

        // add all components as vertices
        for (RootComponent component : rootComponents) {
            topology.addVertex(component);
        }

        // read all edges to topology from deploymentModel
        for (RootComponent source : rootComponents) {
            for (RootComponent target : rootComponents) {
                for (RootRelation edge : deploymentModel.getTopology().getAllEdges(source, target)) {
                    topology.addEdge(source, target, edge);
                }
            }
        }

        return topology;
    }

    private static Set<Graph<RootComponent, RootRelation>> getInducedSubgraphs(List<Set<RootComponent>> listOfSubgraphs, DeploymentModel sourceDeploymentModel) {
        Set<Graph<RootComponent, RootRelation>> topologySet = new HashSet<>();
        for (Set<RootComponent> subgraph : listOfSubgraphs) {
            topologySet.add(getInducedSubgraph(subgraph, sourceDeploymentModel));
        }

        return topologySet;
    }
    /*
    public static boolean isStronglyConnected(Graph<RootComponent, RootRelation> graph) {
        KosarajuStrongConnectivityInspector connectivityInspector = new KosarajuStrongConnectivityInspector(graph);
        //List<Graph<RootComponent, RootRelation>> stronglyConnectedComponents = connectivityInspector.stronglyConnectedSets();
        return connectivityInspector.isStronglyConnected();
    }
     */

    /**
     * Calculate the density of the given graph.
     * @param graph Graph on which the density is to be calculated.
     * @return The density of the graph.
     */
    public static double getDensity(Graph<RootComponent, RootRelation> graph) {
        double numEdges = getNumberOfEdges(graph);
        double numVertices = graph.vertexSet().size();
        return numEdges / (numVertices * (numVertices - 1));
    }

    private static int getNumberOfEdges(Graph<RootComponent, RootRelation> graph) {
        int numberOfEdges = 0;
        for (RootComponent source : graph.vertexSet()) {
            for (RootComponent target : graph.vertexSet()) {
                if (graph.getAllEdges(source, target).size() > 0) {
                    numberOfEdges += 1;
                }
            }
        }
        return numberOfEdges;
    }
}
