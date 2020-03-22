import javax.xml.stream.FactoryConfigurationError;
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

    private final int MAX_DRONES = 10;
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

        for (int k = 0; k < MAX_DRONES; k++) {
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
//          ***  maximum width and maximum height could not exceed
            regionWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            regionHeight = Integer.parseInt(tokens[0]);
            if (regionWidth < 1 || regionWidth > 20 || regionHeight < 1 || regionHeight > 15) {
                System.out.print("The scope of grids is out of range");
                return;
            }

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
//            *** the minimum number of the drones will be one
            if (numberOfDrones < 1) {
                System.out.print("There should be at least one drone in the game");
                return;

            }
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
//            *** min:0, max: 50% of the space region
            if (numSuns < 0 || numSuns > 0.5 * regionWidth * regionHeight) {
                System.out.print("The number of the suns is wrong");
                return;
            }
            for (k = 0; k < numSuns; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a sun at the given location
                regionInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = SUN_CODE;
            }

            tokens = takeCommand.nextLine().split(DELIMITER);
            turnLimit = Integer.parseInt(tokens[0]);
//            *** maximum number of turns for this run min: 1, max: 200
            if (turnLimit < 1 || turnLimit > 200) {
                System.out.print("minimum and maximum number of turns for this run is 1 and 200");
                return;
            }

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

        } else if (droneStrategy[id] == 1) {
//            calculate how many barriers in the game(including the drone and sun)
            int sum_barriers = 0;
            for (int i = 0; i < regionWidth; i++) {
                for (int j = 0; j < regionHeight; j++) {
                    if (regionInfo[i][j] == SUN_CODE || regionInfo[i][j] == CRASH_CODE) {
                        sum_barriers++;
                    }
                }
            }
            int sum_info = regionHeight * regionWidth;

            if (sum_barriers / sum_info > 0.1) {
//                trackScanResults = scanAroundSquare(droneX[id], droneY[id]);
                moveRandomChoice = randGenerator.nextInt(100);
                if (moveRandomChoice < 50) {
                    // change direction
                    trackAction = "steer";
                } else {
                    // thrust forward
                    trackAction = "thrust";
                    thrustRandomChoice = randGenerator.nextInt(2);
                    trackThrustDistance = thrustRandomChoice + 1;
                }
                steerRandomChoice = randGenerator.nextInt(8);
                if (trackAction.equals("steer")) {
                    trackNewDirection = ORIENT_LIST[steerRandomChoice];
                }
            } else {
                moveRandomChoice = randGenerator.nextInt(100);
                if (moveRandomChoice < 30) {
                    // change direction
                    trackAction = "steer";
                } else {
                    // thrust forward
                    trackAction = "thrust";
                    thrustRandomChoice = randGenerator.nextInt(2);
                    trackThrustDistance = thrustRandomChoice + 1;
                }
                steerRandomChoice = randGenerator.nextInt(8);
                if (trackAction.equals("steer")) {
                    trackNewDirection = ORIENT_LIST[steerRandomChoice];
                }
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
        boolean isSuccess = true;

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
                    isSuccess = false;

                } else if (regionInfo[newSquareX][newSquareY] == SUN_CODE) {
                    // drone hit a sun
                    droneStatus[id] = CRASH_CODE;
                    trackMoveCheck = "crash";
//                    make sure that the thrust action is success or not.
                    isSuccess = false;

//                } else if (newSquareX == droneX[1 - id] && newSquareY == droneY[1 - id]) {
//                    // drone collided with the other drone
//                    droneStatus[id] = CRASH_CODE;
//                    droneStatus[1 - id] = CRASH_CODE;
//                    trackMoveCheck = "crash";

                } else if (id >= 0) {
                    int n = droneX.length;

                    for (int i = 0; i < n; i++) {
                        if (i == id) {
                        } else {

                            if (newSquareX == droneX[i] && newSquareY == droneY[i] && droneStatus[i] != CRASH_CODE) {
                                droneStatus[id] = CRASH_CODE;
                                droneStatus[i] = CRASH_CODE;
                                trackMoveCheck = "crash";
                                isSuccess = false;
                            }
                        }
                    }
                }

                if (isSuccess == true) {
                    droneX[id] = newSquareX;
                    droneY[id] = newSquareY;
                    regionInfo[newSquareX][newSquareY] = EMPTY_CODE;

                }


//                } else {
//                    // drone thrust is successful
//                    droneX[id] = newSquareX;
//                    droneY[id] = newSquareY;
//                    // update region status
//                    regionInfo[newSquareX][newSquareY] = EMPTY_CODE;
//                }

                remainingThrust = remainingThrust - 1;
            }

        } else {
            // in the case of an unknown action, treat the action as a pass
            trackMoveCheck = "action_not_recognized";
        }
    }

    public String scanAroundSquare(int targetX, int targetY) {
        String nextSquare, resultString = "";
        boolean isDrone = false;

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
            } else if (droneStatus[2] == OK_CODE && checkX == droneX[2] && checkY == droneY[2]) {
                nextSquare = "drone";
            } else if (droneStatus[3] == OK_CODE && checkX == droneX[3] && checkY == droneY[3]) {
                nextSquare = "drone";
            } else if (droneStatus[4] == OK_CODE && checkX == droneX[4] && checkY == droneY[4]) {
                nextSquare = "drone";
            } else if (droneStatus[5] == OK_CODE && checkX == droneX[5] && checkY == droneY[5]) {
                nextSquare = "drone";
            } else if (droneStatus[6] == OK_CODE && checkX == droneX[6] && checkY == droneY[6]) {
                nextSquare = "drone";
            } else if (droneStatus[7] == OK_CODE && checkX == droneX[7] && checkY == droneY[7]) {
                nextSquare = "drone";
            } else if (droneStatus[8] == OK_CODE && checkX == droneX[8] && checkY == droneY[8]) {
                nextSquare = "drone";
            }else if (droneStatus[9] == OK_CODE && checkX == droneX[9] && checkY == droneY[9]) {
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

            if (resultString.isEmpty()) {
                resultString = nextSquare;
            } else {
                resultString = resultString + "," + nextSquare;
            }
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
                }else if (droneStatus[2] == OK_CODE && i == droneX[2] && j == droneY[2]) {
                    System.out.print("2");
                }else if (droneStatus[3] == OK_CODE && i == droneX[3] && j == droneY[3]) {
                    System.out.print("3");
                }else if (droneStatus[4] == OK_CODE && i == droneX[4] && j == droneY[4]) {
                    System.out.print("1");
                }else if (droneStatus[51] == OK_CODE && i == droneX[5] && j == droneY[5]) {
                    System.out.print("1");
                }else if (droneStatus[6] == OK_CODE && i == droneX[6] && j == droneY[6]) {
                    System.out.print("1");
                }else if (droneStatus[7] == OK_CODE && i == droneX[7] && j == droneY[7]) {
                    System.out.print("1");
                }else if (droneStatus[8] == OK_CODE && i == droneX[8] && j == droneY[8]) {
                    System.out.print("1");
                }else if (droneStatus[9] == OK_CODE && i == droneX[9] && j == droneY[9]) {
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
        for (int k = 0; k < MAX_DRONES; k++) {
            if (droneStatus[k] == CRASH_CODE) {
                continue;
            }
            System.out.println("dir d" + String.valueOf(k) + ": " + droneDirection[k]);
        }
        System.out.println("");
    }

    public Boolean dronesAllStopped() {
        for (int k = 0; k < MAX_DRONES; k++) {
            if (droneStatus[k] == OK_CODE) {
                return Boolean.FALSE;
            }
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
                if (regionInfo[i][j] == SUN_CODE) {
                    numSuns++;
                }
                if (regionInfo[i][j] == STARS_CODE) {
                    numStars++;
                }
            }
        }
        int potentialCut = regionSize - numSuns;
        int actualCut = potentialCut - numStars;
        System.out.println(String.valueOf(regionSize) + "," + String.valueOf(potentialCut) + "," + String.valueOf(actualCut) + "," + String.valueOf(completeTurns));
    }

}