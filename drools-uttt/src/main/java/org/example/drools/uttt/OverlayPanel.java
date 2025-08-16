package org.example.drools.uttt;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class OverlayPanel extends JPanel {
    private String winner = "";

    private boolean isFilled = false;

    public OverlayPanel(int x, int y, int width, int height) {
        super();

        this.setBounds(x, y, width, height);
        this.setOpaque(false);
        this.setVisible(true);
        this.setBorder(new LineBorder(Color.RED, 2, true));
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isFilled() {
        return this.isFilled;
    }

    public void fill() {
        this.isFilled = true;
        this.repaint();
    }

    public void reset() {
        this.isFilled = false;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isFilled) {
            Graphics2D g2 = (Graphics2D) g;
            // 半透明白
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("SansSerif", Font.BOLD, 130));
            g2.setColor(Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            String str = String.valueOf(winner);
            int x = (getWidth() - fm.stringWidth(str)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 20;
            g2.drawString(str, x, y);
        }
    }
}
