package com.github.zlamb1.assignment1.view;

import com.github.zlamb1.assignment1.AddressInfo;
import com.github.zlamb1.view.swing.SwingView;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.concurrent.CountDownLatch;

public class SwingAddressView extends SwingView implements IAddressView {
    @Override
    public void displayAddressInfo(Collection<AddressInfo> addressInfoColl) {
        CountDownLatch latch = new CountDownLatch(1);
        JPanel container = new JPanel(), top = new JPanel(), bottom = new JPanel();
        JScrollPane scrollPane = new JScrollPane(top);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        InfoLayout layout = infoLayout(container, scrollPane, bottom);

        JButton button = new JButton("Continue");
        button.addActionListener(e -> {
            latch.countDown();
        });

        OptionalInt widthSpecifier = addressInfoColl
            .stream()
            .mapToInt(a -> Math.max(Math.max(a.getFirstName().length(), a.getLastInitial().length()), a.getAddress().length()))
            .max();

        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        int i = 0;
        for (AddressInfo addressInfo : addressInfoColl) {
            JLabel label = new JLabel(addressInfo.toFormattedString(-widthSpecifier.orElse(1))) {
                @Override
                public Dimension getMaximumSize() {
                    Dimension d = super.getMaximumSize();
                    d.width = Integer.MAX_VALUE;
                    return d;
                }
            };
            // set monospaced font so that labels are not misaligned
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            label.setHorizontalAlignment(JLabel.CENTER);
            if (i % 2 == 1) {
                label.setBackground(Color.LIGHT_GRAY);
                label.setOpaque(true);
            }
            top.add(label);
            i++;
        }

        bottom.add(button);
        frame.setContentPane(layout.container);
        repaint();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AssertionError("Unexpected InterruptedException", e);
        }

        defaultLayout();
        repaint();
    }
}
