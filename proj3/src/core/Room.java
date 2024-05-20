package core;

import tileengine.TETile;

import java.util.Random;

public class Room {
    private Point bottomLeft;
    private Point topRight;
    private int width;
    private int height;
    private static final int RANDOM_WIDTH = 10;
    private static final int INTERFACE_WIDTH = 25;
    private static final int RANDOM_X = 80;
    private static final int RANDOM_Y = 30;
    private Random random;
    public Room(Random random, TETile[][] world) {
        this.random = random;

        while (width < 4) {
            width = random.nextInt(RANDOM_WIDTH);
        }
        while (height < 4) {
            height = random.nextInt(RANDOM_WIDTH);
        }

        bottomLeft = new Point(random.nextInt(RANDOM_X), random.nextInt(RANDOM_Y));
        while (bottomLeft.getX() + width >= world.length - 1 || bottomLeft.getY() + height >= world[0].length - 1
                || bottomLeft.getX() == 0 || bottomLeft.getY() == 0) {
            bottomLeft = new Point(random.nextInt(RANDOM_X), random.nextInt(RANDOM_Y));
        }
        topRight = new Point(bottomLeft.getX() + width, bottomLeft.getY() + height);
    }

    public Room(Random random, TETile[][] world, String check) {
        this.random = random;

        while (width < 10) {
            width = random.nextInt(INTERFACE_WIDTH);
        }
        while (height < 10) {
            height = random.nextInt(INTERFACE_WIDTH);
        }

        bottomLeft = new Point(random.nextInt(RANDOM_X), random.nextInt(RANDOM_Y));
        while (bottomLeft.getX() + width >= world.length - 1 || bottomLeft.getY() + height >= world[0].length - 1
                || bottomLeft.getX() == 0 || bottomLeft.getY() == 0) {
            bottomLeft = new Point(random.nextInt(RANDOM_X), random.nextInt(RANDOM_Y));
        }
        topRight = new Point(bottomLeft.getX() + width, bottomLeft.getY() + height);
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public Point getTopRight() {
        return topRight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
