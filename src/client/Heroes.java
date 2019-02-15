package client;

import client.model.*;

import static client.model.AbilityName.BLASTER_ATTACK;
import static client.model.AbilityName.GUARDIAN_ATTACK;

abstract public class Heroes {
    abstract double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint);

    abstract double[][] setActionWeight(Hero hero, World world);

    abstract double evaluateMovement(Hero hero, double... stat);

    abstract double evaluateAction(Hero hero, double... stat);
}

class Healer extends Heroes {
    private enum Movement {
        isInObjective,
        isNextInObjPath,
        isInEnemyVision,
        isNextToWall,
        isInGuardianRange,
        isInHealerRange,
        isInSentryLOF,
        isInBombRange,
        isInGuardianAttackRange,
        isInHealerAttackRange,
        isInSentryAttackRange,
        isInBlasterAttackRange,
        isAllyInHealRange, //DEF
    }

    private double[] weightAlloc = {6D, 4D, -2D, 4D, 4D, 1D, -4D, -3D, -2D, -1D, -3D, -3D, 5D};

    @SuppressWarnings("Duplicates")
    double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint) {
        double[][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = hero.getCurrentCell().getRow() - 1; i <= hero.getCurrentCell().getRow() + 1; i++) {
            for (int j = hero.getCurrentCell().getColumn() - 1; j <= hero.getCurrentCell().getColumn() + 1; j++) {
                if(world.getMap().getCell(i,j).isWall())
                    continue;
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += weightAlloc[Movement.isInObjective.ordinal()];
                if (!world.getMap().getCell(i, j).equals(hero.getCurrentCell()) && world.getMap().getCell(i, j).isWall())
                    result[i][j] += weightAlloc[Movement.isNextToWall.ordinal()];
                switch (world.getPathMoveDirections(hero.getCurrentCell(), objectivePoint)[0]) {
                    case UP:
                        if (hero.getCurrentCell().getRow() - i == 1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case DOWN:
                        if (hero.getCurrentCell().getRow() - i == -1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case LEFT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == 1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case RIGHT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == -1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                }
                for (Hero my_hero : world.getOppHeroes()) {
                    switch (my_hero.getAbilities()[0].getName()) {
                        case GUARDIAN_ATTACK:
                        case GUARDIAN_DODGE:
                        case GUARDIAN_FORTIFY:
                            result[i][j] += weightAlloc[Movement.isInGuardianRange.ordinal()];
                        default:
                            if(!my_hero.equals(hero)&&
                                    world.manhattanDistance(hero.getCurrentCell(),my_hero.getCurrentCell())<=4)
                                result[i][j]+=weightAlloc[Movement.isInHealerRange.ordinal()];
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                        result[i][j] += weightAlloc[Movement.isInEnemyVision.ordinal()];
                    }
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
                                        result[i][j] += weightAlloc[Movement.isInBlasterAttackRange.ordinal()];
                                }
                                break;
                            case BLASTER_BOMB:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                    result[i][j] += weightAlloc[Movement.isInBombRange.ordinal()];
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                        result[i][j] += weightAlloc[Movement.isInSentryAttackRange.ordinal()];
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j] += weightAlloc[Movement.isInSentryLOF.ordinal()];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j] += weightAlloc[Movement.isInHealerAttackRange.ordinal()];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j] += weightAlloc[Movement.isInGuardianAttackRange.ordinal()];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }
}

class Sentry extends Heroes {
    private enum Movement {
        isInObjective,
        isNextInObjPath,
        isInEnemyVision,
        isInSentryLOF,
        isNextToWall,
        isInGuardianRange,
        isInHealerRange,
        isInBombRange,
        isInGuardianAttackRange,
        isInHealerAttackRange,
        isInSentryAttackRange,
        isInBlasterAttackRange,
        isEnemyInLOF, //Def
    }

    private double[] weightAlloc = {6D, 4D, -1D, -3D, -2D, 3D, 4D, -4D, -4D, -2D, -3D, -3D, 5D};

