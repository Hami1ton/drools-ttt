package org.example.drools.ttt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.example.drools.ttt.model.*;
import org.kie.api.runtime.KieSession;

public class Game extends JFrame {

    private JButton[][] buttons = new JButton[3][3];
    private CurrentPlayer player = new CurrentPlayer("◯");
    private JLabel statusLabel = new JLabel("◯ の番です", SwingConstants.CENTER);;

    GameStatus status = new GameStatus();

    KieSession kSession;

    public Game(KieSession kSession) {
        this.kSession = kSession;

        setTitle("マルバツゲーム");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);
        this.kSession.insert(statusLabel);

        JButton resetButton = new JButton("リセット");
        resetButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        resetButton.addActionListener(e -> resetGame());
        add(resetButton, BorderLayout.SOUTH);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBackground(Color.WHITE);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 48);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(buttonFont);
                btn.setFocusPainted(false);
                btn.setBackground(new Color(220, 240, 255)); // 薄い青
                int row = i, col = j;
                btn.addActionListener(e -> handleMove(row, col));
                buttons[i][j] = btn;
                boardPanel.add(btn);
                this.kSession.insert(btn);
            }
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 400));
        boardPanel.setBounds(0, 0, 400, 400);
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        add(layeredPane, BorderLayout.CENTER);

        this.kSession.insert(player);
        this.kSession.insert(status);

        setVisible(true);

        this.kSession.fireUntilHalt();
    }

    private void handleMove(int row, int col) {
        if (!status.isGameOver) {
            this.kSession.insert(new Field(row, col, buttons[row][col]));
        }
    }

    private void resetGame() {
        this.kSession.insert(new ResetCommand());
    }
}
