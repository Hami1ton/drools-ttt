package org.example.drools.ttt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.example.drools.ttt.model.Field;
import org.example.drools.ttt.model.ResetCommand;
import org.kie.api.runtime.KieSession;


public class Game extends JFrame {

    // 押下済のボタン記録用
    private String[][] fields = new String[3][3];

    private KieSession kSession;

    public Game(KieSession kSession) {
        this.kSession = kSession;

        setTitle("マルバツゲーム");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);

        // ステータスラベル
        JLabel statusLabel = new JLabel("◯ の番です", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);
        this.kSession.insert(statusLabel);

        // リセットボタン
        JButton resetButton = new JButton("リセット");
        resetButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        resetButton.addActionListener(e -> resetGame());
        add(resetButton, BorderLayout.SOUTH);

        // 盤面
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBounds(0, 0, 400, 400);
        add(boardPanel, BorderLayout.CENTER);

        // ボタン
        Font buttonFont = new Font("SansSerif", Font.BOLD, 48);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(buttonFont);
                btn.setFocusPainted(false);
                int row = i, col = j;
                btn.addActionListener(e -> handleMove(row, col, btn));
                boardPanel.add(btn);
                this.kSession.insert(btn);
            }
        }

        setVisible(true);

        this.kSession.fireUntilHalt();
    }

    private void handleMove(int row, int col, JButton btn) {
        if (this.fields[row][col] == null) {
            this.fields[row][col] = "配置済";
            this.kSession.insert(new Field(row, col, btn));
        }
    }

    private void resetGame() {
        this.fields = new String[3][3];
        this.kSession.insert(new ResetCommand());
    }
}
