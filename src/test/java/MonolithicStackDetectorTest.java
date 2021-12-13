import org.iac2.detector.componentFinders.PropertyStackFinder;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.iac2.detector.MonolithicStackDetector;
import org.iac2.detector.OutputMonolithicStackDetector;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MonolithicStackDetectorTest {
    DeploymentModel model;
    String pathTwoConnComponents = "./src/test/TestResources/Monolith_GraphWithTwoConnectedComponents.yml";
    String pathSimpleTest = "./src/test/TestResources/Monolith_GraphSimpleTest.yml";
    String pathTwoConnComponentsSameNumberVertices = "./src/test/TestResources/Monolith_GraphWithTwoConnectedComponentsWithSameNumberOfVertices.yml";
    String petClinic2Stacks = "./src/test/TestResources/scenario_iaas.yml";

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
    void ScoreSimpleTest() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        MonolithicStackDetector test = new MonolithicStackDetector(0, 0, new PropertyStackFinder());
        assertNotNull(test.calculateScore(model));
        double ScoreMonolithicStack = test.calculateScore(model).getScore();
        assertEquals(0.666, ScoreMonolithicStack, 1e-3);
    }

    @Test
    void ScoreGraphTooSmallBecauseOfVertices() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        MonolithicStackDetector test = new MonolithicStackDetector(5, 2, new PropertyStackFinder());
        assertNotNull(test.calculateScore(model));
        double ScoreMonolithicStack = test.calculateScore(model).getScore();
        assertEquals(0.0, ScoreMonolithicStack, 1e-3);
    }

    @Test
    void ScoreGraphTooSmallBecauseOfRelations() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponents);
        MonolithicStackDetector test = new MonolithicStackDetector(3, 4, new PropertyStackFinder());
        assertNotNull(test.calculateScore(model));
        double ScoreMonolithicStack = test.calculateScore(model).getScore();
        assertEquals(0.0, ScoreMonolithicStack, 1e-3);
    }

    @Test
    void ScoreTwoConnComponents() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponents);
        MonolithicStackDetector monolithicStackDetector = new MonolithicStackDetector(0, 0, new PropertyStackFinder());
        OutputMonolithicStackDetector outputObject = monolithicStackDetector.calculateScore(model);
        assertNotNull(outputObject);
        double ScoreMonolithicStack = outputObject.getScore();
        assertEquals(0.833, ScoreMonolithicStack, 1e-3);
    }

    @Test
    void ScoreTwoConnComponentsSameNumberOfVertices() {
        DeploymentModel model = loadModelFromFile(pathTwoConnComponentsSameNumberVertices);
        MonolithicStackDetector test = new MonolithicStackDetector(0, 0, new PropertyStackFinder());
        assertNotNull(test.calculateScore(model));
        double ScoreMonolithicStack = test.calculateScore(model).getScore();
        assertEquals(0.75, ScoreMonolithicStack, 1e-3);
    }

    @Test
    void ScorePetClinicTwoStacks() {
        DeploymentModel model = loadModelFromFile(petClinic2Stacks);
        MonolithicStackDetector test = new MonolithicStackDetector(2, 1, new PropertyStackFinder());
        assertNotNull(test.calculateScore(model));
        double ScoreMonolithicStack = test.calculateScore(model).getScore();
        assertEquals(0.833, ScoreMonolithicStack, 1e-3);
    }

    /*
    @Test
    void minCutWeightScoreInput0() {
        assertEquals(0,
            org.iac2.detector.MonolithicStackDetector.getMinCutWeightScore(0.0));
    }

    @Test
    void minCutWeightScoreInput1() {
        assertEquals(1,
            org.iac2.detector.MonolithicStackDetector.getMinCutWeightScore(1.0));
    }

    @Test
    void minCutWeightScoreInput3() {
        assertEquals(0.75,
            org.iac2.detector.MonolithicStackDetector.getMinCutWeightScore(3.0));
    }

    @Test
    void minCutWeightScoreInputMinus1() {
        assertEquals(0,
            org.iac2.detector.MonolithicStackDetector.getMinCutWeightScore(-1.0));
    }
    */
    /*
    @Test
    void densityScore1() {
        double densityScore = Math.round(org.iac2.detector.MonolithicStackDetector.getDensityScore(1.0) * 100) / 100.0;
        assertEquals(1.0, densityScore);
    }

    @Test
    void densityScoreEinhalb() {
        double densityScore = Math.round(org.iac2.detector.MonolithicStackDetector.getDensityScore(0.5) * 100) / 100.0;
        assertEquals(0.5, densityScore);
    }

     */
}
