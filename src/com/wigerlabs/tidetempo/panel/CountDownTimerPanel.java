package com.wigerlabs.tidetempo.panel;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CountDownTimerPanel extends JPanel {
    private JLabel timerLabel;
    private JButton startStopButton;
    private Timer timer;
    private int timeLeft = 300; // 5 minutes in seconds
    private boolean isRunning = false;

    public CountDownTimerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Timer Label
        timerLabel = new JLabel("05:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 40));
        timerLabel.setForeground(new Color(0x2E7D32)); // Minimalist Green primary
        timerLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(timerLabel, gbc);

        // Circular Start/Stop Button
        startStopButton = new JButton("Start") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        startStopButton.setPreferredSize(new Dimension(100, 100));
        startStopButton.setFont(new Font("Arial", Font.BOLD, 16));
        startStopButton.setBackground(new Color(0x81C784)); // Minimalist Green secondary
        startStopButton.setForeground(Color.WHITE);
        startStopButton.setFocusPainted(false);
        startStopButton.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove default border
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(startStopButton, gbc);

        // Timer Logic
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning && timeLeft > 0) {
                    timeLeft--;
                    updateTimerLabel();
                } else if (timeLeft <= 0) {
                    stopTimer();
                    JOptionPane.showMessageDialog(CountDownTimerPanel.this, "Time's up!");
                }
            }
        });

        // Button Action
        startStopButton.addActionListener(e -> {
            if (!isRunning) {
                startTimer();
            } else {
                stopTimer();
            }
        });
    }

    private void startTimer() {
        isRunning = true;
        timer.start();
        startStopButton.setText("Stop");
        startStopButton.setBackground(new Color(0xF06292)); // Minimalist Green accent for Stop
    }

    private void stopTimer() {
        isRunning = false;
        timer.stop();
        startStopButton.setText("Start");
        startStopButton.setBackground(new Color(0x81C784)); // Back to Start color
    }

    private void updateTimerLabel() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Method to reset timer (optional)
    public void resetTimer(int seconds) {
        stopTimer();
        timeLeft = seconds;
        updateTimerLabel();
    }
}