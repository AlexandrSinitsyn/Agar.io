package agar.io;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEngine;

import java.awt.*;
import java.util.Random;

import static agar.io.Field.height;
import static agar.io.Field.objects;
import static agar.io.Field.width;

public class Bomb implements DrawableObject, Eatable {
    final int radius = 25;
    final int mass = 2 * radius;
    Vec pos;

    Bomb() {
        Random r = new Random();

        pos = new Vec(r.nextInt(width), r.nextInt(height));
        pos = pos.add(Field.pos);

        Field.objects.add(this);
        EGEngine.i().addDrawableObject(this);
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double v) {
        graphics2D.setColor(Color.green);

        Vec draw = pos.sub(new Vec(radius, radius));
        graphics2D.fillOval(draw.x(), draw.y(), 2 * radius, 2 * radius);
    }

    @Override
    public void die() {
        EGEngine.i().removeDrawableObject(this);
        objects.remove(this);

        new Bomb();
    }
}
