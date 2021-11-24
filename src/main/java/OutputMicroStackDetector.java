import io.github.edmm.model.component.RootComponent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class OutputMicroStackDetector implements DetectorOutputInterface {
    public Map<String, Set<RootComponent>> stacks;
    public Map<String, Set<RootComponent>> services;
    public final int numberOfMicroStacks;
    public final double weightedScore;

    public OutputMicroStackDetector(Map<String, Set<RootComponent>> stacks, Map<String, Set<RootComponent>> services, int numberOfMicroStacks, double weightedScore) {
        this.stacks = stacks;
        this.services = services;
        this.numberOfMicroStacks = numberOfMicroStacks;
        this.weightedScore = weightedScore;
    }

    public Map<String, Set<RootComponent>> getStacks() {
        return stacks;
    }

    public Map<String, Set<RootComponent>> getServices() {
        return services;
    }

    public int getNumberOfMicroStacks() {
        return numberOfMicroStacks;
    }

    @Override
    public double getScore() {
        return weightedScore;
    }

    /**
     * Write a report into the buffered writer object.
     * @param bw Target of the report.
     * @throws IOException
     */
    @Override
    public void write(BufferedWriter bw) throws IOException {
        bw.write("Micro Stack Detektor");
        bw.newLine();
        bw.write("The deployment model has an overall score of " + getScore() + ".");
        bw.newLine();
        bw.write("The overall number of micro stacks is " + getNumberOfMicroStacks() + ".");
        bw.newLine();
        bw.newLine();
        writeMapOfComponentsSet(getStacks(), bw, "stack");
        writeMapOfComponentsSet(getServices(), bw, "service");
    }

    private void writeMapOfComponentsSet(Map<String, Set<RootComponent>> stacks, BufferedWriter bw, String type) throws IOException {
        for (Map.Entry<String, Set<RootComponent>> entry : stacks.entrySet()) {
            bw.write("The " + type + " " + entry.getKey() + " has the following components:");
            bw.newLine();
            for (RootComponent component : entry.getValue()) {
                bw.write(component.getName() + " (" + component.getType() + ")");
                bw.newLine();
            }
            bw.newLine();
        }
    }
}
