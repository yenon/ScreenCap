package yenon.screencap.utils;

/**
 * Created by yenon on 11/25/16.
 */
public class Vector2D {
    private final double x;
    private final double y;

    private Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D rotate(double angle) {
        return new Vector2D(
                (x * Math.cos(angle)) - (y * Math.sin(angle)),
                (x * Math.sin(angle)) + (y * Math.cos(angle))
        );
    }
}
