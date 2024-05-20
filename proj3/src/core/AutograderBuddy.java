package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    private static final String SAVE_FILE = "proj3/src/save.txt";
//    private static final String SAVE_FILE = "save.txt";

    public static TETile[][] getWorldFromInput(String input) {
        World world;
        char[] arr = input.toLowerCase().toCharArray();
        StringBuilder seed = new StringBuilder();
        StringBuilder movement = new StringBuilder();
        boolean save = false;
        boolean load = false;
        for (char c: arr) {
            if (Character.isDigit(c)) {
                seed.append(c);
                save = false;
            } else if (c == 'w' || c == 's' || c == 'a' || c == 'd') {
                movement.append(c);
                save = false;
            } else if (c == ':') {
                movement.append(':');
                save = true;
            } else if (c == 'q') {
                movement.append('q');
                if (save) {
                    FileUtils.writeFile(SAVE_FILE, seed + "\n" + movement.substring(1));
                }
            } else if (c == 'l') {
                load = true;
            }
        }
        if (load) {
            world = new World(SAVE_FILE);
//            world.movePlayer(movement.toString(), world.getWorld());
        } else {
            //            System.out.println(seed);
            world = new World(Long.parseLong(seed.toString()));
            world.movePlayer(movement.substring(1), world.getWorld());
        }
        return world.getWorld();
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
