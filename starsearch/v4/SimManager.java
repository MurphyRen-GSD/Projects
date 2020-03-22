
import java.util.Scanner;
import java.util.Random;
import java.io.*;

public class SimManager {
    private static Random randGenerator;

    public SpaceRegion region;
    public DroneList droneList;
    public ObjectCode oc;
    private Integer turnLimit;

    public SimManager() {
//        create a toolkit for code
        oc = new ObjectCode();
//        create a random generator for action
        randGenerator = new Random();
//        create a space region
        region = new SpaceRegion();
//        create a list of dronesx/ in the game
        droneList = new DroneList();
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

            region.set_width(Integer.parseInt(tokens[0]));

            tokens = takeCommand.nextLine().split(DELIMITER);
            region.set_height(Integer.parseInt(tokens[0]));
            if (region.get_width() < 1 || region.get_width() > 20 || region.get_height() < 1 || region.get_height() > 15) {
                System.out.print("The scope of grids is out of range");
                return;
            }


            // read in the drone starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
//            *** the minimum number of the drones will be one
            droneList.setnumberOfDrones(Integer.parseInt(tokens[0]));


            if (droneList.getNumberofDrones() < 1) {
                System.out.print("There should be at least one drone in the game");
                return;

            }
            for (k = 0; k < droneList.getNumberofDrones(); k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                droneList.setDroneX(k, Integer.parseInt(tokens[0]));
                droneList.setDroneY(k, Integer.parseInt(tokens[1]));
                droneList.setDroneDirection(k, tokens[2]);

                droneList.setDroneStrategy(k, Integer.parseInt(tokens[3]));
                droneList.setDroneStatus(k, oc.OK_CODE);

                // explore the stars at the initial location
                region.set_regionInfo(droneList.getDroneX(k), droneList.getDroneY(k), oc.EMPTY_CODE);
            }

            // read in the sun information
            tokens = takeCommand.nextLine().split(DELIMITER);
            region.setNumSuns(Integer.parseInt(tokens[0]));
            if (region.getNumSuns() < 0 || region.getNumSuns() > 0.5 * region.get_width() * region.get_height()) {
                System.out.print("The number of the suns is wrong");
                return;
            }
            for (k = 0; k < region.getNumSuns(); k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a sun at the given location
                region.set_regionInfo(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), oc.SUN_CODE);
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
        return droneList.getNumberofDrones();
    }


    public void pollDroneForAction(int id) {
        droneList.pollDroneForAction(id, region.get_width(), region.get_height(), region.get_regionInfo_all());
    }


    public void validateDroneAction(int id) {
        droneList.validateAction(id, region.get_width(), region.get_height(), region.get_regionInfo_all());
    }


    public String scanAroundSquare(int targetX, int targetY) {
        String k = droneList.DronescanAroundSquare(targetX, targetY, region.get_width(), region.get_height(), region.get_regionInfo_all());
        return k;
    }


    public void displayActionAndResponses(int id) {
        droneList.DronedisplayActionAndResponses(id);
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
        int charWidth = 2 * region.get_width() + 2;

        // display the rows of the region from top to bottom
        for (j = region.get_height() - 1; j >= 0; j--) {
            renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < region.get_width(); i++) {
                System.out.print("|");

                // the drone overrides all other contents
                if (droneList.getDroneStatus(0) == oc.OK_CODE && i == droneList.getDroneX(0) && j == droneList.getDroneStatus(0)) {
                    System.out.print("0");
                } else if (droneList.getDroneStatus(1) == oc.OK_CODE && i == droneList.getDroneX(1) && j == droneList.getDroneStatus(1)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(2) == oc.OK_CODE && i == droneList.getDroneX(2) && j == droneList.getDroneStatus(2)) {
                    System.out.print("2");
                } else if (droneList.getDroneStatus(3) == oc.OK_CODE && i == droneList.getDroneX(3) && j == droneList.getDroneStatus(3)) {
                    System.out.print("3");
                } else if (droneList.getDroneStatus(4) == oc.OK_CODE && i == droneList.getDroneX(4) && j == droneList.getDroneStatus(4)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(5) == oc.OK_CODE && i == droneList.getDroneX(5) && j == droneList.getDroneStatus(5)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(6) == oc.OK_CODE && i == droneList.getDroneX(6) && j == droneList.getDroneStatus(6)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(7) == oc.OK_CODE && i == droneList.getDroneX(7) && j == droneList.getDroneStatus(7)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(8) == oc.OK_CODE && i == droneList.getDroneX(8) && j == droneList.getDroneStatus(8)) {
                    System.out.print("1");
                } else if (droneList.getDroneStatus(9) == oc.OK_CODE && i == droneList.getDroneX(9) && j == droneList.getDroneStatus(9)) {
                    System.out.print("1");
                } else {
                    switch (region.get_regionInfo(i, j)) {
                        case 0:
                            System.out.print(" ");
                            break;
                        case 1:
                            System.out.print(".");
                            break;
                        case 2:
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
        for (i = 0; i < region.get_width(); i++) {
            System.out.print(" " + i);
        }
        System.out.println("");

        // display the drone's directions
        for (int k = 0; k < oc.MAX_DRONES; k++) {
            if (droneList.getDroneStatus(k) == oc.CRASH_CODE) {
                continue;
            }
            System.out.println("dir d" + String.valueOf(k) + ": " + droneList.getDroneDirection(k));
        }
        System.out.println("");
    }

    public Boolean droneStopped(int id) {
        return droneList.getDroneStatus(id) == oc.CRASH_CODE;
    }


    public Boolean dronesAllStopped() {
        for (int k = 0; k < oc.MAX_DRONES; k++) {
            if (droneList.getDroneStatus(k) == oc.OK_CODE) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void finalReport(int completeTurns) {
        region.SpaceRegionfinalReport(completeTurns);
    }
}
