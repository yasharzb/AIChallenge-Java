package client.Heroes;

import client.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Sentry {
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
        isEnemyInLOF,
        stayInPlace
    }
    File file=new File("/home/yashyzb/Downloads/Compressed/AIC19-Client-Java-1.0/Sentry_Movement_Weights");
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
        for (int i=0;i<world.getMap().getRowNum();i++)
            for (int j=0;j<world.getMap().getColumnNum();j++)
                result[i][j]=(double)0;
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
                            result[i][j] += movementWeightAlloc[Movement.isInHealerRange.ordinal()];
                            break;
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if(world.isInVision(opp_hero.getCurrentCell(),world.getMap().getCell(i,j))){
                        result[i][j]+=movementWeightAlloc[Movement.isEnemyInLOF.ordinal()];
                    }
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
        isHealerInRange,
        isSentryInRange,
        isBlasterInRange,
        isGuardianInRange,
        isLethalN
    }
    private enum Beam{
        isHealerInRange,
        isSentryInRange,
        isBlasterInRange,
        isGuardianInRange,
        isLethalN,
        isFarAway
    }
    private enum Dodge{
        isInSentryLOFandLethalandBeamOffCD,
        isInBlasterRangeandBlastOffCD,
        isInSentryAttackRange,
        isHealerinLOF,
        isSentryinLOF,
        isBlasterinLOF,
        isOnObjective
    }
    private File attackFile=new File("Sentry/Sentry_Attack_Weights");
    private File beamFile=new File("Sentry/Sentry_Beam_Weights");
    private File dodgeFile=new File("Sentry/Sentry_Dodge_Weights");
    private double[] actionWeightAlloc=new double[17];
    @SuppressWarnings("Duplicates")
    public double[][][] setActionWeight(Hero hero, World world, Cell objectivePoint) {
        int counter=0;
        Scanner attackScanner,beamScanner,dodgeScanner;
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
        try {
            beamScanner=new Scanner(beamFile);
            while (beamScanner.hasNextDouble()){
                actionWeightAlloc[counter]=beamScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
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
        for (int i = 0; i <= world.getMap().getRowNum(); i++) {
            for (int j = 0; j <= world.getMap().getColumnNum(); j++) {
                if (world.getMap().getCell(i, j).isWall()) {
                    result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] = result[i][j][AbilityName.SENTRY_RAY.ordinal()%3]
                            = result[i][j][AbilityName.SENTRY_DODGE.ordinal()%3] = -100D;
                    continue;
                }
                outer:
                for (Hero my_hero : world.getMyHeroes()) {
                    for (Hero opp_hero : world.getOppHeroes()) {
                        if (opp_hero.getCurrentCell().equals(my_hero.getCurrentCell()))
                            continue outer;
                    }
                    result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] = result[i][j][AbilityName.SENTRY_RAY.ordinal()%3]
                            = result[i][j][AbilityName.SENTRY_DODGE.ordinal()%3] = -100D;
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    if (opp_hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                        switch (opp_hero.getName()) {
                            case BLASTER:
                                if(hero.getAbilities()[AbilityName.SENTRY_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell()))
                                        if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=7){
                                            result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                    actionWeightAlloc[Attack.isBlasterInRange.ordinal()];
                                            if(opp_hero.getCurrentHP()<=30)
                                                result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                        actionWeightAlloc[Attack.isLethalN.ordinal()];
                                        }
                                if(hero.getAbilities()[AbilityName.SENTRY_RAY.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell())){
                                        result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                actionWeightAlloc[Beam.isBlasterInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=50)
                                            result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                    actionWeightAlloc[Beam.isLethalN.ordinal()];
                                    }
                                break;
                            case SENTRY:
                                if(hero.getAbilities()[AbilityName.SENTRY_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell()))
                                        if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=7){
                                            result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                    actionWeightAlloc[Attack.isSentryInRange.ordinal()];
                                            if(opp_hero.getCurrentHP()<=30)
                                                result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                        actionWeightAlloc[Attack.isLethalN.ordinal()];
                                        }
                                if(hero.getAbilities()[AbilityName.SENTRY_RAY.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell())){
                                        result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                actionWeightAlloc[Beam.isSentryInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=50)
                                            result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                    actionWeightAlloc[Beam.isLethalN.ordinal()];
                                    }
                                break;
                            case HEALER:
                                if(hero.getAbilities()[AbilityName.SENTRY_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell()))
                                        if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=7){
                                            result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                    actionWeightAlloc[Attack.isHealerInRange.ordinal()];
                                            if(opp_hero.getCurrentHP()<=30)
                                                result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                        actionWeightAlloc[Attack.isLethalN.ordinal()];
                                        }
                                if(hero.getAbilities()[AbilityName.SENTRY_RAY.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell())){
                                        result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                actionWeightAlloc[Beam.isHealerInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=50)
                                            result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                    actionWeightAlloc[Beam.isLethalN.ordinal()];
                                    }
                                break;
                            case GUARDIAN:
                                if(hero.getAbilities()[AbilityName.SENTRY_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell()))
                                        if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=7){
                                            result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                    actionWeightAlloc[Attack.isGuardianInRange.ordinal()];
                                            if(opp_hero.getCurrentHP()<=30)
                                                result[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3] +=
                                                        actionWeightAlloc[Attack.isLethalN.ordinal()];
                                        }
                                if(hero.getAbilities()[AbilityName.SENTRY_RAY.ordinal()%3].getRemCooldown() == 0)
                                    if(world.isInVision(opp_hero.getCurrentCell(),hero.getCurrentCell())){
                                        result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                actionWeightAlloc[Beam.isGuardianInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=50)
                                            result[i][j][AbilityName.SENTRY_RAY.ordinal()%3] +=
                                                    actionWeightAlloc[Beam.isLethalN.ordinal()];
                                    }
                                break;
                        }
                    }
                }
                for (Hero opp_hero : world.getOppHeroes()) {
                    for (int ab = 0; ab < 3; ab++) {
                        switch (opp_hero.getAbilities()[ab].getName()) {
                            case BLASTER_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
//                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 5)
/*
                                        result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                                actionWeightAlloc[Dodge..ordinal()%3];
*/
                                }
                                break;
                            case BLASTER_BOMB:
//                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
/*
                                    result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                            actionWeightAlloc[AbilityName.BLASTER_DODGE.ordinal()%3];
*/
                                break;
                            case SENTRY_ATTACK:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
//                                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7)
/*
                                        result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                                actionWeightAlloc[Dodge.ordinal()%3];
*/
                                }
                                break;
                            case SENTRY_RAY:
                                if (world.isInVision(opp_hero.getCurrentCell(), world.getMap().getCell(i, j))) {
                                    result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                            actionWeightAlloc[AbilityName.BLASTER_DODGE.ordinal()%3];
                                }
                                break;
                            case HEALER_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 4)
                                    result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                            actionWeightAlloc[AbilityName.BLASTER_DODGE.ordinal()%3];
                                break;
                            case GUARDIAN_ATTACK:
                                if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 2)
                                    result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3] +=
                                            actionWeightAlloc[AbilityName.BLASTER_DODGE.ordinal()%3];
                                break;
                        }
                    }
                }
            }
        }
        return result;
    }

}

