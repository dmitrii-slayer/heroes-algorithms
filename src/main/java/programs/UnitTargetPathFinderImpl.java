package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    private record Point(int x, int y) {
    }

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null || existingUnitList == null) {
            return new ArrayList<>();
        }

        Point attackPosition = getAttackPosition(attackUnit, targetUnit);
        if (!isValid(attackPosition.x(), attackPosition.y())) {
            return new ArrayList<>();
        }

        if (!isCellFree(attackPosition.x(), attackPosition.y(), attackUnit, targetUnit, existingUnitList)) {
            return new ArrayList<>();
        }

        if (attackUnit.getxCoordinate() == attackPosition.x() &&
                attackUnit.getyCoordinate() == attackPosition.y()) {
            return List.of(new Edge(attackPosition.x(), attackPosition.y()));
        }

        boolean[][] obstacles = createObstacleMap(existingUnitList, attackUnit);

        Point start = new Point(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        return findPath(start, attackPosition, obstacles);
    }

    private Point getAttackPosition(Unit attackUnit, Unit targetUnit) {
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();
        int attackX = attackUnit.getxCoordinate();

        if (targetX < attackX) {
            return new Point(targetX + 1, targetY);
        } else {
            return new Point(targetX - 1, targetY);
        }
    }

    private boolean isCellFree(int x, int y, Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (!isValid(x, y)) {
            return false;
        }

        for (Unit unit : existingUnitList) {
            if (unit != null && unit.isAlive()) {
                if (unit == attackUnit || unit == targetUnit) {
                    continue;
                }

                if (unit.getxCoordinate() == x && unit.getyCoordinate() == y) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean[][] createObstacleMap(List<Unit> existingUnitList, Unit attackUnit) {
        boolean[][] obstacles = new boolean[WIDTH][HEIGHT];

        for (Unit unit : existingUnitList) {
            if (unit != null && unit.isAlive()) {
                if (unit == attackUnit) {
                    continue;
                }

                int x = unit.getxCoordinate();
                int y = unit.getyCoordinate();
                if (isValid(x, y)) {
                    obstacles[x][y] = true;
                }
            }
        }

        return obstacles;
    }

    // bfs
    private List<Edge> findPath(Point start, Point target, boolean[][] obstacles) {
        ArrayDeque<Point> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Point[][] parent = new Point[WIDTH][HEIGHT];

        queue.add(start);
        visited[start.x()][start.y()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.x() == target.x() && current.y() == target.y()) {
                return buildPath(parent, start, target);
            }

            for (int[] dir : DIRECTIONS) {
                int nx = current.x() + dir[0];
                int ny = current.y() + dir[1];

                if (isValid(nx, ny) && !obstacles[nx][ny] && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    parent[nx][ny] = current;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Edge> buildPath(Point[][] parent, Point start, Point target) {
        Point current = target;

        LinkedList<Edge> reversePath = new LinkedList<>();

        while (current != null) {
            reversePath.addFirst(new Edge(current.x(), current.y()));

            if (current.x() == start.x() && current.y() == start.y()) {
                break;
            }

            current = parent[current.x()][current.y()];
        }

        return reversePath;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
}
