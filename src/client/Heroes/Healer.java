package client.Heroes;

import client.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Healer {
    private enum Movement {
        isInObjective,
        isNextInObjPath,
        isInEnemyVision,
        isInGuardianRange,
        isInHealerRange,
        isAllyInHealRange,
        isInSentryLOF,
        isInBombRange,
        isInGuardianAttackRange,
        isInHealerAttackRange,
        isInSentryAttackRange,
        isInBlasterAttackRange,
        isNextToWall,
        stayInPlace
    }
    File file=new File("Healer/Healer_Movement_Weights");
    private double[] movementWeightAlloc=new double[14];

    @SuppressWarnings("Duplicates")
    public double[][] setMovementWeight(Hero hero, World world, Cell objectivePoint) {
        int counter=0;
        Scanner scanner;
        try {
            scanner=new Scanner(file);
            while (scanner.hasNextDouble()){
                movementWeightAlloc[counter]=scanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        boolean heroPercussionFlag=false;
        double[][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = hero.getCurrentCell().getRow() - 1; i <= hero.getCurrentCell().getRow() + 1; i++) {
            for (int j = hero.getCurrentCell().getColumn() - 1; j <= hero.getCurrentCell().getColumn() + 1; j++) {
                if(world.getMap().getCell(i,j).isWall()){
                    result[i][j]=-100;
                    continue;
                }
                for(Hero my_hero: world.getMyHeroes())
                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j))&&!my_hero.equals(hero)){
                        result[i][j]=-100;
                        heroPercussionFlag=true;
                    }
                if(heroPercussionFlag){
                    heroPercussionFlag=false;
                    continue;
                }
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += movementWeightAlloc[Movement.isInObjective.ordinal()];
                if(world.getMap().getCell(i,j).equals(hero.getCurrentCell()))
                    result[i][j] += movementWeightAlloc[Movement.stayInPlace.ordinal()];
                if (!world.getMap().getCell(i, j).equals(hero.getCurrentCell()) && world.getMap().getCell(i, j).isWall())
                    result[i][j] += movementWeightAlloc[Movement.isNextToWall.ordinal()];
                if(world.getPathMoveDirections(hero.getCurrentCell(),objectivePoint).length!=0){
                    switch (world.getPathMoveDirections(hero.getCurrentCell(), objectivePoint)[0]) {
                        case UP:
                            if (hero.getCurrentCell().getRow() - i == 1 && hero.getCurrentCell().getColumn() - j == 0)
                                result[i][j] += movementWeightAlloc[Movement.isNextInObjPath.ordinal()];
                            break;
                        case DOWN:
                            if (hero.getCurrentCell().getRow() - i == -1 && hero.getCurrentCell().getColumn() - j == 0)
                                result[i][j] += movementWeightAlloc[Movement.isNextInObjPath.ordinal()];
                            break;
                        case LEFT:
                            if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == 1)
                                result[i][j] += movementWeightAlloc[Movement.isNextInObjPath.ordinal()];
                            break;
                        case RIGHT:
                            if (hero.getCurrentCell().getRow() - i == 0 && hero.getCurrentCell().getColumn() - j == -1)
                                result[i][j] += movementWeightAlloc[Movement.isNextInObjPath.ordinal()];
                            break;
                    }
                }
                for (Hero my_hero : world.getMyHeroes()) {
                    switch (my_hero.getAbilities()[0].getName()) {
                        case GUARDIAN_ATTACK:
                        case GUARDIAN_DODGE:
                        case GUARDIAN_FORTIFY:
                            result[i][j] += movementWeightAlloc[Movement.isInGuardianRange.ordinal()];
                            break;
                        case HEALER_DODGE:
                        case HEALER_HEAL:
                        case HEALER_ATTACK:
                            if(!my_hero.equals(hero))
                                result[i][j] += movementWeightAlloc[Movement.isInHealerRange.ordinal()];
                            break;
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                        result[i][j] += movementWeightAlloc[Movement.isInEnemyVision.ordinal()];
                    }
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
                                        result[i][j] += movementWeightAlloc[Movement.isInBlasterAttackRange.ordinal()];
                                }
                                break;
                            case BLASTER_BOMB:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                    result[i][j] += movementWeightAlloc[Movement.isInBombRange.ordinal()];
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
                                        result[i][j] += movementWeightAlloc[Movement.isInSentryAttackRange.ordinal()];
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j] += movementWeightAlloc[Movement.isInSentryLOF.ordinal()];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j] += movementWeightAlloc[Movement.isInHealerAttackRange.ordinal()];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j] += movementWeightAlloc[Movement.isInGuardianAttackRange.ordinal()];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private enum Attack{
        isLethalN
    }
    private enum Heal{
        isTargetBelow50,
        isTargetInSentryLOF,
        isTargetOnObjective,
        isTargetHealer,
        isTargetSentry,
        isTargetBlaster,
        isTargetGuardian
    }
    private enum Dodge{
        isInSentryLethalCondition,
        isInBlasterLethalCondition,
        isAllyInGuardRange,
        isOnObjective
    }
    private File attackFile=new File("Healer/Healer_Attack_Weight");
    private File healFile=new File("Healer/Healer_Heal_Weights");
    private File dodgeFile=new File("Healer/Healer_Dodge_Weights");
    private double[] actionWeightAlloc=new double[17];
    @SuppressWarnings("Duplicates")
    public double[][][] setActionWeight(Hero hero, World world) {
        int counter=0;
        Scanner attackScanner,healScanner,dodgeScanner;
        try {
            attackScanner=new Scanner(attackFile);
            while (attackScanner.hasNextDouble()){
                actionWeightAlloc[counter]=attackScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        counter=0;
        try {
            healScanner=new Scanner(healFile);
            while (healScanner.hasNextDouble()){
                actionWeightAlloc[counter]=healScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        counter=0;
        try {
            dodgeScanner=new Scanner(dodgeFile);
            while (dodgeScanner.hasNextDouble()){
                actionWeightAlloc[counter]=dodgeScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        boolean heroPercussionFlag=false;
        double[][][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()][3];
        for (int i = 0; i < world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                if (world.getMap().getCell(i, j).isWall()) {
                    result[i][j][AbilityName.HEALER_ATTACK.ordinal()%3] = result[i][j][AbilityName.HEALER_HEAL.ordinal()%3]
                            = result[i][j][AbilityName.HEALER_DODGE.ordinal()%3] = -100D;
                    continue;
                }
                outer:
                for (Hero my_hero : world.getMyHeroes()) {
                    for (Hero opp_hero : world.getOppHeroes()) {
                        if (opp_hero.getCurrentCell().equals(my_hero.getCurrentCell()))
                            continue outer;
                    }
                    result[i][j][AbilityName.HEALER_ATTACK.ordinal()%3] = result[i][j][AbilityName.HEALER_HEAL.ordinal()%3]
                            = result[i][j][AbilityName.HEALER_DODGE.ordinal()%3] = -100D;
                }

                //Attack

                for (Hero opp_hero : world.getOppHeroes()) {
                    if (opp_hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                        if(hero.getAbilities()[AbilityName.HEALER_ATTACK.ordinal()%3].getRemCooldown() == 0)
                            if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=4){
                                if(opp_hero.getCurrentHP()<=25)
                                    result[i][j][AbilityName.HEALER_ATTACK.ordinal()%3] +=
                                            actionWeightAlloc[Attack.isLethalN.ordinal()];
                            }
                    }
                }

                //Heal

                for (Hero my_hero : world.getMyHeroes()) {
                    if (my_hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                        if(hero.getAbilities()[AbilityName.HEALER_HEAL.ordinal()%3].getRemCooldown() == 0){
                            if(world.manhattanDistance(my_hero.getCurrentCell(),hero.getCurrentCell())<=4){
                                if(my_hero.getCurrentHP()<=50)
                                    result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                            actionWeightAlloc[Heal.isTargetBelow50.ordinal()];
                                if(my_hero.getCurrentCell().isInObjectiveZone())
                                    result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                            actionWeightAlloc[Heal.isTargetOnObjective.ordinal()];
                                switch (my_hero.getName()){
                                    case SENTRY:
                                        result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                                actionWeightAlloc[Heal.isTargetSentry.ordinal()];
                                        break;
                                    case BLASTER:
                                        result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                                actionWeightAlloc[Heal.isTargetBlaster.ordinal()];
                                        break;
                                    case GUARDIAN:
                                        result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                                actionWeightAlloc[Heal.isTargetGuardian.ordinal()];
                                        break;
                                    case HEALER:
                                        result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                                actionWeightAlloc[Heal.isTargetHealer.ordinal()];
                                        break;
                                }
                                for(Hero opp_hero : world.getOppHeroes()){
                                    if(opp_hero.getName()== HeroName.SENTRY){
                                        if(world.isInVision(my_hero.getCurrentCell(),opp_hero.getCurrentCell()))
                                            result[i][j][AbilityName.HEALER_HEAL.ordinal()%3] +=
                                                    actionWeightAlloc[Heal.isTargetInSentryLOF.ordinal()];
                                    }
                                }
                            }
                        }
                    }
                }

                //Dodge

                for (Hero opp_hero : world.getOppHeroes()) {
                    switch (opp_hero.getName()) {
                        case SENTRY:
                            if (world.isInVision(hero.getCurrentCell(), opp_hero.getCurrentCell())
                                    && hero.getCurrentHP() < 51)
                                result[i][j][AbilityName.HEALER_DODGE.ordinal() % 3] += actionWeightAlloc[Dodge.isInSentryLethalCondition.ordinal()];
                            if (world.isInVision(hero.getCurrentCell(), opp_hero.getCurrentCell())
                                    && world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 8 && hero.getCurrentHP() < 31)
                                result[i][j][AbilityName.HEALER_DODGE.ordinal() % 3] += actionWeightAlloc[Dodge.isInSentryLethalCondition.ordinal()];
                            break;
                        case BLASTER:
                            if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 6 && hero.getCurrentHP() < 41)
                                result[i][j][AbilityName.HEALER_DODGE.ordinal() % 3] += actionWeightAlloc[Dodge.isInBlasterLethalCondition.ordinal()];
                            if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 5 && hero.getCurrentHP() < 21)
                                result[i][j][AbilityName.HEALER_DODGE.ordinal() % 3] += actionWeightAlloc[Dodge.isInBlasterLethalCondition.ordinal()];
                            break;
                    }
                    for (Cell objcell : world.getMap().getObjectiveZone()) {
                        if (objcell.getRow() == i && objcell.getColumn() == j)
                            result[i][j][AbilityName.HEALER_DODGE.ordinal() % 3] += actionWeightAlloc[Dodge.isOnObjective.ordinal()];

                    }
                }
            }
        }
        return result;
    }

}
