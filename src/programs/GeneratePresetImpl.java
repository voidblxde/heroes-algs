package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MAX_PER_TYPE = 11;

    private static final int START_X_MIN = 0;
    private static final int START_X_MAX = 2;
    private static final int COLS = START_X_MAX - START_X_MIN + 1;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {
            Army empty = new Army(Collections.emptyList());
            empty.setPoints(0);
            return empty;
        }

        Map<String, Unit> templateByType = new HashMap<>();
        for (Unit u : unitList) {
            if (u != null) templateByType.putIfAbsent(u.getUnitType(), u);
        }
        List<Unit> templates = new ArrayList<>(templateByType.values());

        if (templates.isEmpty()) {
            Army empty = new Army(Collections.emptyList());
            empty.setPoints(0);
            return empty;
        }

        templates.sort((a, b) -> {
            int c1 = Double.compare(ratioAttack(b), ratioAttack(a));
            if (c1 != 0) return c1;
            int c2 = Double.compare(ratioHealth(b), ratioHealth(a));
            if (c2 != 0) return c2;
            return Integer.compare(a.getCost(), b.getCost());
        });

        Map<String, Integer> countByType = new HashMap<>();
        List<Unit> picked = new ArrayList<>();
        int points = 0;

        int maxUnits = templates.size() * MAX_PER_TYPE;
        int cursor = 0;

        while (picked.size() < maxUnits) {
            boolean added = false;

            for (int step = 0; step < templates.size(); step++) {
                Unit t = templates.get((cursor + step) % templates.size());
                String type = t.getUnitType();

                int cnt = countByType.getOrDefault(type, 0);
                if (cnt >= MAX_PER_TYPE) continue;

                int cost = t.getCost();
                if (points + cost > maxPoints) continue;

                int i = picked.size();
                int x = START_X_MIN + (i % COLS);
                int y = i / COLS;

                picked.add(cloneFromTemplate(t, i + 1, x, y));
                points += cost;
                countByType.put(type, cnt + 1);

                cursor = (cursor + step + 1) % templates.size();
                added = true;
                break;
            }

            if (!added) break;
        }

        Army army = new Army(picked);
        army.setPoints(points);
        return army;
    }

    private static double ratioAttack(Unit u) {
        return (double) u.getBaseAttack() / Math.max(1, u.getCost());
    }

    private static double ratioHealth(Unit u) {
        return (double) u.getHealth() / Math.max(1, u.getCost());
    }

    private static Unit cloneFromTemplate(Unit t, int index, int x, int y) {
        Unit u = new Unit(
                t.getUnitType() + " " + index,
                t.getUnitType(),
                t.getHealth(),
                t.getBaseAttack(),
                t.getCost(),
                t.getAttackType(),
                t.getAttackBonuses(),
                t.getDefenceBonuses(),
                x,
                y
        );
        u.setAlive(true);
        return u;
    }
}
