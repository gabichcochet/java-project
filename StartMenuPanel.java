import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartMenuPanel extends JPanel {
    private Image background;

    public StartMenuPanel(CardLayout cardLayout, JPanel container) {
        setLayout(new BorderLayout());
        setBackground(Color.cyan);

        // Load the background image
        background = new ImageIcon(getClass().getResource("/image/menu_background.png")).getImage();

        // Create the title label
        JLabel titleLabel = new JLabel("Flappy Bird", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.CENTER);

        // Create the start button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 30));
        startButton.addActionListener(e -> cardLayout.show(container, "Game"));  // Simplified lambda expression

        // Panel to hold the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image (handles panel resizing)
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
