import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

public class SimManager {

    private static Random randGenerator;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 60;

    private Integer regionWidth;
    private Integer regionHeight;
    private Integer[][] regionInfo;
    private Integer numberOfDrones;
    private Integer droneX[], droneY[];
    private String droneDirection[];
    private Integer droneStrategy[];
    private Integer droneStatus[];
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    private String trackAction;
    private String trackNewDirection;
    private Integer trackThrustDistance;
    private String trackMoveCheck;
    private String trackScanResults;

    private Integer turnLimit;

    private final int EMPTY_CODE = 0;
    private final int STARS_CODE = 1;
    private final int SUN_CODE = 2;

    private final int MAX_DRONES = 2;
    private final int OK_CODE = 1;
    private final int CRASH_CODE = -1;

    private final String[] ORIENT_LIST = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};

    public SimManager() {
        randGenerator = new Random();

        regionHeight = 0;
        regionWidth = 0;
        regionInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];

        numberOfDrones = -1;
        droneX = new Integer[MAX_DRONES];
        droneY = new Integer[MAX_DRONES];
        droneDirection = new String[MAX_DRONES];
        droneStrategy = new Integer[MAX_DRONES];
        droneStatus = new Integer[MAX_DRONES];

        for(int k = 0; k < MAX_DRONES; k++) {
            droneX[k] = -1;
            droneY[k] = -1;
            droneDirection[k] = "north";
            droneStrategy[k] = -1;
            droneStatus[k] = CRASH_CODE;
        }

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("north", 0);
        xDIR_MAP.put("northeast", 1);
        xDIR_MAP.put("east", 1);
        xDIR_MAP.put("southeast", 1);
        xDIR_MAP.put("south", 0);
        xDIR_MAP.put("southwest", -1);
        xDIR_MAP.put("west", -1);
        xDIR_MAP.put("northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("north", 1);
        yDIR_MAP.put("northeast", 1);
        yDIR_MAP.put("east", 0);
        yDIR_MAP.put("southeast", -1);
        yDIR_MAP.put("south", -1);
        yDIR_MAP.put("southwest", -1);
        yDIR_MAP.put("west", 0);
        yDIR_MAP.put("northwest", 1);

        turnLimit = -1;
    }

    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the region information
            tokens = takeCommand.nextLine().split(DELIMITER);
            regionWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            regionHeight = Integer.parseInt(tokens[0]);

            // generate the region information
            regionInfo = new Integer[regionWidth][regionHeight];
            for (i = 0; i < regionWidth; i++) {
                for (j = 0; j < regionHeight; j++) {
                    regionInfo[i][j] = STARS_CODE;
                }
            }

            // read in the drone starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            numberOfDrones = Integer.parseInt(tokens[0]);
            for (k = 0; k < numberOfDrones; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                droneX[k] = Integer.parseInt(tokens[0]);
                droneY[k] = Integer.parseInt(tokens[1]);
                droneDirection[k] = tokens[2];
                droneStrategy[k] = Integer.parseInt(tokens[3]);
                droneStatus[k] = OK_CODE;

                // explore the stars at the initial location
                regionInfo[droneX[k]][droneY[k]] = EMPTY_CODE;
            }

            // read in the sun information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numSuns = Integer.parseInt(tokens[0]);
            for (k = 0; k < numSuns; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a sun at the given location
                regionInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = SUN_CODE;
            }

            tokens = takeCommand.nextLine().split(DELIMITER);
            turnLimit = Integer.parseInt(tokens[0]);

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public Integer simulationDuration() {
        return turnLimit;
    }

    public Integer droneCount() {
        return numberOfDrones;
    }

    public void pollDroneForAction(int id) {
        int moveRandomChoice, thrustRandomChoice, steerRandomChoice;

        if (droneStrategy[id] == 2) {
            Scanner askUser = new Scanner(System.in);
            // generate a move by asking the user - DIAGNOSTIC ONLY
            System.out.print("action?: ");
            trackAction = askUser.nextLine();

            if (trackAction.equals("steer")) {
                System.out.print("direction?: ");
                trackNewDirection = askUser.nextLine();
            } else if (trackAction.equals("thrust")) {
                System.out.print("distance?: ");
                trackThrustDistance = Integer.parseInt(askUser.nextLine());
            }

        } else {
            // generate a move randomly
            moveRandomChoice = randGenerator.nextInt(100);
            if (moveRandomChoice < 5) {
                // do nothing
                trackAction = "pass";
            } else if (moveRandomChoice < 20) {
                // check your surroundings
                trackAction = "scan";
            } else if (moveRandomChoice < 50) {
                // change direction
                trackAction = "steer";
            } else {
                // thrust forward
                trackAction = "thrust";
                thrustRandomChoice = randGenerator.nextInt(3);
                trackThrustDistance = thrustRandomChoice + 1;
            }

            // determine a new direction
            steerRandomChoice = randGenerator.nextInt(8);
            if (trackAction.equals("steer")) {
                trackNewDirection = ORIENT_LIST[steerRandomChoice];
            }
        }
    }

    public void validateDroneAction(int id) {
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            trackScanResults = scanAroundSquare(droneX[id], droneY[id]);
            trackMoveCheck = "ok";

        } else if (trackAction.equals("pass")) {
            trackMoveCheck = "ok";

        } else if (trackAction.equals("steer")) {
            droneDirection[id] = trackNewDirection;
            trackMoveCheck = "ok";

        } else if (trackAction.equals("thrust")) {
            // in the case of a thrust, ensure that the move doesn't cross suns or barriers
            xOrientation = xDIR_MAP.get(droneDirection[id]);
            yOrientation = yDIR_MAP.get(droneDirection[id]);

            trackMoveCheck = "ok";
            int remainingThrust = trackThrustDistance;

            while (remainingThrust > 0 && trackMoveCheck.equals("ok")) {

                int newSquareX = droneX[id] + xOrientation;
                int newSquareY = droneY[id] + yOrientation;

                if (newSquareX < 0 || newSquareX >= regionWidth || newSquareY < 0 || newSquareY >= regionHeight) {
                    // drone hit a barrier and simply doesn't move (do nothing)

                } else if (regionInfo[newSquareX][newSquareY] == SUN_CODE) {
                    // drone hit a sun
                    droneStatus[id] = CRASH_CODE;
                    trackMoveCheck = "crash";

                } else if (newSquareX == droneX[1 - id] && newSquareY == droneY[1 - id]) {
                    // drone collided with the other drone
                    droneStatus[id] = CRASH_CODE;
                    droneStatus[1 - id] = CRASH_CODE;
                    trackMoveCheck = "crash";

                } else {
                    // drone thrust is successful
                    droneX[id] = newSquareX;
                    droneY[id] = newSquareY;
                    // update region status
                    regionInfo[newSquareX][newSquareY] = EMPTY_CODE;
                }

                remainingThrust = remainingThrust - 1;
            }

        } else {
            // in the case of an unknown action, treat the action as a pass
            trackMoveCheck = "action_not_recognized";
        }
    }

    public String scanAroundSquare(int targetX, int targetY) {
        String nextSquare, resultString = "";

        for (int k = 0; k < ORIENT_LIST.length; k++) {
            String lookThisWay = ORIENT_LIST[k];
            int offsetX = xDIR_MAP.get(lookThisWay);
            int offsetY = yDIR_MAP.get(lookThisWay);

            int checkX = targetX + offsetX;
            int checkY = targetY + offsetY;

            if (checkX < 0 || checkX >= regionWidth || checkY < 0 || checkY >= regionHeight) {
                nextSquare = "barrier";
            } else if (droneStatus[0] == OK_CODE && checkX == droneX[0] && checkY == droneY[0]) {
                nextSquare = "drone";
            } else if (droneStatus[1] == OK_CODE && checkX == droneX[1] && checkY == droneY[1]) {
                nextSquare = "drone";
            } else {
                switch (regionInfo[checkX][checkY]) {
                    case EMPTY_CODE:
                        nextSquare = "empty";
                        break;
                    case STARS_CODE:
                        nextSquare = "stars";
                        break;
                    case SUN_CODE:
                        nextSquare = "sun";
                        break;
                    default:
                        nextSquare = "unknown";
                        break;
                }
            }

            if (resultString.isEmpty()) { resultString = nextSquare; }
            else { resultString = resultString + "," + nextSquare; }
        }

        return resultString;
    }

    public void displayActionAndResponses(int id) {
        // display the drone's actions
        System.out.print("d" + String.valueOf(id) + "," + trackAction);
        if (trackAction.equals("steer")) {
            System.out.println("," + trackNewDirection);
        } else if (trackAction.equals("thrust")) {
                System.out.println("," + trackThrustDistance);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("thrust") || trackAction.equals("steer") || trackAction.equals("pass")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action_not_recognized");
        }
    }

    private void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public void renderRegion() {
        int i, j;
        int charWidth = 2 * regionWidth + 2;

        // display the rows of the region from top to bottom
        for (j = regionHeight - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < regionWidth; i++) {
                System.out.print("|");

                // the drone overrides all other contents
                if (droneStatus[0] == OK_CODE && i == droneX[0] && j == droneY[0]) {
                    System.out.print("0");
                } else if (droneStatus[1] == OK_CODE && i == droneX[1] && j == droneY[1]) {
                    System.out.print("1");
                } else {
                    switch (regionInfo[i][j]) {
                        case EMPTY_CODE:
                            System.out.print(" ");
                            break;
                        case STARS_CODE:
                            System.out.print(".");
                            break;
                        case SUN_CODE:
                            System.out.print("s");
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("|");
        }
        renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < regionWidth; i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the drone's directions
        for(int k = 0; k < MAX_DRONES; k++) {
            if (droneStatus[k] == CRASH_CODE) { continue; }
            System.out.println("dir d" + String.valueOf(k) + ": " + droneDirection[k]);
        }
        System.out.println("");
    }

    public Boolean dronesAllStopped() {
        for(int k = 0; k < MAX_DRONES; k++) {
            if (droneStatus[k] == OK_CODE) { return Boolean.FALSE; }
        }
        return Boolean.TRUE;
    }

    public Boolean droneStopped(int id) {
        return droneStatus[id] == CRASH_CODE;
    }

    public void finalReport(int completeTurns) {
        int regionSize = regionWidth * regionHeight;
        int numSuns = 0;
        int numStars = 0;
        for (int i = 0; i < regionWidth; i++) {
            for (int j = 0; j < regionHeight; j++) {
                if (regionInfo[i][j] == SUN_CODE) { numSuns++; }
                if (regionInfo[i][j] == STARS_CODE) { numStars++; }
            }
        }
        int potentialCut = regionSize - numSuns;
        int actualCut = potentialCut - numStars;
        System.out.println(String.valueOf(regionSize) + "," + String.valueOf(potentialCut) + "," + String.valueOf(actualCut) + "," + String.valueOf(completeTurns));
    }

}