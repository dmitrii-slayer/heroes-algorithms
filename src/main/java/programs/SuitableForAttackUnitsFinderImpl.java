package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        if (unitsByRow == null || unitsByRow.isEmpty()) {
            return suitableUnits;
        }

        // создаем мапу для быстрого поиска: Y -> Set<X> где есть живые юниты
        Map<Integer, Set<Integer>> aliveUnitsByY = new HashMap<>();

        // заполняем мапу
        for (int x = 0; x < unitsByRow.size(); x++) {
            List<Unit> column = unitsByRow.get(x);
            if (column == null) continue;

            for (Unit unit : column) {
                if (unit != null && unit.isAlive()) {
                    int y = unit.getyCoordinate();
                    aliveUnitsByY.computeIfAbsent(y, k -> new HashSet<>()).add(x);
                }
            }
        }

        for (int x = 0; x < unitsByRow.size(); x++) {
            List<Unit> column = unitsByRow.get(x);
            if (column == null) continue;

            for (Unit unit : column) {
                if (unit == null || !unit.isAlive()) continue;

                int y = unit.getyCoordinate();
                boolean isBlocked = false;

                Set<Integer> xPositions = aliveUnitsByY.get(y);
                if (isLeftArmyTarget) {
                    // атакуем левую армию (комп)
                    // юнит заблокирован если есть живой юнит справа
                    if (xPositions != null && xPositions.contains(x + 1)) {
                        isBlocked = true;
                    }
                } else {
                    // атакуем правую армию (игрок)
                    // юнит заблокирован если есть живой юнит слева
                    if (xPositions != null && xPositions.contains(x - 1)) {
                        isBlocked = true;
                    }
                }

                if (!isBlocked) {
                    suitableUnits.add(unit);
                }
            }
        }

        return suitableUnits;
    }
}
