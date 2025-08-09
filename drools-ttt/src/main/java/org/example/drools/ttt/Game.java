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

import org.example.drools.ttt.command.PlaceCommand;
import org.example.drools.ttt.command.ResetCommand;
import org.example.drools.ttt.model.Field;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;


public class Game extends JFrame {

    // 押下済のボタン記録用
    private String[][] fields = new String[3][3];

    private JButton[][] btns = new JButton[3][3];

    private JLabel statusLabel = new JLabel("◯ の番です", SwingConstants.CENTER);

    private KieSession kSession;

    public Game(KieSession kSession) {
        this.kSession = kSession;

        setTitle("マルバツゲーム");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);

        // ステータスラベル
        this.statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
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
                this.btns[row][col] = btn;
                boardPanel.add(btn);
                this.kSession.insert(btn);
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
                System.out.println("Fact inserted: " + obj.getClass());                
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {}

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {}
            
        });

    }

    private void handleMove(int row, int col, JButton btn) {
        if (this.fields[row][col] == null) {
            this.fields[row][col] = "配置済";
            this.kSession.insert(new Field(row, col, btn));
            this.kSession.insert(new PlaceCommand(row, col));
        }
    }

    private void place(int row, int col) {
        if (this.fields[row][col] == null) {
            this.fields[row][col] = "配置済";
            this.kSession.insert(new PlaceCommand(row, col));
        }
    }

    private void resetGame() {
        this.statusLabel = new JLabel("◯ の番です", SwingConstants.CENTER);
        this.fields = new String[3][3];
        this.kSession.insert(new ResetCommand());
    }
}
