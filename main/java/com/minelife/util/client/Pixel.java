package com.minelife.util.client;

public class Pixel implements Comparable<Pixel> {
    public int x, y, color;

    public Pixel(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public int compareTo(Pixel o) {
        int result = o.x - x;
        // on the same x
        if(result == 0) {
            result = o.y - y;
            // on the same y
            if(result == 0) {
                return 0;
            }
        }

        return result;
    }
}