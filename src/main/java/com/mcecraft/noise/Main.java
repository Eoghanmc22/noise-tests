package com.mcecraft.noise;

import de.articdive.jnoise.JNoise;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Main {

    final static Object lock = new Object();
    static int noiseType = 0;
    static int freqV = 64;
    static int seedV = 67489;
    static int octavesV = 1;
    static double lacunarityV = 0.5;
    static double persistenceV = 0.5;
    static JNoise noise = JNoise.newBuilder().octavated().setNoise(JNoise.newBuilder().superSimplex().setFrequency(1.0 / freqV).setSeed(seedV).build()).setLacunarity(lacunarityV).setPersistence(persistenceV).setOctaves(octavesV).build();

    public static void updateNoise() {
        synchronized (lock) {
            JNoise base = JNoise.newBuilder().superSimplex().setFrequency(1.0 / freqV).setSeed(seedV).build();
            switch (noiseType) {
                case 0 -> base = JNoise.newBuilder().superSimplex().setFrequency(1.0 / freqV).setSeed(seedV).build();
                case 1 -> base = JNoise.newBuilder().fastSimplex().setFrequency(1.0 / freqV).setSeed(seedV).build();
                case 2 -> base = JNoise.newBuilder().perlin().setFrequency(1.0 / freqV).setSeed(seedV).build();
                case 3 -> base = JNoise.newBuilder().worley().setFrequency(1.0 / freqV).setSeed(seedV).build();
            }
            noise = JNoise.newBuilder().octavated().setNoise(base).setLacunarity(lacunarityV).setPersistence(persistenceV).setOctaves(octavesV).build();
        }
    }

    //lacunarity = freq mul, persistence = amplitude mul
    public static void main(String[] args) {
        JFrame frame = new JFrame();

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));
        JLabel freqL = new JLabel("freq");
        JTextField freq = new JTextField(5);
        JLabel seedL = new JLabel("seed");
        JTextField seed = new JTextField(5);
        JLabel octavesL = new JLabel("octaves");
        JTextField octaves = new JTextField(5);
        JLabel lacunarityL = new JLabel("lacunarity");
        JTextField lacunarity = new JTextField(5);
        JLabel persistenceL = new JLabel("persistence");
        JTextField persistence = new JTextField(5);

        Panel panel = new Panel();

        spinner.addChangeListener(e -> {
            try {
                noiseType = (int) spinner.getValue();
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        freq.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                freqV = Integer.parseInt(freq.getText());
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {

            }
        });

        seed.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                seedV = Integer.parseInt(seed.getText());
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {

            }
        });

        octaves.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                octavesV = Integer.parseInt(octaves.getText());
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {

            }
        });

        lacunarity.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                lacunarityV = Double.parseDouble(lacunarity.getText());
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {

            }
        });

        persistence.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            try {
                persistenceV = Double.parseDouble(persistence.getText());
                updateNoise();
                panel.paint(panel.getGraphics());
            } catch (Throwable t) {

            }
        });

        frame.setLayout(new FlowLayout());

        frame.add(spinner);
        frame.add(freqL);
        frame.add(freq);
        frame.add(seedL);
        frame.add(seed);
        frame.add(octavesL);
        frame.add(octaves);
        frame.add(lacunarityL);
        frame.add(lacunarity);
        frame.add(persistenceL);
        frame.add(persistence);

        int width = 500;
        frame.add(panel);
        frame.setBounds(0, 0, width, width);
        panel.setSize(new Dimension(width, width));
        panel.setPreferredSize(new Dimension(width, width));
        panel.setMinimumSize(new Dimension(width, width));
        panel.setMaximumSize(new Dimension(width, width));
        panel.setBounds(0,0,width,width);
        panel.invalidate();
        frame.setVisible(true);
        frame.setBounds(0, 0, width - panel.getWidth() + width, width - panel.getHeight() + width);
    }

    public static class Panel extends JPanel {

        @Override
        public void paint(Graphics g) {
            synchronized (lock) {
                BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {
                        float value = (float) (noise.getNoise(x, y) + 1) / 2;
                        int valueI = (int) (value*255+0.5);
                        image.setRGB(x, y, valueI << 16 | valueI << 8 | valueI);
                    }
                }
                g.drawImage(image, 0, 0, null);
            }
        }
    }

    @FunctionalInterface
    public interface SimpleDocumentListener extends DocumentListener {
        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void removeUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }
}
