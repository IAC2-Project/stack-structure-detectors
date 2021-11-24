import componentFinders.ComponentsFinder;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import java.util.Map;
import java.util.Set;

public class ServiceStackDetector implements DetectorInterface {
    private final ComponentsFinder stackFinder;
    private final ComponentsFinder serviceFinder;

    public ServiceStackDetector(ComponentsFinder stackFinder, ComponentsFinder serviceFinder) {
        this.stackFinder = stackFinder;
        this.serviceFinder = serviceFinder;
    }

    /**
     * Run detector and calculate the score whether service stack pattern was followed.
     * @param deploymentModel Deployment model to run the detector on.
     * @return Return the computed score.
     */
    @Override
    public OutputServiceStackDetector calculateScore(DeploymentModel deploymentModel) {
        Map<String, Set<RootComponent>> stacks = this.stackFinder.find(deploymentModel);
        Map<String, Set<RootComponent>> services = this.serviceFinder.find(deploymentModel);

        StackAnalyzer stackAnalyzer = new StackAnalyzer(services, stacks);
        int numberOfServiceStacks = stackAnalyzer.getNumberOfServiceStacks();
        double Score = getScore(numberOfServiceStacks, services);
        OutputServiceStackDetector outputServiceStack = new OutputServiceStackDetector(stacks, services, numberOfServiceStacks, Score);
        return outputServiceStack;
    }

    private double getScore(int numberOfServiceStacks, Map<String, Set<RootComponent>> services) {
        return ((double) numberOfServiceStacks / services.size());
    }
}
