package org.example.drools.uttt;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class OverlayPanel extends JPanel {
    private String winner = "";

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
