import componentFinders.BusinessLogicServiceFinder;
import componentFinders.ComponentsFinder;
import componentFinders.PropertyServiceFinder;
import componentFinders.PropertyStackFinder;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.apache.commons.cli.*;

import java.io.*;

public class Detector {

    private static final String INPUT_OPTION = "input";
    private static final String OUTPUT_OPTION = "output";
    private static final String HELP_OPTION = "help";
    private static final String MIN_VERTICES_OPTION = "min_vertices";
    private static final String MIN_EDGES_OPTION = "min_edges";
    private static final String SERVICE_FINDER_OPTION = "service_finder";

    private static final String DETECTOR_MONOLITH = "monolith";
    private static final String DETECTOR_MICRO = "micro";
    private static final String DETECTOR_SERVICE = "service";

    private static final String SERVICE_FINDER_BUSINESS_LOGIC = "business_logic";
    private static final String SERVICE_FINDER_PROPERTY = "property";

    /**
     * Application for running the different detectors.
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        if (args.length <= 1) {
            printHelp("");
            System.exit(1);
        }

        final String detectorName = args[0];
        CommandLine cmd = parseArguments(detectorName, args);
        String inputFilename = cmd.getOptionValue(INPUT_OPTION);
        String outputFilename = cmd.getOptionValue(OUTPUT_OPTION);

        DetectorInterface detector = createDetector(detectorName, cmd);
        final DeploymentModel deploymentModel = getDeploymentModelFromFile(inputFilename);
        DetectorOutputInterface output = detector.calculateScore(deploymentModel);
        writeResultsToFile(outputFilename, output);
    }

    private static DetectorInterface createDetector(String detectorName, CommandLine cmd) {
        PropertyStackFinder stackFinder = new PropertyStackFinder();
        BusinessLogicServiceFinder serviceFinder = new BusinessLogicServiceFinder();

        switch (detectorName) {
            case DETECTOR_MONOLITH:
                final int minVertices = Integer.parseInt(cmd.getOptionValue(MIN_VERTICES_OPTION));
                final int minEdges = Integer.parseInt(cmd.getOptionValue(MIN_EDGES_OPTION));

                return new MonolithicStackDetector(minVertices, minEdges, stackFinder);

            case DETECTOR_MICRO:
                return new MicroStackDetector(stackFinder, createServiceFinder(cmd));

            case DETECTOR_SERVICE:
                return new ServiceStackDetector(stackFinder, createServiceFinder(cmd));

            default:
                System.out.println("Please provide a valid detector name. It must be one of [" +
                        DETECTOR_MONOLITH + ", " +
                        DETECTOR_MICRO + ", " +
                        DETECTOR_SERVICE + "]");
                System.exit(1);
        }

        return null;
    }

    private static ComponentsFinder createServiceFinder(CommandLine cmd) {
        switch (cmd.getOptionValue(SERVICE_FINDER_OPTION, SERVICE_FINDER_BUSINESS_LOGIC)) {
            case SERVICE_FINDER_BUSINESS_LOGIC:
                return new BusinessLogicServiceFinder();

            case SERVICE_FINDER_PROPERTY:
                return new PropertyServiceFinder();

            default:
                System.out.println("Please provide a valid service finder name. It must be one of [" +
                        SERVICE_FINDER_BUSINESS_LOGIC + ", " +
                        SERVICE_FINDER_PROPERTY + "]");
                System.exit(1);
        }

        return null;
    }

    private static CommandLine parseArguments(final String detectorName, String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions(detectorName);
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption(HELP_OPTION)) {
                printHelp(detectorName);
                System.exit(0);
            }
        } catch (ParseException e) {
            printHelp(detectorName);
            System.exit(1);
        }

        return cmd;
    }

    private static Options getOptions(String detectorName) {
        Options options = new Options();

        Option inputOption = new Option("i", INPUT_OPTION, true, "Filename of the EDMM yml with the deployment model.");
        inputOption.setRequired(true);
        options.addOption(inputOption);

        Option outputOption = new Option("o", OUTPUT_OPTION, true, "Output filename for detector results");
        options.addOption(outputOption);
        outputOption.setRequired(true);

        options.addOption("h", HELP_OPTION, false, "Show the help message");

        switch (detectorName) {
            case DETECTOR_MONOLITH:
                addMonolithDetectorOptions(options);
                break;

            case DETECTOR_MICRO:
            case DETECTOR_SERVICE:
                addServiceFinderOptions(options);
                break;
        }

        return options;
    }

    private static void addMonolithDetectorOptions(Options options) {
        Option verticesOption = new Option("v", MIN_VERTICES_OPTION, true, "Minimal number of vertices to be a monolithic stack.");
        verticesOption.setRequired(true);
        options.addOption(verticesOption);

        Option edgesOption = new Option("e", MIN_EDGES_OPTION, true, "Minimal number of edges to be a monolithic stack.");
        edgesOption.setRequired(true);
        options.addOption(edgesOption);
    }

    private static void addServiceFinderOptions(Options options) {
        Option serviceFinderOption = new Option("s", SERVICE_FINDER_OPTION, true, "Selection of the service finder [business_logic, property].");
        options.addOption(serviceFinderOption);
    }

    private static void printHelp(String detectorName) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Detector [detectorName]", getOptions(detectorName));
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

    private static void writeResultsToFile(final String filename, final DetectorOutputInterface output) {
        try {
            File outputFile = new File(filename);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            output.write(bw);
            bw.close();
        } catch (IOException e) {
            System.out.println("Failed to write output into " + filename);
            System.exit(1);
        }
    }
}
