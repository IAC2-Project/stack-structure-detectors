import org.iac2.detector.componentFinders.BusinessLogicServiceFinder;
import org.iac2.detector.componentFinders.PropertyStackFinder;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.iac2.detector.OutputServiceStackDetector;
import org.iac2.detector.ServiceStackDetector;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceStackDetectorTest {
    DeploymentModel model;
    String pathSimpleTest = "./src/test/TestResources/ServiceMicro_GraphSimpleTest_1Stack.yml";
    String pathAdvancedTest2Stacks = "./src/test/TestResources/ServiceMicro_GraphAdvancedTest_2Stacks.yml";
    String pathAdvancedTest3Stacks = "./src/test/TestResources/ServiceMicro_GraphAdvancedTest_3Stacks.yml";
    String pathSimplestTest = "./src/test/TestResources/ServiceMicro_GraphSimplestTest_3Components.yml";

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
        DeploymentModel model = loadModelFromFile(pathAdvancedTest2Stacks);
        ServiceStackDetector serviceStackDetector = new ServiceStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputServiceStackDetector outputObjectServiceStack = serviceStackDetector.calculateScore(model);
        double ScoreServiceStack = Math.round(outputObjectServiceStack.getScore() * 100) / 100.0;
        assertEquals(0.5, ScoreServiceStack);
    }

    @Test
    void calculateScoreTwoServicesOnThreeStacks() {
        DeploymentModel model = loadModelFromFile(pathAdvancedTest3Stacks);
        ServiceStackDetector serviceStackDetector = new ServiceStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputServiceStackDetector outputObjectServiceStack = serviceStackDetector.calculateScore(model);
        double ScoreServiceStack = Math.round(outputObjectServiceStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreServiceStack);
    }

    @Test
    void calculateScoreOneStackWithOneServiceAndAnotherComponent() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        ServiceStackDetector serviceStackDetector = new ServiceStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputServiceStackDetector outputObjectServiceStack = serviceStackDetector.calculateScore(model);
        double ScoreServiceStack = Math.round(outputObjectServiceStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreServiceStack);
    }

    @Test
    void calculateScoreOneServiceOnTwoStacksWithAnotherComponent() {
        DeploymentModel model = loadModelFromFile(pathSimpleTest);
        ServiceStackDetector serviceStackDetector = new ServiceStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputServiceStackDetector outputObjectServiceStack = serviceStackDetector.calculateScore(model);
        double ScoreServiceStack = Math.round(outputObjectServiceStack.getScore() * 100) / 100.0;
        assertEquals(0.0, ScoreServiceStack);
    }

    @Test
    void calculateScoreOneServiceOnOneStacksWithAnotherComponent() {
        DeploymentModel model = loadModelFromFile(pathSimplestTest);
        ServiceStackDetector serviceStackDetector = new ServiceStackDetector(new PropertyStackFinder(), new BusinessLogicServiceFinder());
        OutputServiceStackDetector outputObjectServiceStack = serviceStackDetector.calculateScore(model);
        double ScoreServiceStack = Math.round(outputObjectServiceStack.getScore() * 100) / 100.0;
        assertEquals(1.0, ScoreServiceStack);
    }

}
