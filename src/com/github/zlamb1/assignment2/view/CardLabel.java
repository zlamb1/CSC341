package com.github.zlamb1.assignment2.view;

import com.github.zlamb1.card.Card;
import com.github.zlamb1.view.swing.RoundedBorder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CardLabel extends JLabel {
    private static final String CARD_BACK_IMG_URL = "com/github/zlamb1/assignment2/images/card.png";
    private static Image backImage = null;
    private static final Map<Dimension, Image> backImageCache = new HashMap<>();

    protected static Image getBackImage(int width, int height) {
        Dimension key = new Dimension(width, height);

        if (backImage == null) {
            try {
                URL imageUrl = CardLabel.class.getClassLoader().getResource(CARD_BACK_IMG_URL);
                assert imageUrl != null;
                backImage = ImageIO.read(imageUrl);
                backImageCache.put(key, backImage.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException exc) {
                throw new RuntimeException("Failed to Load CardLabel Image");
            }
        }

        if (backImageCache.containsKey(key)) {
            return backImageCache.get(key);
        } else {
            Image image = backImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            backImageCache.put(key, image);
            return image;
        }
    }

    private final int borderRadius = 10;
    private final int borderWidth = 2;

    private final Card card;
    private boolean active;
    private boolean selected;
    private final boolean renderBack;
    private Color defaultBorderColor = null;

    protected Color getCardColor() {
        return switch (card.getColor()) {
            case BLACK -> Color.BLACK;
            case RED -> Color.RED;
        };
    }

    public CardLabel(Card card, boolean active, boolean renderBack) {
        this.card = card;
        this.renderBack = renderBack;
        this.active = active;
        selected = false;
        setDefaultBorder();
        setPreferredSize(new Dimension(60, 100));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isActive()) {
                    setSelectedBorder();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected()) {
                    setDefaultBorder();
                }
            }
        });
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setSelectedBorder();
        } else {
            setDefaultBorder();
        }
    }

    public Card getCard() {
        return card;
    }

    public void setDefaultBorderColor(Color defaultBorderColor) {
        this.defaultBorderColor = defaultBorderColor;
    }

    public void setDefaultBorder() {
        setBorder(new RoundedBorder(borderRadius, borderWidth, defaultBorderColor == null ? getCardColor() : defaultBorderColor));
    }

    public void setSelectedBorder() {
        setBorder(new RoundedBorder(borderRadius, borderWidth, new Color(210, 210, 0 )));
    }

    @Override
    public void paintComponent(final Graphics g) {
        final int rankPadding = 3;
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setColor(getCardColor());

        if (renderBack) {
            g.drawImage(getBackImage(getWidth(), getHeight()), 0, 0, null);
        } else {
            // draw top-left rank
            g.drawString(card.getRank().getShorthand(), getInsets().left + rankPadding, g.getFontMetrics()
                .getMaxAscent() + getInsets().top
            );

            Font originalFont = g.getFont();
            g.setFont(originalFont.deriveFont(AffineTransform.getScaleInstance(-1, -1)));

            // draw bottom-right flipped rank
            g.drawString(card.getRank().getShorthand(),
                getWidth() - getInsets().right - (g.getFontMetrics().stringWidth(card.getRank().getShorthand()) / 2) - rankPadding - 3,
                getHeight() + getInsets().top - rankPadding - 15
            );

            g.setFont(originalFont.deriveFont(24.0f));
            String suitSymbol = switch (card.getSuit()) {
                case JOKER -> "\uD83C\uDCCF";
                case SPADES -> "♠";
                case CLUBS -> "♣";
                case HEARTS -> "♥";
                case DIAMONDS -> "♦";
            };

            Rectangle2D symbolBounds = g.getFontMetrics().getStringBounds(suitSymbol, g);
            g.drawString(suitSymbol, getWidth() / 2 - (int) (symbolBounds.getWidth() / 2),
                getHeight() / 2 + (int) (symbolBounds.getHeight() / 4));
        }
    }
}
