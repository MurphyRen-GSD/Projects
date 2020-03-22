public class SpaceRegion {


    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 60;


    private Integer regionWidth;
    private Integer regionHeight;
    private Integer[][] regionInfo;
    private Integer numSuns;

    private final int STARS_CODE = 1;
    private final int SUN_CODE = 2;


//    private final int SUN_CODE = 2;
    private final int CRASH_CODE = -1;
    private final int EMPTY_CODE = 0;
//    private final int STARS_CODE = 1;
    private final int OK_CODE = 1;

    private final int MAX_DRONES = 10;




    public SpaceRegion() {
//        regionWidth = 0;
        regionHeight = 0;
        regionWidth = 0;
        regionInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];

    }

    public void set_width(int a) {
        regionWidth = a;
    }

    public void set_height(int a) {regionHeight = a; }

    public void set_regionInfo_start() {
        regionInfo = new Integer[regionWidth][regionHeight];
        for (int i = 0; i < regionWidth; i ++) {
            for (int j = 0; j < regionHeight; j ++) {
                regionInfo[i][j] = STARS_CODE;
//                System.out.println('aaaaaaaaa')

            }
        }
    }


    public void setNumSuns(int a) {numSuns = a;}

    public void set_regionInfo(int i, int j, int code) {regionInfo[i][j] = code; }

    public int get_width() { return regionWidth; }

    public int get_height() {return regionHeight; }

    public int get_regionInfo(int i, int j) { return regionInfo[i][j]; }

    public Integer[][] get_regionInfo_all() { return regionInfo; }

    public int getNumSuns() {return numSuns;}

//    private void renderHorizontalBar(int size) {
//        System.out.print(" ");
//        for (int k = 0; k < size; k++) {
//            System.out.print("-");
//        }
//        System.out.println("");
//    }




//    public void SpaceRenderRegion(Integer droneStatus[], Integer droneX[], Integer droneY[], String droneDirection[]) {
//        int i, j;
//        int charWidth = 2 * regionWidth + 2;
//
//        // display the rows of the region from top to bottom
//        for (j = regionHeight - 1; j >= 0; j--) {
//            renderHorizontalBar(charWidth);
//
//            // display the Y-direction identifier
//            System.out.print(j);
//
//            // display the contents of each square on this row
//            for (i = 0; i < regionWidth; i++) {
//                System.out.print("|");
//
//                // the drone overrides all other contents
//                if (droneStatus[0] == OK_CODE && i == droneX[0] && j == droneY[0]) {
//                    System.out.print("0");
//                } else if (droneStatus[1] == OK_CODE && i == droneX[1] && j == droneY[1]) {
//                    System.out.print("1");
//                }else if (droneStatus[2] == OK_CODE && i == droneX[2] && j == droneY[2]) {
//                    System.out.print("2");
//                }else if (droneStatus[3] == OK_CODE && i == droneX[3] && j == droneY[3]) {
//                    System.out.print("3");
//                }else if (droneStatus[4] == OK_CODE && i == droneX[4] && j == droneY[4]) {
//                    System.out.print("1");
//                }else if (droneStatus[5] == OK_CODE && i == droneX[5] && j == droneY[5]) {
//                    System.out.print("1");
//                }else if (droneStatus[6] == OK_CODE && i == droneX[6] && j == droneY[6]) {
//                    System.out.print("1");
//                }else if (droneStatus[7] == OK_CODE && i == droneX[7] && j == droneY[7]) {
//                    System.out.print("1");
//                }else if (droneStatus[8] == OK_CODE && i == droneX[8] && j == droneY[8]) {
//                    System.out.print("1");
//                }else if (droneStatus[9] == OK_CODE && i == droneX[9] && j == droneY[9]) {
//                    System.out.print("1");
//                } else {
//                    switch (regionInfo[i][j]) {
//                        case EMPTY_CODE:
//                            System.out.print(" ");
//                            break;
//                        case STARS_CODE:
//                            System.out.print(".");
//                            break;
//                        case SUN_CODE:
//                            System.out.print("s");
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//            System.out.println("|");
//        }
//        renderHorizontalBar(charWidth);
//
//        // display the column X-direction identifiers
//        System.out.print(" ");
//        for (i = 0; i < regionWidth; i++) {
//            System.out.print(" " + i);
//        }
//        System.out.println("");
//
//        // display the drone's directions
//        for (int k = 0; k < MAX_DRONES; k++) {
//            if (droneStatus[k] == CRASH_CODE) {
//                continue;
//            }
//            System.out.println("dir d" + String.valueOf(k) + ": " + droneDirection[k]);
//        }
//        System.out.println("");
//    }



    public void SpaceRegionfinalReport(int completeTurns) {
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
