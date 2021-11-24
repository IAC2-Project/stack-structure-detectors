package componentFinders;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyServiceFinder extends PropertyComponentsFinder {
    public PropertyServiceFinder() {
        super("serviceID");
    }
}
