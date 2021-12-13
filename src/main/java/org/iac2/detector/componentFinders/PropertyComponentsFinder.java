package org.iac2.detector.componentFinders;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertyComponentsFinder implements ComponentsFinder {
    final String propertyName;
    public PropertyComponentsFinder(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Find all components with the given property name.
     * @param deploymentModel Deployment model to use for the search.
     * @return Set of components with the given property.
     */
    public Map<String, Set<RootComponent>> find(final DeploymentModel deploymentModel) {
        Map<String, Set<RootComponent>> componentsSetMap = new HashMap();
        final Graph<RootComponent, RootRelation> topology = deploymentModel.getTopology();

        for (RootComponent component : topology.vertexSet()) {
            final String propertyValue = component.getProperty(this.propertyName).get().getValue();
            Set<RootComponent> componentsSet = componentsSetMap.get(propertyValue);
            if (componentsSet == null) {
                componentsSet = new HashSet<>();
                componentsSetMap.put(propertyValue, componentsSet);
            }
            componentsSet.add(component);
        }
        return componentsSetMap;
    }
}
