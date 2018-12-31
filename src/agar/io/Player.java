package agar.io;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEngine;

import javax.imageio.ImageIO;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.awt.event.KeyEvent.*;
import static agar.io.Field.*;

public class Player implements KeyListener, MouseInputListener,
        DrawableObject {
    int id;
    private BufferedImage img = null;
    private Color color = Color.black;
    CopyOnWriteArrayList<Ball> components =
            new CopyOnWriteArrayList<>();

    private Player(int id) {
        this.id = id;
        TopList.players.add(this);

        start();

        EGEngine.i().addDrawableObject(this);
        EGEngine.i().addKeyListener(this);
        EGEngine.i().addMouseListener(this);
        EGEngine.i().addMouseMotionListener(this);
    }

    Player(int id, String path) throws IOException {
        this(id);

        this.img = ImageIO.read(new File(path));
    }

    Player(int id, Color color) {
        this(id);

        this.color = color;
    }

    private void start() {
        Random r = new Random();
        final int startMass = 8;

        Vec pos = new Vec(r.nextInt(width), r.nextInt(height));
        pos = pos.add(Field.pos);
        new Ball(pos, startMass, this);
        // this function scroll the world, so you will be in the middle of the screen.
        findMe();
    }

    private void findMe() {
        Vec me = components.get(0).pos.sub(screenSize.scale_div(2));

        checkPositions(me, new Vec());

        Field.pos = Field.pos.add(me.opposite());
    }


    // before starting to play, you must use one of these functions
    // or your ball won't move.
    private boolean Mouse = false;
    private boolean Keyboard = false;

    void Mouse() {
        Mouse = true;
    }
    void Keyboard() {
        Keyboard = true;
    }


    // As soon as it possible, I'll do something like this:
    //
    // You will be able to not scrolling the world, because
    // it will move synchronized with you:
    // your ball will stay on one place and the world - move.
    private boolean movingWorld = false;

    public void setMovingWorld(boolean movingWorld) {
        this.movingWorld = movingWorld;
    }

    int fullMass() {
        int mass = 0;
        for (Ball ball : components)
            mass += ball.mass;

        return mass;
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double v) {
        if (components.size() == 0)
            restart();


        for (Ball ball : components) {
            { // fixme       drawing
                graphics2D.setColor(color);

                Vec drawPos = positionToDraw(ball.pos, ball.radius);
                int d = (int) (2 * ball.radius);

                if (img != null) {
                    graphics2D.drawImage(scale(img, d, d),
                            drawPos.x(), drawPos.y(), null);
                } else
                    graphics2D.fillOval(drawPos.x(), drawPos.y(), d, d);

                graphics2D.setFont(new Font("MV Boli", Font.ITALIC, 20));
                graphics2D.setColor(Player.getContrastColor(color));
                graphics2D.drawString("id:  " + id,
                        drawPos.x() + 20, (int) (drawPos.y() + ball.radius / 2D));
                graphics2D.drawString("mass:  " + ball.mass,
                        drawPos.x(), (int) (drawPos.y() + ball.radius));
            }

            { // fixme          give speed
                if (Mouse)
                    ball.way = defineWay(mouse, ball.pos);
                if (Keyboard) {
                    Vec speed = new Vec();

                    if (a) speed = speed.add(new Vec(-1, 0));
                    if (s) speed = speed.add(new Vec(0, 1));
                    if (d) speed = speed.add(new Vec(1, 0));
                    if (w) speed = speed.add(new Vec(0, -1));

                    ball.way = speed.normalize();
                }
            }
        }
    }

    static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    private static BufferedImage scale(BufferedImage img,
                                      int width, int height) {
        BufferedImage scaledImage =
                new BufferedImage((width > 0) ? width : 1, (height > 0) ? height : 1, img.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(img, 0, 0, width, height, null);
        graphics2D.dispose();
        return scaledImage;
    }

    private Vec positionToDraw(Vec pos, double radius) {
        return
                pos.sub(new Vec(radius, radius));
    }

    private void split() {
        for (Ball ball : components)
            if (ball.mass >= 80)
                ball.split();
    }

    private void restart() {
        start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    private boolean a = false;
    private boolean s = false;
    private boolean d = false;
    private boolean w = false;
    public void keyPressed(KeyEvent e) {
        if (Keyboard) {
            switch (e.getKeyCode()) {
                case VK_W:
                    split();
                    break;
                case VK_UP:
                    w = true;
                    break;
                case VK_DOWN:
                    s = true;
                    break;
                case VK_RIGHT:
                    d = true;
                    break;
                case VK_LEFT:
                    a = true;
                    break;
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        if (Keyboard) {
            switch (e.getKeyCode()) {
                case VK_UP:
                    w = false;
                    break;
                case VK_DOWN:
                    s = false;
                    break;
                case VK_RIGHT:
                    d = false;
                    break;
                case VK_LEFT:
                    a = false;
                    break;
            }
        }
    }
    public void mouseClicked(MouseEvent e) {}
    private boolean press = false;
    private Vec pressed = new Vec();
    public void mousePressed(MouseEvent e) {
        mouse = new Vec(e.getX(), e.getY());

        switch (e.getButton()) {
            case 1: // Left mouse Button
                if (Mouse)
                    split();
                break;

            case 3: // Right Mouse Button
                pressed = Field.pos.sub(mouse);
                press = true;
                break;
        }
    }
    public void mouseReleased(MouseEvent e) {
        press = false;
        pressed = new Vec();
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        mouse = new Vec(e.getX(), e.getY());

        if (press)
            addPos();
    }
    private Vec mouse = new Vec();
    public void mouseMoved(MouseEvent e) {
        if (Mouse) {
            mouse =
                    new Vec(e.getX(), e.getY());
        }
    }

    private void addPos() {
        Vec last = Field.pos;
        Field.pos = mouse.add(pressed);
        Vec New = Field.pos;

        checkPositions(last, New);
    }

    private void checkPositions(Vec toSub, Vec toAdd) {
        Vec changes = toSub.sub(toAdd);

        for (Eatable object : objects) {
            if (object instanceof Food)
                ((Food) object).pos = ((Food) object).pos.sub(changes);
            if (object instanceof Ball)
                ((Ball) object).pos = ((Ball) object).pos.sub(changes);
            if (object instanceof BotBall)
                ((BotBall) object).pos = ((BotBall) object).pos.sub(changes);
            if (object instanceof Bomb)
                ((Bomb) object).pos = ((Bomb) object).pos.sub(changes);
        }
    }

    private Vec defineWay(Vec mouse, Vec pos) {
        Vec way = mouse.sub(pos);

        return way.normalize();
    }

    boolean mineBall(Ball ball) {
        return ball.player.id == this.id;
    }
}

class Ball implements DrawableObject, Eatable {

    double radius;
    Vec pos;
    int mass;
    private Vec speed;
    Player player;

    Vec way = new Vec();


    Ball(Vec pos, int mass, Player player) {
        this.pos = pos;
        this.mass = mass;
        this.player = player;

        player.components.add(this);
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

            // BotBall
            if (object instanceof BotBall) {
                double length = pos.sub(((BotBall) object).pos).length();

                if (length < radius + ((BotBall) object).radius &&
                        mass / ((BotBall) object).mass > (3D / 2D))
                    return object;
            }

            // Ball
            if (object instanceof Ball) {
                if (object != this) {
                    double length = pos.sub(((Ball) object).pos).length();

                    if (length < radius + ((Ball) object).radius &&
                            mass > ((Ball) object).mass &&
                            pos.sub(((Ball) object).pos).length() < 10 &&
                            player.mineBall(((Ball) object)))
                        return object;

                    else if (length < radius + ((Ball) object).radius &&
                            mass / ((Ball) object).mass > (3D / 2D))
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

    void split() {
        int Mass = mass - (mass / 2);

        Vec adding = way.scale(8 * cellSize);
        new Ball(this.pos.add(adding),
                Mass, this.player);

        mass /= 2;
    }

    @Override
    public void die() {
        player.components.remove(this);

        EGEngine.i().removeDrawableObject(this);

        objects.remove(this);
    }
}