    @SuppressWarnings("Duplicates")
    double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint) {
        double[][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = hero.getCurrentCell().getRow() - 1; i <= hero.getCurrentCell().getRow() + 1; i++) {
            for (int j = hero.getCurrentCell().getColumn() - 1; j <= hero.getCurrentCell().getColumn() + 1; j++) {
                if(world.getMap().getCell(i,j).isWall())
                    continue;
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += weightAlloc[Movement.isInObjective.ordinal()];
                if (!world.getMap().getCell(i, j).equals(hero.getCurrentCell()) && world.getMap().getCell(i, j).isWall())
                    result[i][j] += weightAlloc[Movement.isNextToWall.ordinal()];
                switch (world.getPathMoveDirections(hero.getCurrentCell(), objectivePoint)[0]) {
                    case UP:
                        if (hero.getCurrentCell().getRow() - i == 1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case DOWN:
                        if (hero.getCurrentCell().getRow() - i == -1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case LEFT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == 1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case RIGHT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == -1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                }
                for (Hero my_hero : world.getOppHeroes()) {
                    switch (my_hero.getAbilities()[0].getName()) {
                        case GUARDIAN_ATTACK:
                        case GUARDIAN_DODGE:
                        case GUARDIAN_FORTIFY:
                            result[i][j] += weightAlloc[Movement.isInGuardianRange.ordinal()];
                        default:
                            if(!my_hero.equals(hero)&&
                                    world.manhattanDistance(hero.getCurrentCell(),my_hero.getCurrentCell())<=4)
                                result[i][j]+=weightAlloc[Movement.isInHealerRange.ordinal()];
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if(world.isInVision(opp_hero.getCurrentCell(),world.getMap().getCell(i,j))){
                        result[i][j]+=weightAlloc[Movement.isEnemyInLOF.ordinal()];
                    }
                    if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                        result[i][j] += weightAlloc[Movement.isInEnemyVision.ordinal()];
                    }
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
                                        result[i][j] += weightAlloc[Movement.isInBlasterAttackRange.ordinal()];
                                }
                                break;
                            case BLASTER_BOMB:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                    result[i][j] += weightAlloc[Movement.isInBombRange.ordinal()];
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                        result[i][j] += weightAlloc[Movement.isInSentryAttackRange.ordinal()];
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j] += weightAlloc[Movement.isInSentryLOF.ordinal()];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j] += weightAlloc[Movement.isInHealerAttackRange.ordinal()];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j] += weightAlloc[Movement.isInGuardianAttackRange.ordinal()];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }
}

class Guardian extends Heroes {
    public enum Movement {
        isInObjective,
        isNextInObjPath,
        isInEnemyVision,
        isInSentryLOF,
        isNextToWall,
        isInGuardianRange,
        isInHealerRange,
        isInBombRange,
        isInGuardianAttackRange,
        isInHealerAttackRange,
        isInSentryAttackRange,
        isInBlasterAttackRange,
        isAllyInProtectRange,
    }
    private double[] weightAlloc = {7D, 5D, -2D, -2D, -2D, 3D, 4D, -2D, -2D, -2D, -3D, -3D, 5D};

    @SuppressWarnings("Duplicates")
    double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint) {
        double[][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = hero.getCurrentCell().getRow() - 1; i <= hero.getCurrentCell().getRow() + 1; i++) {
            for (int j = hero.getCurrentCell().getColumn() - 1; j <= hero.getCurrentCell().getColumn() + 1; j++) {
                if(world.getMap().getCell(i,j).isWall())
                    continue;
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += weightAlloc[Movement.isInObjective.ordinal()];
                if (!world.getMap().getCell(i, j).equals(hero.getCurrentCell()) && world.getMap().getCell(i, j).isWall())
                    result[i][j] += weightAlloc[Movement.isNextToWall.ordinal()];
                switch (world.getPathMoveDirections(hero.getCurrentCell(), objectivePoint)[0]) {
                    case UP:
                        if (hero.getCurrentCell().getRow() - i == 1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case DOWN:
                        if (hero.getCurrentCell().getRow() - i == -1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case LEFT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == 1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case RIGHT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == -1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                }
                for (Hero my_hero : world.getOppHeroes()) {
                    switch (my_hero.getAbilities()[0].getName()) {
                        case GUARDIAN_ATTACK:
                        case GUARDIAN_DODGE:
                        case GUARDIAN_FORTIFY:
                            result[i][j] += weightAlloc[Movement.isInGuardianRange.ordinal()];
                        default:
                            if(!my_hero.equals(hero)&&
                                    world.manhattanDistance(hero.getCurrentCell(),my_hero.getCurrentCell())<=4)
                                result[i][j]+=weightAlloc[Movement.isInHealerRange.ordinal()];
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                        result[i][j] += weightAlloc[Movement.isInEnemyVision.ordinal()];
                    }
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
                                        result[i][j] += weightAlloc[Movement.isInBlasterAttackRange.ordinal()];
                                }
                                break;
                            case BLASTER_BOMB:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                    result[i][j] += weightAlloc[Movement.isInBombRange.ordinal()];
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                        result[i][j] += weightAlloc[Movement.isInSentryAttackRange.ordinal()];
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j] += weightAlloc[Movement.isInSentryLOF.ordinal()];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j] += weightAlloc[Movement.isInHealerAttackRange.ordinal()];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j] += weightAlloc[Movement.isInGuardianAttackRange.ordinal()];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }


}

