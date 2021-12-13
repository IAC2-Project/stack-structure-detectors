package org.iac2.detector.componentFinders;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.jgrapht.Graph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BusinessLogicServiceFinder implements ComponentsFinder {
    /**
     * Find all services by using the business logic property. A component belongs to the service if the business logic
     * depends on it.
     * @param deploymentModel Deployment model to use for the search.
     * @return Set of services defined via the business logic.
     */
    public Map<String, Set<RootComponent>> find(final DeploymentModel deploymentModel) {
        final Graph<RootComponent, RootRelation> topology = deploymentModel.getTopology();
        final Set<RootComponent> businessLogicComponents = getBusinessLogicComponents(topology.vertexSet());
        Map<String, Set<RootComponent>> servicesMap = new HashMap();

        for (RootComponent businessLogicComponent : businessLogicComponents) {
            String businessLogicName = businessLogicComponent.getName() + "Service";
            Set<RootComponent> service = getServiceFromBusinessLogicComponent(businessLogicComponent, topology);
            servicesMap.put(businessLogicName, service);
        }

        return servicesMap;
    }

    private Set<RootComponent> getBusinessLogicComponents(final Set<RootComponent> components) {
        Set<RootComponent> businessLogicComponents = new HashSet<>();
        for (RootComponent component : components) {
            if (isBusinessLogicComponent(component)) {
                businessLogicComponents.add(component);
            }
        }
        return businessLogicComponents;
    }

    private Set<RootComponent> getServiceFromBusinessLogicComponent(final RootComponent businessLogicComponent, final Graph<RootComponent, RootRelation> topology) {
        Set<RootComponent> service = new HashSet();
        BreadthFirstIterator bfsIterator = new BreadthFirstIterator(topology, businessLogicComponent);
        while (bfsIterator.hasNext()) {
            RootComponent component = (RootComponent) bfsIterator.next();
            service.add(component);
        }

        return service;
    }

    private static boolean isBusinessLogicComponent(final RootComponent component) {
        final String isBusinessLogicComponent = component.getProperty("isBusinessLogicComponent").get().getValue();
        return Boolean.parseBoolean(isBusinessLogicComponent);
    }

}
