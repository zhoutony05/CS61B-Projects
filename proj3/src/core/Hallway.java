package core;

import java.util.Objects;

public class Hallway {

    private Point start;
    private Point end;
    public Hallway(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hallway hallway = (Hallway) o;
        return (Objects.equals(start, hallway.start) && Objects.equals(end, hallway.end))
                || (Objects.equals(start, hallway.end) && Objects.equals(end, hallway.start));
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
