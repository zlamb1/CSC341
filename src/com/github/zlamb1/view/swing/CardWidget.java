package com.github.zlamb1.view.swing;

import com.github.zlamb1.assignment2.view.CardLabel;
import com.github.zlamb1.card.Card;
import com.github.zlamb1.view.animation.AnimationTween;
import com.github.zlamb1.view.animation.ComponentAnimation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CardWidget extends JPanel implements MouseListener {
    protected Card card;
    protected boolean renderBack;
    protected boolean interactable;

    protected Color borderColor;
    protected Color hoverBorderColor;

    protected boolean isHovering = false;

    protected double defaultScaleTarget = 0.98;
    protected ComponentAnimation scale = new ComponentAnimation(this, 1.0, defaultScaleTarget) {
        @Override
        public boolean isFinished() {
            return !isInteractable();
        }
    };

    public CardWidget(Card card) {
        super();

        this.card = card;

        renderBack = false;
        interactable = false;

        addMouseListener(this);

        scale.getTween().setAnimationDuration(0.1);
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        repaint();
    }

    public boolean doesRenderBack() {
        return renderBack;
    }

    public void setRenderBack(boolean renderBack) {
        this.renderBack = renderBack;
        repaint();
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
        setCursor(Cursor.getPredefinedCursor(interactable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    public Color getCardColor() {
        return getForeground();
    }

    public void setCardBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public void setCardHoverBorderColor(Color hoverBorderColor) {
        this.hoverBorderColor = hoverBorderColor;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(60, 100);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final int borderArc = 10;
        final int rankPadding = 3;
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // clear draw area
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        AffineTransform transform = g2d.getTransform();
        transform.translate(getWidth() / 2.0, getHeight() / 2.0);
        transform.scale(scale.getValue(), scale.getValue());
        transform.translate(getWidth() / -2.0, getHeight() / -2.0);
        g2d.setTransform(transform);

        g2d.setColor(getCardColor());

        if (renderBack) {
            g2d.drawImage(getBackImageWithSize(getSize()), 0, 0, null);
        } else {
            // draw top-left rank
            g2d.drawString(card.getRank().getShorthand(), getInsets().left + rankPadding, g.getFontMetrics()
                .getMaxAscent() + getInsets().top
            );

            Font originalFont = g.getFont();
            g2d.setFont(originalFont.deriveFont(AffineTransform.getScaleInstance(-1, -1)));

            // draw bottom-right flipped rank
            g2d.drawString(card.getRank().getShorthand(),
                getWidth() - getInsets().right - (g.getFontMetrics().stringWidth(card.getRank().getShorthand()) / 2) - rankPadding - 3,
                getHeight() + getInsets().top - rankPadding - 10
            );

            g2d.setFont(originalFont.deriveFont(24.0f));
            String suitSymbol = switch (card.getSuit()) {
                case JOKER -> "\uD83C\uDCCF";
                case SPADES -> "♠";
                case CLUBS -> "♣";
                case HEARTS -> "♥";
                case DIAMONDS -> "♦";
            };

            Rectangle2D symbolBounds = g.getFontMetrics().getStringBounds(suitSymbol, g);
            g2d.drawString(suitSymbol, getWidth() / 2 - (int) (symbolBounds.getWidth() / 2),
                getHeight() / 2 + (int) (symbolBounds.getHeight() / 4));
        }

        g2d.setColor(getCardBorder());
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderArc, borderArc);

        g.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        scale.getTween().setTarget(0.93);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        scale.getTween().setTarget(defaultScaleTarget);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        isHovering = true;

        if (isInteractable()) {
            scale.getTween().reset();
            scale.getTween().setAnimationDirection(AnimationTween.AnimationDirection.FORWARD);
            scale.getTween().start();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isHovering = false;

        if (isInteractable()) {
            scale.getTween().reverse();
        }
    }

    protected Color getCardBorder() {
        if (isHovering && isInteractable() && hoverBorderColor != null) {
            return hoverBorderColor;
        }
        if (borderColor != null) {
            return borderColor;
        }
        return getCardColor();
    }

    protected static final String CARD_BACK_IMG_URL = "com/github/zlamb1/assignment2/images/card.png";
    protected static Image backImage;
    protected static Map<Dimension, Image> backImageCache = new HashMap<>();

    protected static Image getBackImageWithSize(Dimension size) {
        if (backImage == null) {
            try {
                URL imageUrl = CardLabel.class.getClassLoader().getResource(CARD_BACK_IMG_URL);
                assert imageUrl != null;
                backImage = ImageIO.read(imageUrl);
                Image backImageScaled = backImage.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_SMOOTH);
                backImageCache.put(size, backImageScaled);
                return backImageScaled;
            } catch (IOException exc) {
                throw new RuntimeException("Failed to Load CardWidget Back Image");
            }
        }

        if (backImageCache.containsKey(size)) {
            return backImageCache.get(size);
        } else {
            Image backImageScaled = backImage.getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_SMOOTH);
            backImageCache.put(size, backImageScaled);
            return backImageScaled;
        }
    }
}
