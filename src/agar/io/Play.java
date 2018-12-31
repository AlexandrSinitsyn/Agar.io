package agar.io;

import ru.ege.engine.EGEngine;

import java.awt.*;
import java.io.IOException;

public class Play {

    public static void main(String[] args) throws IOException {
        new Field();

        //Player player1 = new Player(567, Color.yellow);
        //player1.Keyboard();
        Player player2 = new Player(528, "test.jpg");
        player2.Mouse();

        new Bot(871);
        new Bot(216);
        new Bot(145);
        new Bot(2356);

        new TopList();

        EGEngine.i().startDrawingThread();
    }
}
