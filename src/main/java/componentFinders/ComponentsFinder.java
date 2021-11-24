package componentFinders;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import java.util.Map;
import java.util.Set;

public interface ComponentsFinder {
    public Map<String, Set<RootComponent>> find(final DeploymentModel deploymentModel);
}
