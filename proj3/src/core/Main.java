package core;

import edu.princeton.cs.algs4.StdDraw;

public class Main {
    private static World world;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final double HIGH_Y = 0.8;
    private static final double MED_Y = 0.6;
    private static final double LOW_Y = 0.4;
    private static final double LOWER_Y = 0.2;

    public static void mainMenu() {
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        StdDraw.enableDoubleBuffering();

        boolean displayMenu = true;

        while (displayMenu) {
            StdDraw.clear();

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0.5, HIGH_Y, "CS61B: THE GAME");

            StdDraw.text(0.5, MED_Y, "New Game (N)");
            StdDraw.text(0.5, 0.5, "Load Game (L)");
            StdDraw.text(0.5, LOW_Y, "Quit (Q)");
            StdDraw.text(0.5, LOWER_Y, "Climb up the mountains to collect the flowers. Make sure to avoid the evil trees though!");

            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case 'N':
                        displayMenu = false;
                        setSeed();
                        break;
                    case 'L':
                        displayMenu = false;
                        //world = new World("proj3/src/save.txt");
                        world = new World("src/save.txt");
                        world.renderBoard();
                        break;
                    case 'Q':
                        displayMenu = false;
                        System.exit(0);
                        break;
                    default:
                }
            }
        }
    }

    private static final double TEXT_Y = 0.8;
    private static final double TEXT_YY = 0.7;
    private static void setSeed() {
        StringBuilder userInput = new StringBuilder();
        while (true) {
            StdDraw.clear();

            StdDraw.setPenColor(StdDraw.BLACK);

            StdDraw.text(0.5, TEXT_Y, "Enter Numbers to Generate World");

            StdDraw.text(0.5, TEXT_YY, "Type [S] to complete");

            StdDraw.text(0.5, 0.5, userInput.toString());

            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'S' || key == 's') {
                    break;
                }
                if (Character.isDigit(key)) {
                    userInput.append(key);
                }
            }
        }
        //AutograderBuddy.getWorldFromInput(String.valueOf(324987));
        //AutograderBuddy.world.renderBoard();
        world = new World(Long.parseLong(String.valueOf(userInput)));
        world.renderBoard();
    }

    public static void main(String[] args) {
        mainMenu();

        //AutograderBuddy.getWorldFromInput(String.valueOf(324987));
        //AutograderBuddy.world.renderBoard();
    }
}
