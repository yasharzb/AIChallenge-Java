package client;

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
    public void preProcess(World world)
    {
        //System.out.println("pre process started");
    }

    public void pickTurn(World world)
    {
        //System.out.println("pick started");

    }

    public void moveTurn(World world)
    {
        boolean[][] check=new boolean[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = 0; i <  world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                if (world.getMap().getCell(i, j).isWall()) {
                    System.out.print(ANSI_PURPLE + "\u25A0 " + ANSI_RESET);
                }
                if (!world.getMap().getCell(i, j).isWall()) {
                    for(Hero hero: world.getMyHeroes()){
                        if(hero.getCurrentCell().equals(world.getMap().getCell(i,j))){
                            System.out.print(ANSI_GREEN+"H "+ANSI_RESET);
                            check[i][j]=true;
                        }
                    }
                    for(Hero hero: world.getOppHeroes()){
                        if(hero.getCurrentCell().equals(world.getMap().getCell(i,j))){
                            System.out.print(ANSI_RED+"H "+ANSI_RESET);
                            check[i][j]=true;
                        }
                    }
                    if(!check[i][j]){
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

    public void actionTurn(World world) {
        //System.out.println("action started");

    }

}