class Blaster extends Heroes {
    public enum Movement {
        isInObjective,
        isNextInObjPath,
        isInEnemyVision,
        isInSentryLOF,
        isNextToWall,
        isInGuardianRange,
        isInHealerRange,
        isInBombRange,
        isInGuardianAttackRange,
        isInHealerAttackRange,
        isInSentryAttackRange,
        isInBlasterAttackRange,
        isEnemyInBombRange,
    }
    private double[] weightAlloc = {6D, 4D, -2D, -3D, 2D, 3D, 3D, -3D, -2D, -2D, -3D, -3D, 5D};

    @SuppressWarnings("Duplicates")
    double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint) {
        double[][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = hero.getCurrentCell().getRow() - 1; i <= hero.getCurrentCell().getRow() + 1; i++) {
            for (int j = hero.getCurrentCell().getColumn() - 1; j <= hero.getCurrentCell().getColumn() + 1; j++) {
                if(world.getMap().getCell(i,j).isWall())
                    continue;
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += weightAlloc[Movement.isInObjective.ordinal()];
                if (!world.getMap().getCell(i, j).equals(hero.getCurrentCell()) && world.getMap().getCell(i, j).isWall())
                    result[i][j] += weightAlloc[Movement.isNextToWall.ordinal()];
                switch (world.getPathMoveDirections(hero.getCurrentCell(), objectivePoint)[0]) {
                    case UP:
                        if (hero.getCurrentCell().getRow() - i == 1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case DOWN:
                        if (hero.getCurrentCell().getRow() - i == -1 && hero.getCurrentCell().getColumn() - j == 0)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case LEFT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == 1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                    case RIGHT:
                        if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == -1)
                            result[i][j] += weightAlloc[Movement.isNextInObjPath.ordinal()];
                        break;
                }
                for (Hero my_hero : world.getOppHeroes()) {
                    switch (my_hero.getAbilities()[0].getName()) {
                        case GUARDIAN_ATTACK:
                        case GUARDIAN_DODGE:
                        case GUARDIAN_FORTIFY:
                            result[i][j] += weightAlloc[Movement.isInGuardianRange.ordinal()];
                        default:
                            if(!my_hero.equals(hero)&&
                                    world.manhattanDistance(hero.getCurrentCell(),my_hero.getCurrentCell())<=4)
                                result[i][j]+=weightAlloc[Movement.isInHealerRange.ordinal()];
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7) {
                        result[i][j] += weightAlloc[Movement.isEnemyInBombRange.ordinal()];
                    }
                    if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                        result[i][j] += weightAlloc[Movement.isInEnemyVision.ordinal()];
                    }
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
                                        result[i][j] += weightAlloc[Movement.isInBlasterAttackRange.ordinal()];
                                }
                                break;
                            case BLASTER_BOMB:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                    result[i][j] += weightAlloc[Movement.isInBombRange.ordinal()];
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                        result[i][j] += weightAlloc[Movement.isInSentryAttackRange.ordinal()];
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j] += weightAlloc[Movement.isInSentryLOF.ordinal()];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j] += weightAlloc[Movement.isInHealerAttackRange.ordinal()];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j] += weightAlloc[Movement.isInGuardianAttackRange.ordinal()];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }

}
