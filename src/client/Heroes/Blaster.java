package client.Heroes;

import client.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static client.model.HeroName.HEALER;

public class Blaster {

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
        stayInPlace
    }
    private File file=new File("Blaster/Blaster_Movement_Weights");
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
        for (int i = Math.max(hero.getCurrentCell().getRow() - 1,0); i <= Math.min(hero.getCurrentCell().getRow() + 1,world.getMap().getRowNum()); i++) {
            for (int j = Math.max(hero.getCurrentCell().getColumn() - 1,0); j <= Math.min(hero.getCurrentCell().getColumn() + 1,world.getMap().getColumnNum()); j++) {
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
                if(world.getMap().getCell(i,j).equals(hero.getCurrentCell()))
                    result[i][j] += movementWeightAlloc[Movement.stayInPlace.ordinal()];
                if (world.getMap().getCell(i, j).isInObjectiveZone())
                    result[i][j] += movementWeightAlloc[Movement.isInObjective.ordinal()];
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
                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) <= 7) {
                        result[i][j] += movementWeightAlloc[Movement.isEnemyInBombRange.ordinal()];
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
    private enum Bomb{
        isHealerInRange,
        isSentryInRange,
        isBlasterInRange,
        isGuardianInRange,
        isLethalN
    }
    private enum Dodge{
        isInSentryLOFandLethalandBeamOffCD,
        isInBlasterRangeandBlastOffCD,
        isInSentryAttackRange,
        isHealerinLOF,
        isSentryinLOF,
        isBlasterinLOF,
        isInObjective
    }
    private File attackFile=new File("Blaster/Blaster_Attack_Weights");
    private File bombFile=new File("Blaster/Blaster_Bomb_Weights");
    private File dodgeFile=new File("Blaster/Blaster_Dodge_Weights");
    private double[] attackWeightAlloc=new double[5];
    private double[] bombWeightAlloc=new double[5];
    private double[] dodgeWeightAlloc=new double[7];
    @SuppressWarnings("Duplicates")
    public double[][][] setActionWeight(Hero hero, World world,Cell objectivePoint) {
        int counter=0;
        Scanner attackScanner,bombScanner,dodgeScanner;
        try {
            attackScanner=new Scanner(attackFile);
            while (attackScanner.hasNextDouble()){
                attackWeightAlloc[counter]=attackScanner.nextDouble();
                counter++;
            }
        }
        catch (FileNotFoundException error){
            error.printStackTrace();
        }
        counter=0;
        try {
            bombScanner=new Scanner(bombFile);
            while (bombScanner.hasNextDouble()){
                bombWeightAlloc[counter]=bombScanner.nextDouble();
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
        for(int i=0;i<world.getMap().getRowNum();i++){
            for(int j=0;j<world.getMap().getColumnNum();j++){
                result[i][j][0]=result[i][j][1]=result[i][j][2]=0;
            }
        }

        //Attack

        for (Hero opp_hero : world.getOppHeroes()) {
            switch (opp_hero.getName()) {
                case BLASTER:
                    if (hero.getAbilities()[AbilityName.BLASTER_ATTACK.ordinal()%3].getRemCooldown() == 0)
                        if (world.isInVision(opp_hero.getCurrentCell(), hero.getCurrentCell())) {
                            if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 4) {
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()]
                                        [AbilityName.BLASTER_ATTACK.ordinal() % 3]+=attackWeightAlloc[Attack.isBlasterInRange.ordinal()];
                                if (opp_hero.getCurrentHP() <= 20)
                                    result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()]
                                            [AbilityName.BLASTER_ATTACK.ordinal() % 3]+=attackWeightAlloc[Attack.isLethalN.ordinal()];
                            }
                            else if(world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5){
                                outer:
                                for(int i=opp_hero.getCurrentCell().getRow()-1;i<=opp_hero.getCurrentCell().getRow()+1;i++){
                                    for(int j=opp_hero.getCurrentCell().getColumn()-1;j<=opp_hero.getCurrentCell().getColumn()+1;j++){
                                        if(world.manhattanDistance(world.getMap().getCell(i,j),hero.getCurrentCell())<=4){
                                            result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3]+=attackWeightAlloc[Attack.isBlasterInRange.ordinal()];
                                            if (opp_hero.getCurrentHP() <= 20)
                                                result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3]+=attackWeightAlloc[Attack.isLethalN.ordinal()];
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    if (hero.getAbilities()[AbilityName.BLASTER_BOMB.ordinal()%3].getRemCooldown() == 0) {
                        if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                            result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                    bombWeightAlloc[Bomb.isBlasterInRange.ordinal()];
                            if (opp_hero.getCurrentHP() <= 40)
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                        bombWeightAlloc[Bomb.isLethalN.ordinal()];
                        }
                        else if(world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 7){
                            outer:
                            for(int i=opp_hero.getCurrentCell().getRow()-2;i<=opp_hero.getCurrentCell().getRow()+2;i++){
                                for(int j=opp_hero.getCurrentCell().getColumn()-2;j<=opp_hero.getCurrentCell().getColumn()+2;j++){
                                    if(world.manhattanDistance(world.getMap().getCell(i,j),hero.getCurrentCell())<=4){
                                        result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3]+=bombWeightAlloc[Bomb.isBlasterInRange.ordinal()];
                                        if (opp_hero.getCurrentHP() <= 20)
                                            result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3]+=bombWeightAlloc[Bomb.isLethalN.ordinal()];
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case SENTRY:
                    if (hero.getAbilities()[AbilityName.BLASTER_ATTACK.ordinal()%3].getRemCooldown() == 0)
                        if (world.isInVision(opp_hero.getCurrentCell(), hero.getCurrentCell())) {
                            if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 4) {
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                        attackWeightAlloc[Attack.isSentryInRange.ordinal()];
                                if (opp_hero.getCurrentHP() <= 20)
                                    result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                            attackWeightAlloc[Attack.isLethalN.ordinal()];
                            } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                                outer:
                                for (int i = opp_hero.getCurrentCell().getRow() - 1; i <= opp_hero.getCurrentCell().getRow() + 1; i++) {
                                    for (int j = opp_hero.getCurrentCell().getColumn() - 1; j <= opp_hero.getCurrentCell().getColumn()+1; j++) {
                                        if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                            result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isSentryInRange.ordinal()];
                                            if (opp_hero.getCurrentHP() <= 20)
                                                result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isLethalN.ordinal()];
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    if (hero.getAbilities()[AbilityName.BLASTER_BOMB.ordinal()%3].getRemCooldown() == 0) {
                        if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                            result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                    bombWeightAlloc[Bomb.isSentryInRange.ordinal()];
                            if (opp_hero.getCurrentHP() <= 40)
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                        bombWeightAlloc[Bomb.isLethalN.ordinal()];
                        } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 7) {
                            outer:
                            for (int i = opp_hero.getCurrentCell().getRow() - 2; i <= opp_hero.getCurrentCell().getRow() + 2; i++) {
                                for (int j = opp_hero.getCurrentCell().getColumn() - 2; j <= opp_hero.getCurrentCell().getColumn() + 2; j++) {
                                    if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                        result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isSentryInRange.ordinal()];
                                        if (opp_hero.getCurrentHP() <= 20)
                                            result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isLethalN.ordinal()];
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case HEALER:
                    if (hero.getAbilities()[AbilityName.BLASTER_ATTACK.ordinal()%3].getRemCooldown() == 0)
                        if (world.isInVision(opp_hero.getCurrentCell(), hero.getCurrentCell())) {
                            if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 4) {
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                        attackWeightAlloc[Attack.isHealerInRange.ordinal()];
                                if (opp_hero.getCurrentHP() <= 20)
                                    result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                            attackWeightAlloc[Attack.isLethalN.ordinal()];
                            } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                                outer:
                                for (int i = opp_hero.getCurrentCell().getRow() - 1; i <= opp_hero.getCurrentCell().getRow() + 1; i++) {
                                    for (int j = opp_hero.getCurrentCell().getColumn() - 1; j <= opp_hero.getCurrentCell().getColumn()+1; j++) {
                                        if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                            result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isHealerInRange.ordinal()];
                                            if (opp_hero.getCurrentHP() <= 20)
                                                result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isLethalN.ordinal()];
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    if (hero.getAbilities()[AbilityName.BLASTER_BOMB.ordinal()%3].getRemCooldown() == 0) {
                        if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                            result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                    bombWeightAlloc[Bomb.isHealerInRange.ordinal()];
                            if (opp_hero.getCurrentHP() <= 40)
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                        bombWeightAlloc[Bomb.isLethalN.ordinal()];
                        } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 7) {
                            outer:
                            for (int i = opp_hero.getCurrentCell().getRow() - 2; i <= opp_hero.getCurrentCell().getRow() + 2; i++) {
                                for (int j = opp_hero.getCurrentCell().getColumn() - 2; j <= opp_hero.getCurrentCell().getColumn() + 2; j++) {
                                    if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                        result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isHealerInRange.ordinal()];
                                        if (opp_hero.getCurrentHP() <= 20)
                                            result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isLethalN.ordinal()];
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case GUARDIAN:
                    if (hero.getAbilities()[AbilityName.BLASTER_ATTACK.ordinal()%3].getRemCooldown() == 0)
                        if (world.isInVision(opp_hero.getCurrentCell(), hero.getCurrentCell())) {
                            if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 4) {
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                        attackWeightAlloc[Attack.isGuardianInRange.ordinal()];
                                if (opp_hero.getCurrentHP() <= 20)
                                    result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_ATTACK.ordinal() % 3] +=
                                            attackWeightAlloc[Attack.isLethalN.ordinal()];
                            } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                                outer:
                                for (int i = opp_hero.getCurrentCell().getRow() - 1; i <= opp_hero.getCurrentCell().getRow() + 1; i++) {
                                    for (int j = opp_hero.getCurrentCell().getColumn() - 1; j <= opp_hero.getCurrentCell().getColumn()+1; j++) {
                                        if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                            result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isGuardianInRange.ordinal()];
                                            if (opp_hero.getCurrentHP() <= 20)
                                                result[i][j][AbilityName.BLASTER_ATTACK.ordinal() % 3] += attackWeightAlloc[Attack.isLethalN.ordinal()];
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    if (hero.getAbilities()[AbilityName.BLASTER_BOMB.ordinal()%3].getRemCooldown() == 0) {
                        if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 5) {
                            result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                    bombWeightAlloc[Bomb.isGuardianInRange.ordinal() % 3];
                            if (opp_hero.getCurrentHP() <= 40)
                                result[opp_hero.getCurrentCell().getRow()][opp_hero.getCurrentCell().getColumn()][AbilityName.BLASTER_BOMB.ordinal() % 3] +=
                                        bombWeightAlloc[Bomb.isLethalN.ordinal()];
                        } else if (world.manhattanDistance(opp_hero.getCurrentCell(), hero.getCurrentCell()) <= 7) {
                            outer:
                            for (int i = opp_hero.getCurrentCell().getRow() - 2; i <= opp_hero.getCurrentCell().getRow() + 2; i++) {
                                for (int j = opp_hero.getCurrentCell().getColumn() - 2; j <= opp_hero.getCurrentCell().getColumn() + 2; j++) {
                                    if (world.manhattanDistance(world.getMap().getCell(i, j), hero.getCurrentCell()) <= 4) {
                                        result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isGuardianInRange.ordinal()];
                                        if (opp_hero.getCurrentHP() <= 20)
                                            result[i][j][AbilityName.BLASTER_BOMB.ordinal() % 3] += bombWeightAlloc[Bomb.isLethalN.ordinal()];
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }

        //Dodge

/*
        for (Hero opp_hero : world.getOppHeroes()) {
            switch (opp_hero.getName()) {
                case SENTRY:
                    if (world.isInVision(hero.getCurrentCell(), opp_hero.getCurrentCell())
                            && hero.getCurrentHP() < 51)
                        result[i][j][AbilityName.BLASTER_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isInSentryLOFandLethalandBeamOffCD.ordinal()];
                    if (world.isInVision(hero.getCurrentCell(), opp_hero.getCurrentCell())
                            && world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 8 && hero.getCurrentHP() < 31)
                        result[i][j][AbilityName.BLASTER_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isInSentryAttackRange.ordinal()];
                    break;
                case BLASTER:
                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 6 && hero.getCurrentHP() < 41)
                        result[i][j][AbilityName.BLASTER_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isInBlasterRangeandBlastOffCD.ordinal()];
                    if (world.manhattanDistance(hero.getCurrentCell(), opp_hero.getCurrentCell()) < 5 && hero.getCurrentHP() < 21)
                        result[i][j][AbilityName.BLASTER_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isInBlasterRangeandBlastOffCD.ordinal()];
                    break;
            }
            for (Cell objcell : world.getMap().getObjectiveZone()) {
                if (objcell.getRow() == i && objcell.getColumn() == j)
                    result[i][j][AbilityName.BLASTER_DODGE.ordinal() % 3] += dodgeWeightAlloc[Dodge.isInObjective.ordinal()];

            }
            for (Hero enemyHero: world.getOppHeroes())
            {
                switch (enemyHero.getName())
                {
                    case HEALER:
                        if (world.manhattanDistance(enemyHero.getCurrentCell(),hero.getCurrentCell())<6)
                            result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3]+=dodgeWeightAlloc[Dodge.isHealerinLOF.ordinal()];
                        break;
                    case BLASTER:
                        if (world.manhattanDistance(enemyHero.getCurrentCell(),hero.getCurrentCell())<6)
                            result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3]+=dodgeWeightAlloc[Dodge.isBlasterinLOF.ordinal()];
                        break;
                    case SENTRY:
                        if (world.manhattanDistance(enemyHero.getCurrentCell(),hero.getCurrentCell())<6)
                            result[i][j][AbilityName.BLASTER_DODGE.ordinal()%3]+=dodgeWeightAlloc[Dodge.isSentryinLOF.ordinal()];
                        break;
                }
            }
        }
*/
        return result;
    }

}
