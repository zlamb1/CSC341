package com.github.zlamb1.assignment3.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuBar extends JMenuBar {
    protected final ICanvasArea canvasArea;
    protected FileDialog imageOpener;
    protected FileDialog imageSaver;

    public MenuBar(ICanvasArea canvasArea) {
        super();

        this.canvasArea = canvasArea;

        // We use a FileDialog over JFileChooser because FileDialog has the native UI for opening/saving files.
        imageOpener = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Open Image", FileDialog.LOAD);
        imageSaver = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save Image", FileDialog.SAVE);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMargin(new Insets(3, 10, 3, 10));

        JMenuItem openImageMenuItem = makeMenuItem("Open Image");
        openImageMenuItem.addActionListener(e -> {
            promptOpenImage();
        });

        fileMenu.add(openImageMenuItem);

        JMenuItem saveImageMenuItem = makeMenuItem("Save Image");
        saveImageMenuItem.addActionListener(e -> {
            promptSaveImage();
        });

        fileMenu.add(saveImageMenuItem);

        add(fileMenu);
    }

    protected JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.setMargin(new Insets(3, 5, 3, 5));
        return item;
    }

    protected void promptOpenImage() {
        File cwd = new File(".");
        imageOpener.setDirectory(cwd.getAbsolutePath());
        imageOpener.setFilenameFilter((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));

        imageOpener.setVisible(true);

        if (imageOpener.getDirectory() != null && imageOpener.getFile() != null) {
            File selectedFile = new File(imageOpener.getDirectory(), imageOpener.getFile());
            try {
                BufferedImage image = ImageIO.read(selectedFile);
                canvasArea.setCanvasImage(image);
            } catch (IOException exc)
            {
                exc.printStackTrace();
            }
        }
    }

    protected void promptSaveImage() {
        File cwd = new File(".");
        imageSaver.setDirectory(cwd.getAbsolutePath());
        imageSaver.setFile("canvas.jpg");
        imageSaver.setFilenameFilter((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));

        // blocks thread until user selects
        imageSaver.setVisible(true);

        if (imageSaver.getDirectory() != null && imageSaver.getFile() != null) {
            File selectedFile = new File(imageSaver.getDirectory(), imageSaver.getFile());
            try {
                if (selectedFile.exists() || selectedFile.createNewFile()) {
                    Dimension imageSize = canvasArea.getCanvasSize();

                    String ext = "";
                    if (selectedFile.getName().contains(".")) {
                        ext = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1);
                    }

                    int imageType = switch (ext) {
                        case "png" -> BufferedImage.TYPE_INT_ARGB;
                        default -> BufferedImage.TYPE_INT_RGB;
                    };

                    BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, imageType);
                    Graphics g = image.createGraphics();
                    canvasArea.drawCanvas(g);
                    g.dispose();
                    ImageIO.write(image, "jpg", selectedFile);
                }
            } catch (IOException ignored)
            {
                // FIXME
            }
        }
    }
}
