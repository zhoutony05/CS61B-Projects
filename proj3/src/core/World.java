package core;

import edu.princeton.cs.algs4.StdDraw;
import net.sf.saxon.expr.Component;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class World {
    private TETile[][] world;
    private List<Room> rooms;
    private List<Hallway> hallways;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private final TERenderer ter = new TERenderer();
    private final TERenderer inTER = new TERenderer();
    private Random random;
    private int[] idWQU;
    private int[] sizeWQU;
    private Point player;
    private Point interfacePlayer;
    //private static final String SAVE_FILE = "proj3/src/save.txt";
    private static final String SAVE_FILE = "src/save.txt";
    private static final int DIMENSIONS = 15;
    private static final int MAX_ROOMS = 12;
    private static final int NUM_MOUNTAINS = 3;
    private static final int NUM_FLOWERS_PER_ROOM = 5;
    private static final int NUM_TREES_PER_ROOM = 3;
    private static final String WIN_MESSAGE = "YOU WIN!";
    private static final String LOSE_MESSAGE = "YOU LOSE!";
    private int numFlowers = 0;
    private int numRoomsCleared = 0;
    private String keyPress;
    private long seed;

    public World(long seed) {
        rooms = new ArrayList<>();
        hallways = new ArrayList<>();
        world = new TETile[WIDTH][HEIGHT];
        idWQU = new int[DIMENSIONS];
        sizeWQU = new int[DIMENSIONS];
        random = new Random(seed);
        this.seed = seed;
        keyPress = "";
        world = fillWithNothing(world);
        fillWorld();
        generateHallways();
        placePlayer(world);
        placeMountains();
    }
    public World(String filename) {
        rooms = new ArrayList<>();
        hallways = new ArrayList<>();
        world = new TETile[WIDTH][HEIGHT];
        idWQU = new int[DIMENSIONS];
        sizeWQU = new int[DIMENSIONS];
        loadBoard(filename);
    }

    public void placeMountains() {
        int mountainsPlaced = 0;
        while (mountainsPlaced < NUM_MOUNTAINS) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (world[x][y] == Tileset.FLOOR) {
                world[x][y] = Tileset.MOUNTAIN;
                mountainsPlaced++;
            }
        }
    }

    public TETile[][] generateInterface() {
        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
        newWorld = fillWithNothing(newWorld);

//        Random random = new Random(seed);
        Room room = new Room(random, newWorld, "check");
        fillRoom(room, newWorld);

        int flowers = 0;
        int trees = 0;

        while (flowers < NUM_FLOWERS_PER_ROOM || trees < NUM_TREES_PER_ROOM) {
            int x = random.nextInt(room.getWidth()) + room.getBottomLeft().getX();
            int y = random.nextInt(room.getHeight()) + room.getBottomLeft().getY();
            if (newWorld[x][y] == Tileset.FLOOR) {
                if (flowers < NUM_FLOWERS_PER_ROOM) {
                    newWorld[x][y] = Tileset.FLOWER;
                    flowers++;
                } else {
                    newWorld[x][y] = Tileset.TREE;
                    trees++;
                }
            }
        }

        return newWorld;
//        seed += 1;
//        renderInterface(newWorld);
    }

    public void renderInterface(TETile[][] newWorld) {
        placeInterfacePlayer(newWorld);

        inTER.initialize(WIDTH, HEIGHT);
        while (numFlowers < NUM_FLOWERS_PER_ROOM) {
            drawWorld(newWorld, interfacePlayer, inTER);
            handleKeyPress(newWorld, interfacePlayer);
            StdDraw.show();
        }
        keyPress += "\n";
        numFlowers = 0;
        numRoomsCleared += 1;

        if (numRoomsCleared == NUM_MOUNTAINS) {
            drawWinLose("Win");
        }
        renderBoard();
    }

    public void drawWinLose(String condition) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        boolean displayScreen = true;

        while (displayScreen) {
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 5, "Type [m] to return to main menu");
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 10, "Type [q] to quit");
            if (Objects.equals(condition, "Win")) {
                StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, WIN_MESSAGE);
            } else {
                StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, LOSE_MESSAGE);
            }
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == 'M') {
                    displayScreen = false;
                    Main.mainMenu();
                } else if (key == 'Q') {
                    System.exit(0);
                }
            }
