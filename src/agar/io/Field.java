package agar.io;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEngine;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Field implements DrawableObject {
    
    static final int width = 5000;//EGEngine.i().getWidth() - 200;
    static final int height = 5000;//EGEngine.i().getHeight() - 200;
    static final int cellSize = 20;

    static Vec pos = new Vec(100, 100);
    final static Vec screenSize =
            new Vec(EGEngine.i().getWidth(), EGEngine.i().getHeight());
    static CopyOnWriteArrayList<Eatable> objects = new CopyOnWriteArrayList<>();

    static {
        int count = (int) (Math.min(width, height) * 11D / 30D - 500D / 3D);

        for (int i = 0; i < count; i++)
            new Food();


        int bombCount = width / 100; //(int) (width * 9D / 400D - 12.5);

        for (int i = 0; i < bombCount; i++)
            new Bomb();
    }

    Field() {
        EGEngine.i().addDrawableObject(this);
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double v) {
        graphics2D.setColor(Color.lightGray);


        for (int i = 0; i <= floor(width / cellSize); i++)
            graphics2D.drawLine(pos.x() + i * cellSize, pos.y(),
                    pos.x() + i * cellSize, pos.y() + height);

        for (int i = 0; i <= floor(height / cellSize); i++)
            graphics2D.drawLine(pos.x(), pos.y() + i * cellSize,
                    pos.x() + width, pos.y() + i * cellSize);
    }

    private int floor(double a) {
        return (a - (int) a > 0.5) ? (int) a + 1 : (int) a;
    }
}
