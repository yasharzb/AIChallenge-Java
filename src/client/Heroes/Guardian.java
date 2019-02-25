package client.Heroes;

import client.model.AbilityName;
import client.model.Cell;
import client.model.Hero;
import client.model.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static client.model.HeroName.BLASTER;

public class Guardian {

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
        stayInPlace
    }

    private File file=new File("Guardian/Guardian_Movement_Weight");
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
                if(world.getMap().getCell(i,j).isWall()) {
                    result[i][j]=-100;
                    continue;
                }
                for(Hero my_hero: world.getMyHeroes())
                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j))&&!my_hero.equals(hero)){
                        heroPercussionFlag=true;
                        result[i][j]=-100;
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
                            if(!my_hero.equals(hero))
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
    private enum Guard{
        isInSentryLethalCondition,
        isInBlasterLethalCondition
    }
    private enum Dodge{
        isInSentryLethalCondition,
        isInBlasterLethalCondition,
        isAllyInGuardRange,
        isOnObjective
    }
    private File attackFile=new File("Guardian/Guardian_Attack_Weights");
    private File guardFile=new File("Guardian/Guardian_Guard_Weights");
    private File dodgeFile=new File("Guardian/Guardian_Dodge_Weights");
    private double[] attackWieghtAlloc=new double[5];
    private double[] guardWeightAlloc=new double[2];
    private double[] dodgeWeightAlloc=new double[4];
    @SuppressWarnings("Duplicates")
    public double[][][] setActionWeight(Hero hero, World world,Cell objectivePoint) {
        int counter=0;
        Scanner attackScanner,guardScanner,dodgeScanner;
        try {
            attackScanner=new Scanner(attackFile);
            while (attackScanner.hasNextDouble()){
                attackWieghtAlloc[counter]=attackScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        counter=0;
        try {
            guardScanner=new Scanner(guardFile);
            while (guardScanner.hasNextDouble()){
                guardWeightAlloc[counter]=guardScanner.nextDouble();
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
                dodgeWeightAlloc[counter]=dodgeScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        boolean heroPercussionFlag=false;
        double[][][] result = new double[world.getMap().getRowNum()][world.getMap().getColumnNum()][3];
        for(int i=objectivePoint.getRow();i<objectivePoint.getRow()+5;i++){
            for(int j=objectivePoint.getColumn();j<objectivePoint.getColumn()+5;j++){
                if (world.getMap().getCell(i, j).isWall()) {
                    result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] = result[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3]
                            = result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3] = -100D;
                    continue;
                }

                //Attack

                for (Hero opp_hero : world.getOppHeroes()) {
                    if (opp_hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                        switch (opp_hero.getName()) {
                            case BLASTER:
                                if(hero.getAbilities()[AbilityName.GUARDIAN_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=2){
                                        result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                attackWieghtAlloc[Attack.isBlasterInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=40)
                                            result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                    attackWieghtAlloc[Attack.isLethalN.ordinal()];
                                    }
                                break;
                            case SENTRY:
                                if(hero.getAbilities()[AbilityName.GUARDIAN_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=2){
                                        result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                attackWieghtAlloc[Attack.isSentryInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=40)
                                            result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                    attackWieghtAlloc[Attack.isLethalN.ordinal()];
                                    }
                                break;
                            case HEALER:
                                if(hero.getAbilities()[AbilityName.GUARDIAN_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=2){
                                        result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                attackWieghtAlloc[Attack.isHealerInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=40)
                                            result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                    attackWieghtAlloc[Attack.isLethalN.ordinal()];
                                    }
                                break;
                            case GUARDIAN:
                                if(hero.getAbilities()[AbilityName.GUARDIAN_ATTACK.ordinal()%3].getRemCooldown() == 0)
                                    if(world.manhattanDistance(opp_hero.getCurrentCell(),hero.getCurrentCell())<=2){
                                        result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                attackWieghtAlloc[Attack.isGuardianInRange.ordinal()];
                                        if(opp_hero.getCurrentHP()<=40)
                                            result[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3] +=
                                                    attackWieghtAlloc[Attack.isLethalN.ordinal()];
                                    }
                                break;
                        }
                    }
                }

                //Guard

                for (Hero my_hero : world.getMyHeroes()) {
                    if (my_hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                        if(hero.getAbilities()[AbilityName.GUARDIAN_FORTIFY.ordinal()%3].getRemCooldown() == 0){
                            if(world.manhattanDistance(my_hero.getCurrentCell(),hero.getCurrentCell())<=4){
                                for(Hero opp_hero : world.getOppHeroes()){
                                    switch (opp_hero.getName()){
                                        case BLASTER:
                                            if(world.isInVision(my_hero.getCurrentCell(),opp_hero.getCurrentCell())){
                                                if(world.manhattanDistance(my_hero.getCurrentCell(),opp_hero.getCurrentCell())
                                                    <=5)
                                                    result[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3] +=
                                                            guardWeightAlloc[Guard.isInBlasterLethalCondition.ordinal()];
                                            }
                                            else {
                                                if(world.manhattanDistance(my_hero.getCurrentCell(),opp_hero.getCurrentCell())
                                                        <=7)
                                                    result[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3] +=
                                                            guardWeightAlloc[Guard.isInBlasterLethalCondition.ordinal()];
                                            }
                                            break;
                                        case SENTRY:
                                            if(world.isInVision(my_hero.getCurrentCell(),opp_hero.getCurrentCell()))
                                                result[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3] +=
                                                        guardWeightAlloc[Guard.isInSentryLethalCondition.ordinal()];
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

                //Dodge
                for (Hero opp_hero : world.getOppHeroes())
                {
                    switch(opp_hero.getName()){
                        case SENTRY:
                            if (world.isInVision(hero.getCurrentCell(),opp_hero.getCurrentCell())
                                    && hero.getCurrentHP()<51)
                                result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]+= dodgeWeightAlloc[Dodge.isInSentryLethalCondition.ordinal()];
                            if (world.isInVision(hero.getCurrentCell(),opp_hero.getCurrentCell())
                                    && world.manhattanDistance(hero.getCurrentCell(),opp_hero.getCurrentCell())<8 && hero.getCurrentHP()<31)
                                result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]+= dodgeWeightAlloc[Dodge.isInSentryLethalCondition.ordinal()];
                            break;
                        case BLASTER:
                            if(world.manhattanDistance(hero.getCurrentCell(),opp_hero.getCurrentCell())<6 && hero.getCurrentHP()<41)
                                result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]+= dodgeWeightAlloc[Dodge.isInBlasterLethalCondition.ordinal()];
                            if(world.manhattanDistance(hero.getCurrentCell(),opp_hero.getCurrentCell())<5 && hero.getCurrentHP()<21)
                                result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]+= dodgeWeightAlloc[Dodge.isInBlasterLethalCondition.ordinal()];
                            break;
                    }
                    for (Cell objcell: world.getMap().getObjectiveZone())
                    {
                        if (objcell.getRow()==i && objcell.getColumn()==j)
                            result[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]+=dodgeWeightAlloc[Dodge.isOnObjective.ordinal()];

                    }

                    for (Hero allies : world.getMyHeroes()) {
                        if (allies!=hero)
                            if (world.manhattanDistance(world.getMap().getCell(i, j), allies.getCurrentCell()) < 5) {
                                result[i][j][AbilityName.GUARDIAN_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isAllyInGuardRange.ordinal()];
                                break;
                            }
                    }
                }
            }
        }
        return result;
    }


}
