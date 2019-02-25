package client;

import client.Heroes.Blaster;
import client.Heroes.Guardian;
import client.Heroes.Healer;
import client.Heroes.Sentry;
import client.model.*;

import java.util.Random;



public class AI
{

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Random random = new Random();

    private Cell[] objectivePoints=new Cell[4];
    private Cell objectivePoint;
    public void preProcess(World world)
    {
        for(int i=0;i<4;i++){
            objectivePoints[i]=world.getMap().getCell(random.nextInt(world.getMap().getRowNum())
                    ,random.nextInt(world.getMap().getColumnNum()));
            while (!objectivePoints[i].isInObjectiveZone())
                objectivePoints[i]=world.getMap().getCell(random.nextInt(world.getMap().getRowNum())
                        ,random.nextInt(world.getMap().getColumnNum()));
            System.out.println(objectivePoints[i].getRow()+" , "+objectivePoints[i].getColumn());
        }

        System.out.println("pre process started");
    }

    public void pickTurn(World world)
    {
        System.out.println("pick started");
        world.pickHero(HeroName.values()[world.getCurrentTurn()]);
        System.out.println(HeroName.values()[world.getCurrentTurn()]);
    }
    public void moveTurn(World world)
    {
        System.out.println("move started");
        draw_map(world);
        double nextCellWeight;
        Cell nextCell;
        for (Hero hero : world.getMyHeroes())
        {
            if(hero.getCurrentCell().isInObjectiveZone()&&
                    (world.manhattanDistance(world.getOppHeroes()[0].getCurrentCell(),hero.getCurrentCell())<=4||
                     world.manhattanDistance(world.getOppHeroes()[1].getCurrentCell(),hero.getCurrentCell())<=4||
                     world.manhattanDistance(world.getOppHeroes()[2].getCurrentCell(),hero.getCurrentCell())<=4||
                     world.manhattanDistance(world.getOppHeroes()[3].getCurrentCell(),hero.getCurrentCell())<=4))
                continue;
            nextCell=hero.getCurrentCell();
            if(hero.getId()%2==1)
                objectivePoint=objectivePoints[hero.getId()/2];
            else
                objectivePoint=objectivePoints[hero.getId()/2];
            switch (hero.getName()){
                case HEALER:
                    nextCellWeight=new Healer().setMovementWeight(hero,world,objectivePoint)
                            [hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()];
                    for (int i=hero.getCurrentCell().getRow()-1;i<=hero.getCurrentCell().getRow()+1;i++){
                        outer:
                        for (int j=hero.getCurrentCell().getColumn()-1;j<=hero.getCurrentCell().getColumn()+1;j++){
                            if(i==hero.getCurrentCell().getRow()||j==hero.getCurrentCell().getColumn()){
                                for(Hero my_hero : world.getMyHeroes()){
                                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j)))
                                        continue outer;
                                }
                                if(nextCellWeight<new Healer().setMovementWeight(hero,world,objectivePoint)
                                        [i][j]) {
                                    nextCellWeight = new Healer().setMovementWeight(hero, world, objectivePoint)
                                            [i][j];
                                    nextCell=world.getMap().getCell(i,j);
                                }
                            }
                        }
                    }
                    break;
                case SENTRY:
                    nextCellWeight=new Sentry().setMovementWeight(hero,world,objectivePoint)
                            [hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()];
                    for (int i=hero.getCurrentCell().getRow()-1;i<=hero.getCurrentCell().getRow()+1;i++){
                        outer:
                        for (int j=hero.getCurrentCell().getColumn()-1;j<=hero.getCurrentCell().getColumn()+1;j++){
                            if(i==hero.getCurrentCell().getRow()||j==hero.getCurrentCell().getColumn()){
                                for(Hero my_hero : world.getMyHeroes()){
                                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j)))
                                        continue outer;
                                }
                                if(nextCellWeight<new Sentry().setMovementWeight(hero,world,objectivePoint)
                                        [i][j]) {
                                    nextCellWeight = new Sentry().setMovementWeight(hero, world, objectivePoint)
                                            [i][j];
                                    nextCell=world.getMap().getCell(i,j);
                                }
                            }
                        }
                    }
                    break;
                case GUARDIAN:
                    nextCellWeight=new Guardian().setMovementWeight(hero,world,objectivePoint)
                            [hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()];
                    for (int i=hero.getCurrentCell().getRow()-1;i<=hero.getCurrentCell().getRow()+1;i++){
                        outer:
                        for (int j=hero.getCurrentCell().getColumn()-1;j<=hero.getCurrentCell().getColumn()+1;j++){
                            if(i==hero.getCurrentCell().getRow()||j==hero.getCurrentCell().getColumn()){
                                for(Hero my_hero : world.getMyHeroes()){
                                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j)))
                                        continue outer;
                                }
                                if(nextCellWeight<new Guardian().setMovementWeight(hero,world,objectivePoint)
                                        [i][j]) {
                                    nextCellWeight = new Guardian().setMovementWeight(hero, world, objectivePoint)
                                            [i][j];
                                    nextCell=world.getMap().getCell(i,j);
                                }
                            }
                        }
                    }
                    break;
                case BLASTER:
                    nextCellWeight=new Blaster().setMovementWeight(hero,world,objectivePoint)
                            [hero.getCurrentCell().getRow()][hero.getCurrentCell().getColumn()];
                    for (int i=hero.getCurrentCell().getRow()-1;i<=hero.getCurrentCell().getRow()+1;i++){
                        outer:
                        for (int j=hero.getCurrentCell().getColumn()-1;j<=hero.getCurrentCell().getColumn()+1;j++){
                            if(i==hero.getCurrentCell().getRow()||j==hero.getCurrentCell().getColumn()){
                                for(Hero my_hero : world.getMyHeroes()){
                                    if(my_hero.getCurrentCell().equals(world.getMap().getCell(i,j)))
                                        continue outer;
                                }
                                if(nextCellWeight<new Blaster().setMovementWeight(hero,world,objectivePoint)
                                        [i][j]) {
                                    nextCellWeight = new Blaster().setMovementWeight(hero, world, objectivePoint)
                                            [i][j];
                                    nextCell=world.getMap().getCell(i,j);
                                }
                            }
                        }
                    }
                    break;
            }
            System.out.println("NextCell: "+nextCell.getRow()+" , "+nextCell.getColumn());
            System.out.println("HeroCell: "+hero.getCurrentCell().getRow()+" , "+hero.getCurrentCell().getColumn());
            if(world.getPathMoveDirections(hero.getCurrentCell(),nextCell).length!=0){
                System.out.println("Direction: "+world.getPathMoveDirections(hero.getCurrentCell(),nextCell)[0]);
                world.moveHero(hero,world.getPathMoveDirections(hero.getCurrentCell(),nextCell)[0]);
            }
        }
    }

    public void actionTurn(World world) {
        Cell objPoint=world.getMap().getCell(0,0);
        if(world.getAP()<15)
            return;
        outer:
        for(int i=0;i<world.getMap().getRowNum();i++)
            for(int j=0;j<world.getMap().getColumnNum();j++)
                if(world.getMap().getCell(i,j).isInObjectiveZone()){
                    objPoint=world.getMap().getCell(i,j);
                    break outer;
                }
        System.out.println("action started");
        System.out.println(world.getAP());
        draw_map(world);
        double actionWeight=0D;
        Cell targetCell=world.getMap().getCell(0,0);
        int actionnaireID=0;
        AbilityName abilityName=AbilityName.BLASTER_ATTACK;
        System.out.println("Starting the for loop.");
        for(Hero my_hero : world.getMyHeroes()){
            System.out.println("Loop cycle");
            switch (my_hero.getName()){
                case HEALER:
                    System.out.println("Healer Case");
                    for(int i=objPoint.getRow();i<objPoint.getRow()+5;i++){
                        for(int j=objPoint.getColumn();j<objPoint.getColumn()+5;j++){
                            System.out.println("Healer_Attack: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_ATTACK.ordinal()%3]);
                            System.out.println("Healer_Heal: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_HEAL.ordinal()%3]);
                            System.out.println("Healer_Dodge: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_DODGE.ordinal()%3]);
                            if(new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_ATTACK.ordinal()%3]>actionWeight){
                                actionWeight=new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_ATTACK.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.HEALER_ATTACK;
                            }
                            if(new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_HEAL.ordinal()%3]>actionWeight){
                                actionWeight=new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_HEAL.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.HEALER_HEAL;
                            }
/*
                            if(new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_DODGE.ordinal()%3]>actionWeight){
                                System.out.println("Healer_Dodge B");
                                actionWeight=new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.HEALER_DODGE.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.HEALER_DODGE;
                                System.out.println("Healer_Dodge A");
                            }
*/
                        }
                    }
                    break;
                case GUARDIAN:
                    System.out.println("Guardian Case");
                    for(int i=objPoint.getRow();i<objPoint.getRow()+5;i++){
                        for(int j=objPoint.getColumn();j<objPoint.getColumn()+5;j++){
                            System.out.println("Guardian_Attack: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3]);
                            System.out.println("Guardian_Fortify: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3]);
                            System.out.println("Guardian_Dodge: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]);
                            if(new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3]>actionWeight){
                                actionWeight=new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_ATTACK.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.GUARDIAN_ATTACK;
                            }
                            if(new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3]>actionWeight){
                                actionWeight=new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_FORTIFY.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.GUARDIAN_FORTIFY;
                            }
/*
                            if(new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3]>actionWeight){
                                System.out.println("Guardian_Dodge B");
                                actionWeight=new Guardian().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.GUARDIAN_DODGE.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.GUARDIAN_DODGE;
                                System.out.println("Guardian_Dodge A");
                            }
*/
                        }
                    }
                    break;
                case BLASTER:
                    System.out.println("Blaster Case");
                    for(int i=objPoint.getRow();i<objPoint.getRow()+5;i++){
                        for(int j=objPoint.getColumn();j<objPoint.getColumn()+5;j++){
                            System.out.println("Blaster_Attack: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_ATTACK.ordinal()%3]);
                            System.out.println("Blaster_Bomb: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_BOMB.ordinal()%3]);
                            System.out.println("Blaster_Dodge: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_DODGE.ordinal()%3]);
                            if(new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_ATTACK.ordinal()%3]>actionWeight){
                                actionWeight=new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_ATTACK.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.BLASTER_ATTACK;
                            }
                            if(new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_BOMB.ordinal()%3]>actionWeight){
                                actionWeight=new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_BOMB.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.BLASTER_BOMB;
                            }
/*
                            if(new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_DODGE.ordinal()%3]>actionWeight){
                                System.out.println("Blaster_Dodge B");
                                actionWeight=new Blaster().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.BLASTER_DODGE.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.BLASTER_DODGE;
                                System.out.println("Blaster_Dodge A");
                            }
*/
                        }
                    }
                    break;
                case SENTRY:
                    System.out.println("Sentry Case");
                    for(int i=objPoint.getRow();i<objPoint.getRow()+5;i++){
                        for(int j=objPoint.getColumn();j<objPoint.getColumn()+5;j++){
                            System.out.println("Sentry_Attack: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3]);
                            System.out.println("Sentry_Ray: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_RAY.ordinal()%3]);
                            System.out.println("Sentry_Dodge: "+new Healer().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_DODGE.ordinal()%3]);
                            if(new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3]>actionWeight){
                                actionWeight=new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_ATTACK.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.SENTRY_ATTACK;
                            }
                            if(new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_RAY.ordinal()%3]>actionWeight){
                                actionWeight=new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_RAY.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.SENTRY_RAY;
                            }
/*
                            if(new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_DODGE.ordinal()%3]>actionWeight){
                                System.out.println("Sentry_Dodge B");
                                actionWeight=new Sentry().setActionWeight(my_hero,world,objPoint)[i][j][AbilityName.SENTRY_DODGE.ordinal()%3];
                                targetCell=world.getMap().getCell(i,j);
                                actionnaireID=my_hero.getId();
                                abilityName=AbilityName.SENTRY_DODGE;
                                System.out.println("Sentry_Dodge A");
                            }
*/
                        }
                    }
                    break;
                default:
                    System.out.println("Gooz pich!");
                    break;
            }
        }
        System.out.println("Target Cell: "+targetCell.getRow()+" , "+targetCell.getColumn());
        System.out.println("Ability name: "+abilityName);
        System.out.println("Actionnaire ID: "+actionnaireID);
        if(targetCell!=world.getMap().getCell(0,0)){
            world.castAbility(world.getHero(actionnaireID),abilityName,targetCell);
        }
    }
    public void draw_map(World world) {
        boolean[][] check = new boolean[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = 0; i < world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                if (world.getMap().getCell(i, j).isWall()) {
                    System.out.print(ANSI_PURPLE + "\u25A0 " + ANSI_RESET);
                }
                if (!world.getMap().getCell(i, j).isWall()) {
                    for (Hero hero : world.getMyHeroes()) {
                        if (hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                            System.out.print(ANSI_GREEN + hero.getId() + " " + ANSI_RESET);
                            check[i][j] = true;
                        }
                    }
                    for (Hero hero : world.getOppHeroes()) {
                        if (hero.getCurrentCell().equals(world.getMap().getCell(i, j))) {
                            System.out.print(ANSI_RED + hero.getId()+" " + ANSI_RESET);
                            check[i][j] = true;
                        }
                    }
                    if (!check[i][j]) {
                        if (world.getMap().getCell(i, j).isInObjectiveZone()) {
                            System.out.print(ANSI_BLUE + "J " + ANSI_RESET);
                        } else if (world.getMap().getCell(i, j).isInMyRespawnZone()) {
                            System.out.print(ANSI_GREEN + "S " + ANSI_RESET);
                        } else if (world.getMap().getCell(i, j).isInOppRespawnZone()) {
                            System.out.print(ANSI_RED + "S " + ANSI_RESET);
                        } else if (world.getMap().getCell(i, j).isInVision()) {
                            System.out.print("V ");
                        } else {
                            System.out.print("  ");
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}
