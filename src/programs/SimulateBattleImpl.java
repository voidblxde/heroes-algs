package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    public void setPrintBattleLog(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) return;

        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();
        if (playerUnits == null || computerUnits == null) return;

        while (hasAlive(playerUnits) && hasAlive(computerUnits)) {
            List<Unit> order = buildOrder(playerUnits, computerUnits);

            for (Unit attacker : order) {
                if (attacker == null || !attacker.isAlive()) continue;
                if (!hasAlive(playerUnits) || !hasAlive(computerUnits)) break;

                Unit target = attacker.getProgram().attack();

                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(attacker, target);
                }
            }
        }
    }

    private static boolean hasAlive(List<Unit> units) {
        for (Unit u : units) {
            if (u != null && u.isAlive()) return true;
        }
        return false;
    }

    private static List<Unit> buildOrder(List<Unit> playerUnits, List<Unit> computerUnits) {
        List<Unit> all = new ArrayList<>();
        for (Unit u : playerUnits) if (u != null && u.isAlive()) all.add(u);
        for (Unit u : computerUnits) if (u != null && u.isAlive()) all.add(u);

        all.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
        return all;
    }
}
