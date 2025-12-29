package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.Program;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> allUnits = new ArrayList<>(playerArmy.getUnits().size() + computerArmy.getUnits().size());
        allUnits.addAll(playerArmy.getUnits());
        allUnits.addAll(computerArmy.getUnits());

        allUnits.sort(Comparator
                .comparingInt(Unit::getBaseAttack)
                .reversed());

        while (hasAliveUnits(playerArmy) && hasAliveUnits(computerArmy)) {
            for (Unit currentUnit : allUnits) {
                if (!currentUnit.isAlive()) {
                    continue;
                }

                Program program = currentUnit.getProgram();
                if (program != null) {
                    Unit target = program.attack();
                    if (target == null) {
                        System.out.println("Не найдена цель для атаки");
                        continue;
                    }

                    printBattleLog.printBattleLog(currentUnit, target);
                    if (!target.isAlive()) {
                        System.out.println(target.getName() + " мертв!");
                    }
                }
            }
        }

        System.out.println("####### Конец #######");
        if (hasAliveUnits(playerArmy)) {
            System.out.println("Победил игрок!");
        } else {
            System.out.println("Победил компьютер!");
        }
    }

    private boolean hasAliveUnits(Army army) {
        for (Unit unit : army.getUnits()) {
            if (unit.isAlive()) {
                return true;
            }
        }
        return false;
    }
}
