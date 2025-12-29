package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MIN_X = 0;
    private static final int MAX_X = 2;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 20;
    private static final int MAX_UNITS_PER_TYPE = 11;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        List<Unit> armyUnits = new ArrayList<>();
        int totalPoints = 0;

        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort(Comparator
                .comparingDouble((Unit u) -> (double) u.getBaseAttack() / u.getCost())
                .thenComparingDouble(u -> (double) u.getHealth() / u.getCost())
                .reversed());

        // жадный алгоритм
        for (Unit unitTemplate : sortedUnits) {
            int unitCost = unitTemplate.getCost();
            int maxCanTake = Math.min(MAX_UNITS_PER_TYPE,
                    (maxPoints - totalPoints) / unitCost);

            if (maxCanTake > 0) {
                for (int i = 1; i <= maxCanTake; i++) {
                    Unit newUnit = createUnitFromTemplate(unitTemplate, i);
                    armyUnits.add(newUnit);
                }
                totalPoints += maxCanTake * unitCost;

                if (totalPoints >= maxPoints) {
                    break;
                }
            }
        }

        assignCoordinates(armyUnits);
        return new Army(armyUnits);
    }

    private Unit createUnitFromTemplate(Unit template, int number) {
        return new Unit(
                template.getName() + " " + number,
                template.getUnitType(),
                template.getHealth(),
                template.getBaseAttack(),
                template.getCost(),
                template.getAttackType(),
                new HashMap<>(template.getAttackBonuses()),
                new HashMap<>(template.getDefenceBonuses()),
                -1,
                -1
        );
    }

    private void assignCoordinates(List<Unit> armyUnits) {
        List<Unit> rangedUnits = new ArrayList<>();
        List<Unit> meleeUnits = new ArrayList<>();

        for (Unit unit : armyUnits) {
            if (isRangedUnit(unit)) {
                rangedUnits.add(unit);
            } else {
                meleeUnits.add(unit);
            }
        }

        // дальников ставим в дальний ряд, ближников ставим вперед
        // ближний бой: сначала x=2, потом x=1, потом x=0
        int[] meleeUnitPreferredOrder = new int[]{2, 1, 0};

        // дальний бой: сначала x=0, потом x=1, потом x=2
        int[] rangedUnitPreferredOrder = new int[]{0, 1, 2};

        // указатели на текущие Y в каждой колонке [x=0, x=1, x=2]
        int[] colY = new int[3];

        for (Unit unit : rangedUnits) {
            placeUnit(unit, colY, rangedUnitPreferredOrder);
        }

        for (Unit unit : meleeUnits) {
            placeUnit(unit, colY, meleeUnitPreferredOrder);
        }
    }

    private void placeUnit(Unit unit, int[] colY, int[] preferredOrder) {
        for (int col : preferredOrder) {
            if (colY[col] <= MAX_Y) {
                unit.setxCoordinate(col);
                unit.setyCoordinate(colY[col]);
                colY[col]++;
                return;
            }
        }

        // если все предпочтительные колонки заполнены, ищем любую свободную
        for (int col = 0; col < 3; col++) {
            if (colY[col] <= MAX_Y) {
                unit.setxCoordinate(col);
                unit.setyCoordinate(colY[col]);
                colY[col]++;
                return;
            }
        }

        // все колонки заполнены (ошибка)
        unit.setxCoordinate(-1);
        unit.setyCoordinate(-1);
    }

    private boolean isRangedUnit(Unit unit) {
        return "Ranged combat".equals(unit.getAttackType());
    }
}
