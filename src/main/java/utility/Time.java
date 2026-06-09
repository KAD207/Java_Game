package utility;

public class Time {
    // init at system startup
    public static float timeElapsed = System.nanoTime();

    public static float getTimeElapsed(){
        return (float) ((System.nanoTime() - timeElapsed) * 1E-9);
    }


}
