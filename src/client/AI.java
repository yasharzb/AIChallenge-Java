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
        System.out.println("action started");
/*
        Hero[] heroes = world.getMyHeroes();
        Map map = world.getMap();
        for (Hero hero : heroes)
        {
            int row = random.nextInt(map.getRowNum());
            int column = random.nextInt(map.getColumnNum());

            world.castAbility(hero, hero.getAbilities()[random.nextInt(3)], row, column);
        }
*/
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
