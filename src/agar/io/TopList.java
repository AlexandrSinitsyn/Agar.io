package agar.io;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEngine;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;

import static agar.io.Field.screenSize;

public class TopList implements DrawableObject {

    static LinkedList<Player> players = new LinkedList<>();
    static LinkedList<Bot> bots = new LinkedList<>();

    private Vec size = new Vec(300, 400);
    private Vec pos = screenSize.sub(size);

    TopList() {
        EGEngine.i().addDrawableObject(this, 1);
    }

    private void sort(LinkedList<Player> players, LinkedList<Bot> bots) {
        players.sort(new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return
                        Integer.compare(b.fullMass(), a.fullMass());
            }
        });

        bots.sort(new Comparator<Bot>() {
            @Override
            public int compare(Bot a, Bot b) {
                return
                        Integer.compare(b.fullMass(), a.fullMass());
            }
        });
    }

    @Override
    public void drawAndUpdate(Graphics2D graphics2D, double v) {
        sort(players, bots);

        graphics2D.setColor(Color.white);
        graphics2D.fillRect(pos.x(), pos.y(), size.x(), size.y());

        graphics2D.setStroke(new BasicStroke(5));
        graphics2D.setColor(Color.black);
        graphics2D.drawLine(pos.add((size).scale_div(2)).x(), pos.y(),
                pos.add((size).scale_div(2)).x(), pos.add(size).y());

        graphics2D.setFont(new Font("MV Boli", Font.ITALIC, 20));
        Vec names =
                new Vec(((size).scale_div(2)).scale_div(2).x(), 20);
        graphics2D.drawString("Players",
                pos.add(names).x(), pos.add(names).y());
        graphics2D.drawString("Bots",
                pos.add(names.scale(3)).x(), pos.add(names).y());

        graphics2D.drawLine(pos.x(), pos.add(names).y() + 10,
                pos.add(size).x(), pos.add(names).y() + 10);

        for (int i = 0; i < 5; i++) {
            if (players.size() > i)
                graphics2D.drawString(players.get(i).id + ":  " + players.get(i).fullMass(),
                        pos.x(), pos.add(names).y() + 20 * (i + 1 + 1));

            if (bots.size() > i)
            graphics2D.drawString(bots.get(i).id + ":  " + bots.get(i).fullMass(),
                    pos.add(size.scale_div(2)).x(), pos.add(names).y() + 20 * (i + 1 + 1));
        }
    }
}
