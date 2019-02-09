package client;

import client.model.*;

import java.util.Random;
import java.util.Scanner;

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
        Random random=new Random();
        //System.out.println("pick started");
        world.pickHero(HeroName.values()[random.nextInt(4)]);
        for(Hero hero1:world.getMyHeroes())
            System.out.println(hero1.getName());
    }

    public void moveTurn(World world)
    {
        draw_map(world);
        Random random=new Random();
        int rand;
        Hero hero;
        boolean check_hero=false,moved=false;
        hero=world.getMyHeroes()[random.nextInt(4)];
        while (!moved){
            rand=random.nextInt(4);
            switch (rand){
                case 0:
                    while (!world.getMap().getCell(hero.getCurrentCell().getRow()-1,hero.getCurrentCell().getColumn())
                            .isWall()){
                        for(Hero my_hero:world.getMyHeroes()){
                            if(world.getMap().getCell(hero.getCurrentCell().getRow()-1,hero.getCurrentCell().getColumn())
                                    .equals(my_hero)){
                                check_hero=true;
                            }
                        }
                        if(!check_hero){
                            world.moveHero(hero,Direction.values()[rand]);
                            moved=true;
                        }
                    }break;
                case 1:
                    if(!world.getMap().getCell(hero.getCurrentCell().getRow()+1,hero.getCurrentCell().getColumn())
                            .isWall()){
                        for(Hero my_hero:world.getMyHeroes()){
                            if(world.getMap().getCell(hero.getCurrentCell().getRow()+1,hero.getCurrentCell().getColumn())
                                    .equals(my_hero)){
                                check_hero=true;
                            }
                        }
                        if(!check_hero){
                            world.moveHero(hero,Direction.values()[rand]);
                            moved=true;
                        }
                    }break;
                case 2:
                    if(!world.getMap().getCell(hero.getCurrentCell().getRow(),hero.getCurrentCell().getColumn()-1)
                            .isWall()){
                        for(Hero my_hero:world.getMyHeroes()){
                            if(world.getMap().getCell(hero.getCurrentCell().getRow(),hero.getCurrentCell().getColumn()-1)
                                    .equals(my_hero)){
                                check_hero=true;
                            }
                        }
                        if(!check_hero){
                            world.moveHero(hero,Direction.values()[rand]);
                            moved=true;
                        }
                    }break;
                case 3:
                    if(!world.getMap().getCell(hero.getCurrentCell().getRow(),hero.getCurrentCell().getColumn()+1)
                            .isWall()){
                        for(Hero my_hero:world.getMyHeroes()){
                            if(world.getMap().getCell(hero.getCurrentCell().getRow(),hero.getCurrentCell().getColumn()+1)
                                    .equals(my_hero)){
                                check_hero=true;
                            }
                        }
                        if(!check_hero){
                            world.moveHero(hero,Direction.values()[rand]);
                            moved=true;
                        }
                    }break;
            }
        }
    }

    public void actionTurn(World world) {
        //System.out.println("action started");

    }

    public void draw_map(World world){
        boolean[][] check=new boolean[world.getMap().getRowNum()][world.getMap().getColumnNum()];
        for (int i = 0; i <  world.getMap().getRowNum(); i++) {
            for (int j = 0; j < world.getMap().getColumnNum(); j++) {
                if (world.getMap().getCell(i, j).isWall()) {
                    System.out.print(ANSI_PURPLE + "\u25A0 " + ANSI_RESET);
                }
                if (!world.getMap().getCell(i, j).isWall()) {
                    for(Hero hero: world.getMyHeroes()){
                        if(hero.getCurrentCell().equals(world.getMap().getCell(i,j))){
                            System.out.print(ANSI_GREEN+hero.getId()+" "+ANSI_RESET);
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

}
