import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class DroneList {

    private static Random randGenerator;


    private Integer numberOfDrones;
    private Integer droneX[], droneY[];
    private String droneDirection[];
    private Integer droneStrategy[];
    private Integer droneStatus[];

    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;


    private final int MAX_DRONES = 10;

    private String trackAction;
    private String trackNewDirection;
    private Integer trackThrustDistance;
    private String trackMoveCheck;
    private String trackScanResults;

    private final int SUN_CODE = 2;
    private final int CRASH_CODE = -1;
    private final int EMPTY_CODE = 0;
    private final int STARS_CODE = 1;
    private final int OK_CODE = 1;


    private final String[] ORIENT_LIST = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};


    public DroneList() {
        numberOfDrones = -1;
        droneX = new Integer[MAX_DRONES];
        droneY = new Integer[MAX_DRONES];
        droneDirection = new String[MAX_DRONES];
        droneStrategy = new Integer[MAX_DRONES];
        droneStatus = new Integer[MAX_DRONES];


        randGenerator = new Random();

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

        for (int k = 0; k < MAX_DRONES; k++) {
            droneX[k] = -1;
            droneY[k] = -1;
            droneDirection[k] = "north";
            droneStrategy[k] = -1;
            droneStatus[k] = CRASH_CODE;


        }
    }

        public void setnumberOfDrones ( int a){
            numberOfDrones = a;
        }

        public void setDroneX ( int k, int a){
            droneX[k] = a;
        }

        public void setDroneY ( int k, int a){
            droneY[k] = a;
        }

        public void setDroneDirection ( int k, String a){
            droneDirection[k] = a;
        }

        public void setDroneStrategy ( int k, int a){
            droneStrategy[k] = a;
        }

        public void setDroneStatus ( int k, int a){
            droneStatus[k] = a;
        }


        public int getNumberofDrones () {
            return numberOfDrones;
        }


        public int getDroneX ( int k){
            return droneX[k];
        }

        public int getDroneY ( int k){
            return droneY[k];
        }

        public String getDroneDirection ( int k){
            return droneDirection[k];
        }

        public int getDroneStrategy ( int k){
            return droneStrategy[k];
        }

        public int getDroneStatus ( int k){
            return droneStatus[k];
        }

        public String getTrackAction () {
            return trackAction;
        }

        public String getTrackNewDirection () {
            return trackNewDirection;
        }

        public int getTrackThrustDistance () {
            return trackThrustDistance;
        }

        public String getTrackMoveCheck () {
            return trackMoveCheck;
        }

        public String getTrackScanResults () {
            return trackScanResults;
        }


        public void pollDroneForAction ( int id, int regionWidth, int regionHeight, Integer[][] regionInfo){
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
                        if (regionInfo[i][j] == 2 || regionInfo[i][j] == -1) {
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

        public void validateAction(int id, int regionWidth, int regionHeight, Integer[][] regionInfo){
            int xOrientation, yOrientation;
            boolean isSuccess = true;

            if (trackAction.equals("scan")) {
                // in the case of a scan, return the information for the eight surrounding squares
                // always use a northbound orientation
                trackScanResults = DronescanAroundSquare(droneX[id], droneY[id], regionWidth, regionHeight, regionInfo);
                trackScanResults = DronescanAroundSquare(droneX[id], droneY[id], regionWidth, regionHeight, regionInfo);
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
//                    *************
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


        public String DronescanAroundSquare ( int targetX, int targetY, int regionWidth, int regionHeight, Integer[][]
        regionInfo){
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
                } else if (droneStatus[9] == OK_CODE && checkX == droneX[9] && checkY == droneY[9]) {
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






    }
