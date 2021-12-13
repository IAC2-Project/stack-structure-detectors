package org.iac2.detector;

import io.github.edmm.model.component.RootComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StackAnalyzer {
    private final Map<String, Set<RootComponent>> services;
    private final Map<String, Set<RootComponent>> stacks;

    /**
     *
     * @param services Services to be used for the analysis.
     * @param stacks Stacks to be used for the analysis.
     */
    public StackAnalyzer(final Map<String, Set<RootComponent>> services, Map<String, Set<RootComponent>> stacks) {
        this.services = services;
        this.stacks = stacks;
    }

    /**
     * Get number of micro stacks.
     * @return Number of micro stacks.
     */
    public int getNumberOfMicroStacks() {
        int numberOfMicroStacks = 0;
        for (Set<RootComponent> service : this.services.values()) {
            boolean serviceIsUnionOfMultipleStacks = isServiceUnionOfMultipleStacks(service, this.stacks);
            if (serviceIsUnionOfMultipleStacks) {
                numberOfMicroStacks++;
            }
        }
        return numberOfMicroStacks;
    }

    private boolean isServiceUnionOfMultipleStacks(Set<RootComponent> service, Map<String, Set<RootComponent>> stacks) {
        int numberOfStacks = 0;
        for (Set<RootComponent> stack : stacks.values()) {
            Set<RootComponent> intersection = new HashSet(stack);
            intersection.retainAll(service);
            if (stack.size() == intersection.size()) {
                numberOfStacks++;
            }
        }
        return numberOfStacks > 1;
    }

    /**
     * Get number of service stacks.
     * @return Number of service stacks.
     */
    public int getNumberOfServiceStacks() {
        int numberOfServiceStacks = 0;
        for (Set<RootComponent> service : this.services.values()) {
            for (Set<RootComponent> stack : this.stacks.values()) {
                boolean serviceEqualsStack = service.equals(stack);
                if (serviceEqualsStack) {
                    numberOfServiceStacks++;
                }
            }
        }
        return numberOfServiceStacks;
    }

}
