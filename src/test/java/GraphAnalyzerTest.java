import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphAnalyzerTest {
    DeploymentModel model;
    String pathTwoConnComponents = "./src/test/TestResources/Monolith_GraphWithTwoConnectedComponents.yml";
    String pathSimpleTest = "./src/test/TestResources/Monolith_GraphSimpleTest.yml";
    String getPathTwoConnComponentsSameNumberVertices = "./src/test/TestResources/Monolith_GraphWithTwoConnectedComponentsWithSameNumberOfVertices.yml";

    private DeploymentModel loadModelFromFile(String filepath) {
        try {
            InputStream inputstream = new FileInputStream(filepath);
            EntityGraph entityGraph = new EntityGraph(inputstream);
            model = new DeploymentModel("My fancy deployment model", entityGraph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return model;
    }

    @Test
    void getConnectedComponentsTwo() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponents);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        assertEquals(2, connectedComponents.size());
    }

    @Test
    void getConnectedComponentsOne() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        assertEquals(1, connectedComponents.size());
    }

    @Test
    void getLargestGraphTwoConnComponentsWithSameNumberOfVertices() {
        DeploymentModel model = loadModelFromFile(getPathTwoConnComponentsSameNumberVertices);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        int numberOfComponents = largestGraph.vertexSet().size();
        int numberOfRelations = largestGraph.edgeSet().size();
        assertEquals(4, numberOfComponents);
        assertEquals(5, numberOfRelations);
    }

    @Test
    void getLargestGraphOneComponent() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        assertEquals(model.getTopology(), largestGraph);
    }

    @Test
    void getMinCutWeightEquals3() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        double minCutWeight = GraphAnalyzer.getMinCutWeight(largestGraph);
        assertEquals(3.0, minCutWeight);
    }

    @Test
    void getMinCutWeightEquals2() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponents);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        double minCutWeight = GraphAnalyzer.getMinCutWeight(largestGraph);
        assertEquals(2.0, minCutWeight);
    }

    @Test
    void isPseudoMonolithTrue() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponents);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        assertTrue(GraphAnalyzer.isPseudoMonolith(connectedComponents));
    }

    @Test
    void isPseudoMonolithFalse() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        assertFalse(GraphAnalyzer.isPseudoMonolith(connectedComponents));
    }

   @Test
    void getDensitySimpleTest() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        double density = Math.round(GraphAnalyzer.getDensity(largestGraph) * 100) / 100.0;
        assertEquals(0.5, density);
    }
    @Test
    void getDensityTwoConnCompWithSameNumberOfVertices() {
        DeploymentModel model = loadModelFromFile(getPathTwoConnComponentsSameNumberVertices);
        Set<Graph<RootComponent, RootRelation>> connectedComponents = GraphAnalyzer.getConnectedComponents(model.getTopology(), model);
        Graph<RootComponent, RootRelation> largestGraph = GraphAnalyzer.getLargestGraph(connectedComponents);
        double density = Math.round(GraphAnalyzer.getDensity(largestGraph) * 100) / 100.0;
        assertEquals(0.42, density);
    }
}
