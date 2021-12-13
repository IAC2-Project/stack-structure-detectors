import org.iac2.detector.componentFinders.BusinessLogicServiceFinder;
import org.iac2.detector.componentFinders.PropertyStackFinder;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.iac2.detector.MicroStackDetector;
import org.iac2.detector.OutputMicroStackDetector;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicroStackDetectorTest {
    DeploymentModel model;
    String pathSimpleTest = "./src/test/TestResources/ServiceMicro_GraphSimpleTest_2Stack.yml";
    String pathAdvancedTest = "./src/test/TestResources/ServiceMicro_GraphAdvancedTest_3Stacks.yml";
    String pathOneServiceOnTwoStacks2Components = "./src/test/TestResources/ServiceMicro_GraphSimplestTest_2Components.yml";
    String pathOneServiceOnTwoStacks3Components = "./src/test/TestResources/ServiceMicro_GraphSimplestTest_3Components.yml";

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
    void calculateScoreTwoServicesWithTwoStacks() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        MicroStackDetector microStackDetector = new MicroStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputMicroStackDetector outputObjectMicroStack = microStackDetector.calculateScore(model);
        double ScoreMicroStack = Math.round(outputObjectMicroStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreMicroStack);
    }

    @Test
    void calculateScoreTwoServicesOnThreeStacks() {
        DeploymentModel model = loadModelFromFile(pathAdvancedTest);
        MicroStackDetector microStackDetector = new MicroStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputMicroStackDetector outputObjectMicroStack = microStackDetector.calculateScore(model);
        double ScoreMicroStack = Math.round(outputObjectMicroStack.getScore() * 100) / 100.0;
        assertEquals(1.0, ScoreMicroStack);
    }

    @Test
    void calculateScoreOneStackWithOneServiceAndAnotherComponent() {
        DeploymentModel model = loadModelFromFile(pathOneServiceOnTwoStacks3Components);
        MicroStackDetector microStackDetector = new MicroStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputMicroStackDetector outputObjectMicroStack = microStackDetector.calculateScore(model);
        double ScoreMicroStack = Math.round(outputObjectMicroStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreMicroStack);
    }

    @Test
    void calculateScoreOneServiceOnTwoStacksWithAnotherComponent() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        MicroStackDetector microStackDetector = new MicroStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputMicroStackDetector outputObjectMicroStack = microStackDetector.calculateScore(model);
        double ScoreMicroStack = Math.round(outputObjectMicroStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreMicroStack);
    }

    @Test
    void calculateScoreOneServiceOnTwoStacks() {
        DeploymentModel model = loadModelFromFile(pathOneServiceOnTwoStacks2Components);
        MicroStackDetector microStackDetector = new MicroStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputMicroStackDetector outputObjectMicroStack = microStackDetector.calculateScore(model);
        double ScoreMicroStack = Math.round(outputObjectMicroStack.getScore() * 100) / 100.0;
        assertEquals(1.0, ScoreMicroStack);
    }

}
