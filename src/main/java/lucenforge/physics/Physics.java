package lucenforge.physics;

public class Physics {

    private static long gameStartTimeMillis;

    private static long updateStartTime;
    private static long deltaTimeMillis;

    public static void init(){
        gameStartTimeMillis = System.currentTimeMillis();
    }

    // Returns the time since startup in milliseconds
    public static long getRuntimeMillis(){
        return System.currentTimeMillis() - gameStartTimeMillis;
    }
    public static float getRuntimeSeconds(){
        return getRuntimeMillis()/1000f;
    }

    // Updates the time (and more later)
    public static void update() {
        long millisNow = System.currentTimeMillis();
        deltaTimeMillis = millisNow - updateStartTime;
        updateStartTime = millisNow;
    }

    public static long deltaTimeMillis(){
        return deltaTimeMillis;
    }
    public static float deltaTimeSeconds(){
        return deltaTimeMillis()/1000f;
    }
}
