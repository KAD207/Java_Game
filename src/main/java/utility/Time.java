package utility;

public class Time {
    // init at system startup
    public static double timeStarted = System.nanoTime();

    public static float getTimeStarted(){
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }


}
