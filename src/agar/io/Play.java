package agar.io;

import ru.ege.engine.EGEngine;

import java.awt.*;
import java.io.IOException;

public class Play {

    public static void main(String[] args) throws IOException {
        //Creating field to play
        new Field();

        //To use you Player, you need to switch on Keyboard or Mouse.
        Player player1 = new Player(567, Color.yellow);
        player1.Keyboard();
        Player player2 = new Player(528, "someImage.jpg");
        player2.Mouse();

        //Create some bots to compete with
        new Bot(871);
        new Bot(216);
        new Bot(145);
        new Bot(2356);

        //TopList is for knownig everybodies mass and so on
        new TopList();

        //start play
        EGEngine.i().startDrawingThread();
    }
}
