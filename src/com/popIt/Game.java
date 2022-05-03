package com.popIt;

import java.io.IOException;
import java.util.*;
import com.popIt.design.*;

/* TODO: Resident Evil mini-game - Hallway - Walkie Talkie:
    If user gets walkie talkie, iterate through inventory once item is set
        if user has walkie talkie, have walkie talkie print 'message' associated w/ json object
        once message is printed, pop walkie-talkie from user inventory
    ie:
        get(item)
        if player.getInventory.matches["walkie talkie"]{
            print(however we call the parsed json - walkie talkie -> message)
            remove item from json (need to see parse docs before working out logic)
        else{.....}


 */
public class Game {
    private final Player player = new Player();
    private final Scanner scanner = new Scanner(System.in);
    private Map map = new Map();
    private boolean isOver;
    private boolean endGamePlay;
    private boolean checkEnd;


    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public boolean isEndGamePlay() {
        return endGamePlay;
    }

    public void setEndGamePlay(boolean endGamePlay) {
        this.endGamePlay = endGamePlay;
    }

    public boolean isCheckEnd() {
        return checkEnd;
    }

    public void setCheckEnd(boolean checkEnd) {
        this.checkEnd = checkEnd;
    }

    public void execute() {
        System.out.println("Executing game...");
        welcome();
        while (!isOver()) {
            try {
                while (!isEndGamePlay()) {
                    try {
                        gamePlay();
                        if (isOver()) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("game is over= " + isOver());
                    System.out.println("end game play= " + isEndGamePlay());
                }
                if (isEndGamePlay()) {
                    setEndGamePlay(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Game over");
    }

    public void welcome() {


        getSplashTheme();
        getOpening();
        getUsername();
        getMenu();

    }

    private void getSplashTheme() {
        Ascii.splashScreen();
    }

    private void getOpening() {
        Ascii.opening();
    }

    private void getUsername() {
        try {
            boolean validInput = false;
            System.out.print("Please enter your name: ");
            while (!validInput) {
                String username = scanner.nextLine();
                if (username.matches("[a-zA-Z]{2,15}")) {
                    player.setUsername(username);
                    validInput = true;
                } else {
                    System.out.println(
                            "Please enter a valid user name between 2 and 15 characters (numbers not allowed)");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMenu() {
        Ascii.commands();
    }

    private void showStatus() {
        //show player and current room status
        //TODO: get/set logic for items
        System.out.println("\n ===== " + player.getUsername() + "'s Current Status =====\n" +
                "Current Room: " + map.getCurrentRoom() + "\n" +
                "Current Lives: " + player.getLives() + "\n" +
                "Current Inventory: " + player.getInventory() + "\n" +
                "Items in Room: map.getItems()\n" +
                "You can Move: map.availableDirections()\n" +
                "=====\n");

    }

    private boolean checkEnd() {
        String currentRoom = map.getCurrentRoom();
        if (currentRoom.equals("mazecenter")) {
            checkEnd = true;
            Ascii.win();
        }else if(player.getLives() < 1){
            checkEnd = true;
            Ascii.lose();
        }else {
            checkEnd = false;
        }
        return checkEnd;
    }


    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /*JS - 05/03 - Randomize function generates number 1-8,
    if number is >= 1 an ascii image of a ghost appears and player lives are set- 1
    player.getLives() is returned with current value
    Code checked, sout remains if you want to check generated digits and verify logic works*/
    private int randomize() {
        double digit = Math.random() * 8;
        System.out.println(digit);
        if(digit <= 2 ){
            Ascii.ghost();
            player.setLives(player.getLives() -1);
            return player.getLives();
        }
        return player.getLives();
    }

    /*JS - 05/03 - if the current room contains "trap" - ascii.ghost generates ghost image and player loses 1 life
     */
    private int trap() {
        try {
            if (map.getCurrentRoom().contains("trap")) {
                Ascii.ghost();
                player.setLives(player.getLives() - 1);
            }
            return player.getLives();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player.getLives();
    }


    private void gamePlay() {
        player.setLives(5);
        map.setCurrentRoom("start");
        player.setInventory(null);

        while (true) {
            clearScreen();
            showStatus();

            String move = "";
            while (move.equals("")) {
                System.out.println("enter command >");
                move = scanner.nextLine().toLowerCase();
            }
            String[] moveArray = move.split(" ");
            if (moveArray[0].equals("go")) {
                if (moveArray[1].equals(map.getMap(moveArray[1]))) {
                    map.setCurrentRoom(map.getCurrentRoom());
                }
            } else if (moveArray[0].equals("look")) {
                if (moveArray[1].equals(map.getCurrentRoom())) {
                    map.roomInfo();
                } else if (moveArray[1].equals("item")) {
                    map.itemInfo();
                }
            } else if (move.matches("show menu|menu")) {
                getMenu();
                String input = scanner.nextLine().toLowerCase();
                if (input.matches("q|quit")) {
                    System.out.println("Are you sure you want to QUIT?");
                    String inputQuit = scanner.nextLine().toLowerCase();
                    if (inputQuit.matches("y|yes")) {
                        setOver(true);
                        break;
                    } else {
                        continue;
                    }
                }
                if (input.matches("r|restart|")) {
                    setEndGamePlay(true);
                    break;
                }
                if (input.matches("c|continue|")) {
                    System.out.println("continuing game");
                }

            }
            trap();
            randomize();
            System.out.println();
            checkEnd();
            if (checkEnd) {
                setOver(true);
                break;
            }
        }
    }
}