//        StdDraw.pause(10000);
//        System.exit(0);
        }
    }

    public TETile[][] fillWithNothing(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    public void fillWorld() {
        int numRooms = 0;
        while (numRooms < MAX_ROOMS) {
            numRooms = random.nextInt(DIMENSIONS);
        }
        while (numRooms > 0) {
            Room newRoom = new Room(random, world);
            boolean validRoom = checkValidRoom(newRoom);
            if (validRoom) {
                fillRoom(newRoom, world);
                addRoomWQU(newRoom);
                rooms.add(newRoom);
                numRooms--;
            }
        }
    }

    private void drawHUD(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            TETile tile = world[mouseX][mouseY];
            drawHUDText(tile.description());
        }
    }

    private void drawHUDText(String text) {
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, MAX_ROOMS));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, "Tile: " + text);
    }

    private void placePlayer(TETile[][] world) {
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (world[x][y] == Tileset.FLOOR) {
                player = new Point(x, y);
                break;
            }
        }
    }

    private void placeInterfacePlayer(TETile[][] world) {
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (world[x][y] == Tileset.FLOOR) {
                interfacePlayer = new Point(x, y);
                break;
            }
        }
    }

    public void renderBoard() {
        ter.initialize(WIDTH, HEIGHT);
        while (numRoomsCleared < NUM_MOUNTAINS) {
            drawWorld(world, player, ter);
            handleKeyPress(world, player);
            StdDraw.show();
        }
    }

    private void drawWorld(TETile[][] world, Point player, TERenderer ter) {
        ter.drawTiles(world);
        world[player.getX()][player.getY()] = Tileset.AVATAR;
        drawHUD(world);
    }

    private void handleKeyPress(TETile[][] world, Point player) {
        if (!StdDraw.hasNextKeyTyped()) {
            return;
        }
        char key = StdDraw.nextKeyTyped();
        int newX = player.getX();
        int newY = player.getY();
        switch (key) {
            case ':':
                keyPress += ':';
                break;
            case 'w':
                newY += 1;
                keyPress += 'w';
                break;
            case 'a':
                newX -= 1;
                keyPress += 'a';
                break;
            case 's':
                newY -= 1;
                keyPress += 's';
                break;
            case 'd':
                newX += 1;
                keyPress += 'd';
                break;
            case 'q', 'Q':
                if (keyPress.charAt(keyPress.length() - 1) == ':') {
                    keyPress += 'q';
                    handleQuitSave();
                }
                break;
            default:
                break;
        }
        if (isValidMove(newX, newY, world, false)) {
            world[player.getX()][player.getY()] = Tileset.FLOOR;
            player.setX(newX);
            player.setY(newY);
        }
    }

    private void handleQuitSave() {
        saveBoard();
        System.exit(0);
    }

    private boolean isValidMove(int x, int y, TETile[][] world, boolean load) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        } else if (world[x][y] == Tileset.MOUNTAIN) {
            world[x][y] = Tileset.FLOOR;
            if (!load) {
                keyPress += "\n";
                renderInterface(generateInterface());
                player.setX(x);
                player.setY(y);
                return true;
            }
//            world[x][y] = Tileset.FLOOR;
        } else if (world[x][y] == Tileset.FLOWER) {
            numFlowers += 1;
            return true;
        } else if (world[x][y] == Tileset.TREE) {
            drawWinLose("Lose");
        }
        return world[x][y] == Tileset.FLOOR;
    }

    private boolean checkValidRoom(Room room) {
        for (int i = room.getBottomLeft().getX(); i <= room.getTopRight().getX(); i++) {
            for (int j = room.getBottomLeft().getY(); j <= room.getTopRight().getY(); j++) {
                if (world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    private void fillRoom(Room room, TETile[][] world) {
        for (int i = room.getBottomLeft().getX(); i <= room.getTopRight().getX(); i++) {
            for (int j = room.getBottomLeft().getY(); j <= room.getTopRight().getY(); j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = room.getBottomLeft().getX(); i <= room.getTopRight().getX(); i++) {
            world[i][room.getBottomLeft().getY()] = Tileset.WALL;
        }
        for (int i = room.getBottomLeft().getY(); i <= room.getTopRight().getY(); i++) {
            world[room.getBottomLeft().getX()][i] = Tileset.WALL;
        }
        for (int i = room.getBottomLeft().getX(); i <= room.getTopRight().getX(); i++) {
            world[i][room.getTopRight().getY()] = Tileset.WALL;
        }
        for (int i = room.getBottomLeft().getY(); i <= room.getTopRight().getY(); i++) {
            world[room.getTopRight().getX()][i] = Tileset.WALL;
        }
    }

    private void generateHallways() {
        while (!allRoomsConnected()) {
            Room room2 = rooms.get(random.nextInt(rooms.size()));
            Room room1 = rooms.get(random.nextInt(rooms.size()));
            if (!room1.equals(room2)) {
                Point start = randomEdge(room1);
                Point end = randomEdge(room2);
                Hallway hallway = new Hallway(start, end);
                if (canConnect(buildHallway(start, end)) && !hallways.contains(hallway)) {
                    connectRooms(room1, room2, hallway);
                    hallways.add(hallway);
                }
            }
        }
    }

    private void connectRooms(Room room1, Room room2, Hallway hallway) {
        drawHallway(hallway.getStart(), hallway.getEnd());
        union(find(rooms.indexOf(room1)), find(rooms.indexOf(room2)));
    }

    private void drawHallway(Point start, Point end) {
        HashSet<Point> hallway = buildHallway(start, end);
        for (Point point: hallway) {
            world[point.getX()][point.getY()] = Tileset.FLOOR;
            paintAdjacent(point);
        }
    }

    private void paintAdjacent(Point point) {
        if (world[point.getX() + 1][point.getY()] == Tileset.NOTHING) {
            world[point.getX() + 1][point.getY()] = Tileset.WALL;
        }
        if (world[point.getX() - 1][point.getY()] == Tileset.NOTHING) {
            world[point.getX() - 1][point.getY()] = Tileset.WALL;
        }
        if (world[point.getX()][point.getY() + 1] == Tileset.NOTHING) {
            world[point.getX()][point.getY() + 1] = Tileset.WALL;
        }
        if (world[point.getX()][point.getY() - 1] == Tileset.NOTHING) {
            world[point.getX()][point.getY() - 1] = Tileset.WALL;
        }
    }

    private HashSet<Point> buildHallway(Point start, Point end) {
        HashSet<Point> hallway = new HashSet<>();
        Point pos = start;
        hallway.add(pos);
        while (pos.getY() != end.getY()) {
            int increment = 0;
            if (end.getY() > pos.getY()) {
                increment++;
            } else if (end.getY() < pos.getY()) {
                increment--;
            }
            Point temp = new Point(pos.getX(), pos.getY() + increment);
            hallway.add(temp);
            pos = temp;
        }
        while (pos.getX() != end.getX()) {
            int increment = 0;
            if (end.getX() > pos.getX()) {
                increment++;
            } else if (end.getX() < pos.getX()) {
                increment--;
            }
            Point temp = new Point(pos.getX() + increment, pos.getY());
            hallway.add(temp);
            pos = temp;
        }
        return hallway;
    }

    private boolean canConnect(HashSet<Point> hallway) {
        for (Point point: hallway) {
            if (world[point.getX()][point.getY()] == Tileset.FLOOR) {
                return false;
            }
        }
        return true;
    }

    private Point randomEdge(Room room) {
        int wall = random.nextInt(4);
        int x, y;
        switch (wall) {
            case 0:
                x = room.getBottomLeft().getX() + random.nextInt(1, room.getWidth());
                y = room.getTopRight().getY();
                break;
            case 1:
                x = room.getTopRight().getX();
                y = room.getBottomLeft().getY() + random.nextInt(1, room.getHeight());
                break;
            case 2:
                x = room.getBottomLeft().getX() + random.nextInt(1, room.getWidth());
                y = room.getBottomLeft().getY();
                break;
            default:
                x = room.getBottomLeft().getX();
                y = room.getBottomLeft().getY() + random.nextInt(1, room.getHeight());
                break;
        }
        return new Point(x, y);
    }

    private boolean allRoomsConnected() {
        int root = find(0);
        for (int i = 1; i < rooms.size(); i++) {
            int currentRoot = find(i);
            if (root != currentRoot) {
                return false;
            }
        }
        return true;
    }

    // WQU

    private void addRoomWQU(Room room) {
        int index = rooms.size();
        idWQU[index] = index;
        sizeWQU[index] = 1;
    }

    private int find(int p) {
        while (p != idWQU[p]) {
            idWQU[p] = idWQU[idWQU[p]];
            p = idWQU[p];
        }
        return p;
    }

    private void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return;
        }
        if (sizeWQU[rootP] < sizeWQU[rootQ]) {
            idWQU[rootP] = rootQ;
            sizeWQU[rootQ] += sizeWQU[rootP];
        } else {
            idWQU[rootQ] = rootP;
            sizeWQU[rootP] += sizeWQU[rootQ];
        }
    }

    public TETile[][] getWorld() {
        return world;
    }
    /*
    public void saveBoard() {
        StringBuilder s = new StringBuilder();
        for (int j = HEIGHT - 1; j >= 0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                TETile key = world[i][j];
                int val = -1;
                if (key.equals(Tileset.NOTHING)) {
                    val = 0;
                } else if (key.equals(Tileset.FLOOR)) {
                    val = 1;
                } else if (key.equals(Tileset.WALL)) {
                    val = 2;
                } else if (key.equals(Tileset.AVATAR)) {
                    val = 3;
                }
                s.append(val);
            }
            s.append("\n");
        }
        FileUtils.writeFile(SAVE_FILE, s.toString());
    }
    */


    public void saveBoard() {
        StringBuilder s = new StringBuilder();
        for (int j = HEIGHT - 1; j >= 0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                TETile key = world[i][j];
                int val = -1;
                if (key.equals(Tileset.NOTHING)) {
                    val = 0;
                } else if (key.equals(Tileset.FLOOR)) {
                    val = 1;
                } else if (key.equals(Tileset.WALL)) {
                    val = 2;
                } else if (key.equals(Tileset.AVATAR)) {
                    val = 3;
                } else if (key.equals(Tileset.MOUNTAIN)) {
                    val = 4;
                }
                s.append(val);
            }
            s.append("\n");
        }
        FileUtils.writeFile(SAVE_FILE, seed + "\n" + numRoomsCleared + "\n" + s);
    }

    public void loadBoard(String filename) {
        String s = FileUtils.readFile(filename);
        String[] arr = s.split("\n");
        seed = Long.parseLong(arr[0]);
        numRoomsCleared = Integer.parseInt(arr[1]);
        random = new Random(seed);
        fillWithNothing(world);
        fillWorld();
        generateHallways();
        placePlayer(world);
        placeMountains();
        world = generateBoard(filename);
        for (int i = 0; i < numRoomsCleared; i++) {
            generateInterface();
        }
    }

    public TETile[][] generateBoard(String filename) {
        String s = FileUtils.readFile(filename);
        String[] arr = s.split("\n");
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        for (int j = HEIGHT - 1; j >= 0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                TETile tile = null;
                int val = Character.getNumericValue(arr[HEIGHT - j + 1].charAt(i));
                switch (val) {
                    case 0 -> tile = Tileset.NOTHING;
                    case 1 -> tile = Tileset.FLOOR;
                    case 2 -> tile = Tileset.WALL;
                    case 3 -> {
                        tile = Tileset.AVATAR;
                        player = new Point(i, j);
                    }
                    case 4 -> {
                        tile = Tileset.MOUNTAIN;
                    }
                }
                tiles[i][j] = tile;
            }
        }
        return tiles;
    }

    public void movePlayer(String movement, TETile[][] world) {
        String[] keys = movement.split("");
        for (String key: keys) {
            int newX = player.getX();
            int newY = player.getY();
            switch (key) {
                case "w" -> {
                    newY += 1;
                }
                case "a" -> {
                    newX -= 1;
                }
                case "s" -> {
                    newY -= 1;
                }
                case "d" -> {
                    newX += 1;
                }
                default -> {

                }
            }
            if (isValidMove(newX, newY, world, true)) {
                world[player.getX()][player.getY()] = Tileset.FLOOR;
                player.setX(newX);
                player.setY(newY);
            }
        }
    }


}
