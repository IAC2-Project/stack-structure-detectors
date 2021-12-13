package org.iac2.detector;

import org.iac2.detector.componentFinders.BusinessLogicServiceFinder;
import org.iac2.detector.componentFinders.ComponentsFinder;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class ServiceFinder {
    private static final String INPUT_OPTION = "input";
    private static final String OUTPUT_OPTION = "output";
    private static final String HELP_OPTION = "help";

    /**
     * Application for finding the services with the business logic service finder and storing the found services
     * in a copy of the EDDM yml file.
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        CommandLine cmd = parseArguments(args);
        String inputFilename = cmd.getOptionValue(INPUT_OPTION);
        System.out.println(inputFilename);
        String outputFilename = cmd.getOptionValue(OUTPUT_OPTION);

        final DeploymentModel deploymentModel = getDeploymentModelFromFile(inputFilename);
        final ComponentsFinder serviceFinder = new BusinessLogicServiceFinder();
        addServicesToDeploymentModel(serviceFinder, deploymentModel);
        saveDeploymentModelToFile(outputFilename, deploymentModel);
    }

    private static void addServicesToDeploymentModel(final ComponentsFinder serviceFinder, DeploymentModel deploymentModel) {
        Map<String, Set<RootComponent>> servicesMap = serviceFinder.find(deploymentModel);
        for (Map.Entry<String, Set<RootComponent>> entry : servicesMap.entrySet()) {
            String serviceID = entry.getKey();
            Set<RootComponent> service = entry.getValue();
            addServiceProperty(serviceID, service);
        }
    }

    private static void addServiceProperty(String serviceID, Set<RootComponent> service) {
        for (RootComponent component : service) {
            component.addProperty("serviceID", serviceID);
        }
    }

    private static CommandLine parseArguments(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption(HELP_OPTION)) {
                printHelp();
                System.exit(0);
            }
        } catch (ParseException e) {
            printHelp();
            System.exit(1);
        }

        return cmd;
    }

    private static Options getOptions() {
        Options options = new Options();

        Option inputOption = new Option("i", INPUT_OPTION, true, "Filename of the EDMM yml with the deployment model.");
        inputOption.setRequired(true);
        options.addOption(inputOption);

        Option outputOption = new Option("o", OUTPUT_OPTION, true, "Output filename for detector results");
        options.addOption(outputOption);
        outputOption.setRequired(true);

        options.addOption("h", HELP_OPTION, false, "Show the help message");

        return options;
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("org.iac2.detector.ServiceFinder", getOptions());
    }

    private static DeploymentModel getDeploymentModelFromFile(final String filename) {
        DeploymentModel deploymentModel = null;
        try {
            InputStream inputstream = new FileInputStream(filename);
            EntityGraph entityGraph = new EntityGraph(inputstream);

            deploymentModel = new DeploymentModel("DeploymentModel", entityGraph);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not load the EDMM file " + filename);
            System.exit(1);
        }

        return deploymentModel;
    }

    private static void saveDeploymentModelToFile(String outputFilename, DeploymentModel deploymentModel) {
        try {
            File outputFile = new File(outputFilename);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            deploymentModel.getGraph().generateYamlOutput(bw);
            bw.close();
        } catch (IOException e) {
            System.out.println("Failed to write edmm yml file into " + outputFilename);
            System.exit(1);
        }
    }
}
