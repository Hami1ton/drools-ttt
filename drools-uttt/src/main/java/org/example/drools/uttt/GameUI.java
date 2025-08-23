package org.example.drools.uttt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.example.drools.uttt.incmd.*;
import org.example.drools.uttt.outcmd.*;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;


public class GameUI extends JFrame {

    private KieSession kSession;

    private JLabel statusLabel = new JLabel("✕ の番です", SwingConstants.CENTER);

    private JButton[][] btns = new JButton[9][9];

    // 押下済のボタン記録用
    private PlaceCmd[][] placeCmds = new PlaceCmd[9][9];

    // 先攻は「✕」
    private String currentMark = "✕";

    private boolean gameOver = false;

    // 巨大◯や✕を描画するパネル
    private OverlayPanel[][] overlayPanels = new OverlayPanel[3][3];


    public GameUI(KieSession kSession) {
        this.kSession = kSession;

        setTitle("スーパー マルバツゲーム");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(490, 550);
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
        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBounds(10, 0, 450, 450);
        add(boardPanel, BorderLayout.CENTER);

        // ボタン
        Font buttonFont = new Font("SansSerif", Font.BOLD, 15);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JButton btn = new JButton("");
                btn.setFont(buttonFont);
                btn.setFocusPainted(false);
                int row = i, col = j;
                btn.addActionListener(e -> place(row, col));
                this.btns[row][col] = btn;
                boardPanel.add(btn);
            }
        }

        // 盤面の上にオーバーレイを重ねるためのレイヤードペイン
        JLayeredPane layeredPane = new JLayeredPane();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                OverlayPanel overlayPanel = new OverlayPanel(10 + j * 150, i * 150, 150, 150);

                layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
                add(layeredPane, BorderLayout.CENTER);
                this.overlayPanels[i][j] = overlayPanel;
            }
        }
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        
        // out command受信時の処理
        addRuleEventListener(this.kSession);

        setVisible(true);

        this.kSession.fireUntilHalt();
    }

    private void addRuleEventListener(KieSession kSession) {
        kSession.addEventListener(new RuleRuntimeEventListener() {

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                Object obj = event.getObject();

                if (obj instanceof LabelUpdCmd cmd) {
                    statusLabel.setText(cmd.label());

                } else if (obj instanceof GameOverCmd) {
                    gameOver = true;
                    deactivateButtons();

                } else if (obj instanceof OverlayCmd cmd) {
                    overlayPanels[cmd.row()][cmd.col()].setWinner(cmd.mark());
                    overlayPanels[cmd.row()][cmd.col()].fill();

                } else if (obj instanceof FieldChangeCmd cmd) {
                    if(overlayPanels[cmd.localRow()][cmd.localCol()].isFilled()) {
                        // 移動先が勝敗確定済なら、全ボタンを押下可能にする
                        resetButtons(false);
                        return;
                    }
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            btns[i][j].setEnabled(false);
                            if (i / 3 == cmd.localRow() && j / 3 == cmd.localCol()) {
                                // FieldChangeCmdで指定した領域のみ押下可能にする
                                btns[i][j].setEnabled(true);
                            }
                        }
                    }
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
        if (this.gameOver) {
            return;
        }
        if (this.overlayPanels[row / 3][col / 3].isFilled()) {
            // 勝敗確定したGrobalFieldのボタンは押下できない
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
        this.statusLabel.setText("✕ の番です");
        resetButtons(true);
        for (OverlayPanel[] panelArr : this.overlayPanels) {
            for (OverlayPanel panel: panelArr) {
                panel.reset();
            }
        }
        this.placeCmds = new PlaceCmd[9][9];
        this.currentMark = "✕";
        this.gameOver = false;

        // ルールエンジンにコマンド投入
        this.kSession.insert(new ResetCmd());
    }

    private void resetButtons(boolean shouldDeleteLabel) {
        for (JButton[] btnArr : this.btns) {
            for (JButton btn: btnArr) {
                if (shouldDeleteLabel) {
                    btn.setText("");
                }
                btn.setEnabled(true);
            }
        }
    }

    private void deactivateButtons() {
        for (JButton[] btnArr : this.btns) {
            for (JButton btn: btnArr) {
                btn.setEnabled(false);
            }
        }
    }
}
