package componentFinders;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyStackFinder extends PropertyComponentsFinder {
    public PropertyStackFinder() {
        super("stackID");
    }
}
