import componentFinders.ComponentsFinder;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import java.util.Map;
import java.util.Set;

public class MicroStackDetector implements DetectorInterface {
    private final ComponentsFinder stackFinder;
    private final ComponentsFinder serviceFinder;

    public MicroStackDetector(ComponentsFinder stackFinder, ComponentsFinder serviceFinder) {
        this.stackFinder = stackFinder;
        this.serviceFinder = serviceFinder;
    }

    /**
     * Run detector and calculate the score whether micro stack pattern was followed.
     * @param deploymentModel Deployment model to run the detector on.
     * @return Return the computed score.
     */
    @Override
    public OutputMicroStackDetector calculateScore(DeploymentModel deploymentModel) {
        Map<String, Set<RootComponent>> stacks = this.stackFinder.find(deploymentModel);
        Map<String, Set<RootComponent>> services = this.serviceFinder.find(deploymentModel);

        StackAnalyzer stackAnalyzer = new StackAnalyzer(services, stacks);
        int numberOfMicroStacks = stackAnalyzer.getNumberOfMicroStacks();
        double Score = getScore(numberOfMicroStacks, services);
        OutputMicroStackDetector outputMicroStack = new OutputMicroStackDetector(stacks, services, numberOfMicroStacks, Score);
        return outputMicroStack;
    }

    private double getScore(int numberOfMicroStacks, Map<String, Set<RootComponent>> services) {
        return ((double) numberOfMicroStacks / services.values().size());
    }
}
