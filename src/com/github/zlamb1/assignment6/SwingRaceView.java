package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.IRace;
import com.github.zlamb1.assignment5.IRaceView;
import com.github.zlamb1.assignment5.racer.IRacer;
import com.github.zlamb1.view.swing.LookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class SwingRaceView extends JFrame implements IRaceView {
    protected BufferStrategy bufferStrategy;
    protected IRace race;

    public SwingRaceView() {
        LookAndFeel.setSystemLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            setTitle("Assignment 6 - Race");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setIgnoreRepaint(true);

            setVisible(true);

            Thread t = new Thread(() -> {

                while (true) {
                    if (bufferStrategy == null) {
                        createBufferStrategy(2);
                        bufferStrategy = getBufferStrategy();
                    }

                    do {
                        do {
                            Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
                            paintBuffer(g2d);
                            g2d.dispose();
                        } while (bufferStrategy.contentsRestored());

                        bufferStrategy.show();
                    } while (bufferStrategy.contentsLost());
                }
            });

            t.setDaemon(true);
            t.start();
        });
    }

    public void paintBuffer(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // clear frame
        g2d.clearRect(0, 0, getWidth(), getHeight());

        Insets insets = getInsets();
        g2d.translate(insets.left, insets.top);

        int width = getWidth() - insets.left - insets.right, height = getHeight() - insets.top - insets.bottom;

        if (race == null || race.getRacers().isEmpty()) {
            return;
        }

        int trackCount = race.getRacers().size();
        int trackHeight = height / trackCount;
        int trackMargin = 3;

        int markCount = 6;
        int markWidth = width / (markCount * 2);
        int markHeight = trackHeight / 8;
        int markMargin = 3;

        for (int i = 0; i < trackCount; i++) {
            IDrawableRacer drawableRacer = (IDrawableRacer) race.getRacers().get(i);

            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, trackHeight * i + trackMargin, width, trackHeight - trackMargin * 2);

            int trackCenterY = (trackHeight * i + trackMargin) + (trackHeight - trackMargin * 2) / 2;
            int markY = trackCenterY - (markHeight / 2);

            for (int j = 0; j < (markCount * 2); j++) {
                if (j % 2 == 1) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(j * markWidth + markMargin - markWidth / 2, markY, markWidth - markMargin * 2, markHeight);
                }
            }

            double trueTrackSize = 0.8;
            double position = Math.min(drawableRacer.getPosition(), race.getRaceDistance());

            int racerX = (int) (((position / race.getRaceDistance()) * (width * trueTrackSize)) + (width * ((1.0 - trueTrackSize) / 2.0)));
            drawableRacer.drawRacer(g, racerX, trackCenterY);
        }
    }

    @Override
    public void drawEmptyRace(IRace race) {
        this.race = race;
    }

    @Override
    public void drawRoundStart(IRace race) {
        this.race = race;
    }

    @Override
    public void drawRacer(IRace race, IRacer racer) {
        this.race = race;
    }

    @Override
    public void drawRoundEnd(IRace race) {
        this.race = race;
    }

    @Override
    public void drawWinner(IRace race) {
        this.race = race;

        setTitle(race.getWinner().getName() + " wins!");
    }
}
