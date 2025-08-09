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

import org.example.drools.ttt.incmd.PlaceCmd;
import org.example.drools.ttt.incmd.ResetCmd;
import org.example.drools.ttt.outcmd.LabelUpdCmd;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;


public class GameUI extends JFrame {

    private KieSession kSession;

    // 押下済のボタン記録用
    private PlaceCmd[][] placeCmds = new PlaceCmd[3][3];

    private JButton[][] btns = new JButton[3][3];

    // 先行は「◯」
    private String currentMark = "◯";

    private JLabel statusLabel = new JLabel("◯ の番です", SwingConstants.CENTER);

    private boolean gameOver = false;

    public GameUI(KieSession kSession) {
        this.kSession = kSession;

        setTitle("マルバツゲーム");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);

        // ステータスラベル
        this.statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);
        
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
                btn.addActionListener(e -> place(row, col));
                this.btns[row][col] = btn;
                boardPanel.add(btn);
            }
        }

        addRuleEventListener(this.kSession);

        setVisible(true);

        this.kSession.fireUntilHalt();
    }

    private void addRuleEventListener(KieSession kSession) {
        kSession.addEventListener(new RuleRuntimeEventListener() {

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                Object obj = event.getObject();

                if (obj.getClass().getName().equals("org.example.drools.ttt.outcmd.LabelUpdCmd")) {
                    statusLabel.setText(((LabelUpdCmd)obj).label());
                }

                if (obj.getClass().getName().equals("org.example.drools.ttt.outcmd.GameOverCmd")) {
                    gameOver = true;
                }
                // System.out.println("Fact inserted: " + obj.getClass());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {}

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {} 
        });
    }

    private void place(int row, int col) {
        if (gameOver) {
            return;
        }

        if (this.placeCmds[row][col] == null) {
            this.btns[row][col].setText(this.currentMark);

            // ルールエンジンにコマンド投入
            var cmd = new PlaceCmd(row, col, this.currentMark);
            this.placeCmds[row][col] = cmd;
            this.kSession.insert(cmd);

            // 手番更新
            if (this.currentMark.equals("◯")) {
                this.currentMark = "✕";
            } else {
                this.currentMark = "◯";
            }
        }
    }

    private void resetGame() {
        this.statusLabel.setText("◯ の番です");
        this.gameOver = false;
        this.placeCmds = new PlaceCmd[3][3];
        
        for (JButton[] btns : this.btns) {
            for (JButton btn: btns) {
                btn.setText("");
            }
        }
        this.currentMark = "◯";

        this.kSession.insert(new ResetCmd());
    }
}
