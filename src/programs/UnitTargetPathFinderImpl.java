package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    private static final int[] DX = { 1, 1, 1, 0, 0, -1, -1, -1 };
    private static final int[] DY = { 1, 0, -1, 1, -1, 1, 0, -1 };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null) return Collections.emptyList();

        int sx = attackUnit.getxCoordinate();
        int sy = attackUnit.getyCoordinate();
        int gx = targetUnit.getxCoordinate();
        int gy = targetUnit.getyCoordinate();

        if (!inBounds(sx, sy) || !inBounds(gx, gy)) return Collections.emptyList();
        if (sx == gx && sy == gy) return List.of(new Edge(sx, sy));

        boolean[] blocked = new boolean[WIDTH * HEIGHT];
        if (existingUnitList != null) {
            for (Unit u : existingUnitList) {
                if (u == null || !u.isAlive()) continue;
                if (u == attackUnit || u == targetUnit) continue;
                int x = u.getxCoordinate();
                int y = u.getyCoordinate();
                if (inBounds(x, y)) blocked[id(x, y)] = true;
            }
        }

        int start = id(sx, sy);
        int goal = id(gx, gy);

        int[] parent = new int[WIDTH * HEIGHT];
        Arrays.fill(parent, -1);

        ArrayDeque<Integer> q = new ArrayDeque<>();
        boolean[] visited = new boolean[WIDTH * HEIGHT];

        visited[start] = true;
        q.add(start);

        while (!q.isEmpty()) {
            int cur = q.poll();
            if (cur == goal) break;

            int cx = cur % WIDTH;
            int cy = cur / WIDTH;

            for (int k = 0; k < 8; k++) {
                int nx = cx + DX[k];
                int ny = cy + DY[k];
                if (!inBounds(nx, ny)) continue;

                int nid = id(nx, ny);
                if (visited[nid]) continue;

                if (blocked[nid] && nid != goal) continue;

                visited[nid] = true;
                parent[nid] = cur;
                q.add(nid);
            }
        }

        if (!visited[goal]) return Collections.emptyList();

        ArrayList<Edge> path = new ArrayList<>();
        for (int cur = goal; cur != -1; cur = parent[cur]) {
            path.add(new Edge(cur % WIDTH, cur / WIDTH));
        }
        Collections.reverse(path);
        return path;
    }

    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private static int id(int x, int y) {
        return y * WIDTH + x;
    }
}
