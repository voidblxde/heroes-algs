package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> result = new ArrayList<>();
        if (unitsByRow == null) return result;

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            Set<Integer> occupiedY = new HashSet<>(row.size() * 2);
            for (Unit u : row) {
                if (u != null && u.isAlive()) {
                    occupiedY.add(u.getyCoordinate());
                }
            }

            for (Unit u : row) {
                if (u == null || !u.isAlive()) continue;

                int y = u.getyCoordinate();
                boolean covered = isLeftArmyTarget
                        ? occupiedY.contains(y - 1)  // атакуем левую армию
                        : occupiedY.contains(y + 1); // атакуем правую армию

                if (!covered) result.add(u);
            }
        }

        return result;
    }
}
