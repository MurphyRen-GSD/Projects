public class SpaceRegion {

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 60;

    private Integer regionWidth;
    private Integer regionHeight;
    private Integer[][] regionInfo;
    private Integer numSuns;
    public ObjectCode oc;

    // generate the region information
    public SpaceRegion() {
        oc = new ObjectCode();
        regionHeight = 0;
        regionWidth = 0;
        regionInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        for (int i = 0; i < DEFAULT_WIDTH; i++) {
            for (int j = 0; j < DEFAULT_HEIGHT; j++) {
                regionInfo[i][j] = oc.STARS_CODE;
            }
        }
    }

    public void set_width(int a) {
        regionWidth = a;
    }

    public void set_height(int a) {
        regionHeight = a;
    }

    public void set_regionInfo_start() {
        regionInfo = new Integer[regionWidth][regionHeight];
        for (int i = 0; i < regionWidth; i++) {
            for (int j = 0; j < regionHeight; j++) {
                regionInfo[i][j] = oc.STARS_CODE;

            }
        }
    }


    public void setNumSuns(int a) {
        numSuns = a;
    }

    public void set_regionInfo(int i, int j, int code) {
        regionInfo[i][j] = code;
    }

    public int get_width() {
        return regionWidth;
    }

    public int get_height() {
        return regionHeight;
    }

    public int get_regionInfo(int i, int j) {
        return regionInfo[i][j];
    }

    public Integer[][] get_regionInfo_all() {
        return regionInfo;
    }

    public int getNumSuns() {
        return numSuns;
    }


    public void SpaceRegionfinalReport(int completeTurns) {
        int regionSize = regionWidth * regionHeight;
        int numSuns = 0;
        int numStars = 0;
        for (int i = 0; i < regionWidth; i++) {
            for (int j = 0; j < regionHeight; j++) {
                if (regionInfo[i][j] == oc.SUN_CODE) {
                    numSuns++;
                }
                if (regionInfo[i][j] == oc.STARS_CODE) {
                    numStars++;
                }
            }
        }
        int potentialCut = regionSize - numSuns;
        int actualCut = potentialCut - numStars;
        System.out.println(String.valueOf(regionSize) + "," + String.valueOf(potentialCut) + "," + String.valueOf(actualCut) + "," + String.valueOf(completeTurns));
    }


}
