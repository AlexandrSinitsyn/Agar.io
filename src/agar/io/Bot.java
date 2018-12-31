package agar.io;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEngine;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static agar.io.Field.*;
import static agar.io.Field.cellSize;
import static agar.io.Field.objects;

public class Bot implements DrawableObject {
    int id;
    private Color color = Color.black;
    CopyOnWriteArrayList<BotBall> components =
            new CopyOnWriteArrayList<>();

    Bot(int id) {
        this.id = id;
        TopList.bots.add(this);

        start();
        
        Random r = new Random();
        color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));

        EGEngine.i().addDrawableObject(this);
    }

    private void start() {
        Random r = new Random();
        final int startMass = 8;

        Vec pos = new Vec(r.nextInt(width), r.nextInt(height));
        pos = pos.add(Field.pos);
        new BotBall(pos, startMass, this);
    }

    int fullMass() {
        int mass = 0;
        for (BotBall ball : components)
            mass += ball.mass;

        return mass;
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double v) {
        if (components.size() == 0)
            restart();


        for (BotBall ball : components) {
            { // fixme       drawing
                graphics2D.setColor(color);

                Vec drawPos = positionToDraw(ball.pos, ball.radius);
                int d = (int) (2 * ball.radius);

                graphics2D.fillOval(drawPos.x(), drawPos.y(), d, d);

                graphics2D.setFont(new Font("MV Boli", Font.ITALIC, 20));
                graphics2D.setColor(Player.getContrastColor(color));
                graphics2D.drawString("id:  " + id,
                        drawPos.x() + 20, (int) (drawPos.y() + ball.radius / 2D));
                graphics2D.drawString("mass:  " + ball.mass,
                        drawPos.x(), (int) (drawPos.y() + ball.radius));
            }

            ball.way = defineWay(defineLeast().pos);
        }
    }

    private Vec positionToDraw(Vec pos, double radius) {
        return
                pos.sub(new Vec(radius, radius));
    }

    private void restart() {
        start();
    }

    private BotBall defineLeast() {
        BotBall least = components.get(0);

        for (BotBall ball : components)
            if (ball.mass < least.mass)
                least = ball;

        return least;
    }

    private Vec defineWay(Vec pos) {
        // fixme       !

        LinkedList<Food> foodList = new LinkedList<>();

        for (Eatable eat : objects)
            if (eat instanceof Food)
                foodList.add((Food) eat);

        Food nearest = foodList.get(0);

        for (Food food : foodList)
            if (food.pos.sub(pos).length() < nearest.pos.sub(pos).length())
                nearest = food;

        Vec way = nearest.pos.sub(pos);

        return way.normalize();
    }

    boolean mineBall(BotBall ball) {
        return ball.bot.id == this.id;
    }
}

class BotBall implements DrawableObject, Eatable {

    double radius;
    Vec pos;
    int mass;
    private Vec speed;
    Bot bot;

    Vec way = new Vec();


    BotBall(Vec pos, int mass, Bot bot) {
        this.pos = pos;
        this.mass = mass;
        this.bot = bot;

        bot.components.add(this);
        objects.add(this);

        EGEngine.i().addDrawableObject(this);
    }

    private void defineSpeed() {
        double sp = 0.25D;
        if (mass <= 800)
            sp = -mass / 100D + 9;
        else if (mass < 8000)
            sp = -mass / 14200D + 0.8875D;

        speed = new Vec(sp, sp).scale(cellSize).mull(way);
    }

    private void defineRadius() {
        radius = 10 +
                ( Math.log(mass) / Math.log(2) ) * 10;
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double sec) {
        defineSpeed();
        defineRadius();

        pos = pos.add(speed.scale(sec));

        if (canEat() != null)
            eat(canEat());
    }

    private Eatable canEat() {
        for (Eatable object : objects) {
            // Food
            if (object instanceof Food) {
                double length = pos.sub(((Food) object).pos).length();

                if (length < radius + ((Food) object).radius)
                    return object;
            }

            // Bomb
            if (object instanceof Bomb) {
                double length = pos.sub(((Bomb) object).pos).length();

                if (length < radius + ((Bomb) object).radius &&
                        mass / ((Bomb) object).mass > (3D / 2D))
                    return object;
            }

            // Ball
            if (object instanceof Ball) {
                double length = pos.sub(((Ball) object).pos).length();

                if (length < radius + ((Ball) object).radius &&
                        mass / ((Ball) object).mass > (3D / 2D))
                    return object;
            }

            // BotBall
            if (object instanceof BotBall) {
                if (object != this) {
                    double length = pos.sub(((BotBall) object).pos).length();

                    if (length < radius + ((BotBall) object).radius &&
                            mass > ((BotBall) object).mass &&
                            pos.sub(((BotBall) object).pos).length() < 2 &&
                            bot.mineBall(((BotBall) object)))
                        return object;

                    else if (length < radius + ((BotBall) object).radius &&
                            mass / ((BotBall) object).mass > (3D / 2D))
                        return object;
                }
            }
        }

        return null;
    }

    private void eat(Eatable object) {
        assert object != null;

        if (object instanceof Food)
            mass++;
        if (object instanceof Ball)
            mass += ((Ball) object).mass;
        if (object instanceof BotBall)
            mass += ((BotBall) object).mass;
        if (object instanceof Bomb)
            split();

        object.die();
    }

    private void split() {
        int Mass = mass - (mass / 2);

        Vec adding = way.scale(8 * cellSize);
        new BotBall(this.pos.add(adding),
                Mass, this.bot);

        mass /= 2;
    }

    @Override
    public void die() {
        bot.components.remove(this);

        EGEngine.i().removeDrawableObject(this);

        objects.remove(this);
    }
}
