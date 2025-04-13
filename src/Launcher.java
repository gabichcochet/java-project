import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Launcher {
    public static void main(String[] args) {
        JFrame launcherFrame = new JFrame("Game Launcher");
        launcherFrame.setSize(500, 350);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        // Flappy Bird
        ImageIcon flappyIcon = new ImageIcon(Launcher.class.getResource("/image/menu_background.png"));
        Image flappyImage = flappyIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton flappyBirdButton = new JButton(new ImageIcon(flappyImage));
        flappyBirdButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        flappyBirdButton.setPreferredSize(new Dimension(150, 100));
        flappyBirdButton.addActionListener(e -> {
            launcherFrame.dispose();
            FlappyBird.main(null);
        });
        JLabel flappyLabel = new JLabel("Flappy-Bara");
        flappyLabel.setForeground(Color.BLACK);
        flappyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flappyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(flappyLabel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gamePanel.add(flappyBirdButton);

        // Plus ou Moins
        ImageIcon plusMinusIcon = new ImageIcon(Launcher.class.getResource("/image/Plus_and_Minus.png"));
        Image plusMinusImage = plusMinusIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton plusOuMoinsButton = new JButton(new ImageIcon(plusMinusImage));
        plusOuMoinsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        plusOuMoinsButton.setPreferredSize(new Dimension(150, 100));
        plusOuMoinsButton.addActionListener(e -> {
            launcherFrame.dispose();
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "PlusOuMoinsSwing");
                pb.inheritIO();
                pb.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        JLabel plusMinusTitle = new JLabel("Plus ou Moins", SwingConstants.CENTER);
        plusMinusTitle.setFont(new Font("Arial", Font.BOLD, 16));
        plusMinusTitle.setForeground(Color.BLACK);
        plusMinusTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(plusMinusTitle);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(plusOuMoinsButton);

        // Blackjack
        ImageIcon blackjackIcon = new ImageIcon(Launcher.class.getResource("/image/blackjackicon.png"));
        Image blackjackImage = blackjackIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton blackjackButton = new JButton(new ImageIcon(blackjackImage));
        blackjackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        blackjackButton.setPreferredSize(new Dimension(150, 100));
        blackjackButton.addActionListener(e -> {
            launcherFrame.dispose();
            SwingUtilities.invokeLater(() -> new BlackJack().setVisible(true));
        });
        JLabel blackjackLabel = new JLabel("Blackjack", SwingConstants.CENTER);
        blackjackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackjackLabel.setForeground(Color.BLACK);
        blackjackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(blackjackLabel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(blackjackButton);

        // Memory Game
        ImageIcon memoryIcon = new ImageIcon(Launcher.class.getResource("/image/memoryicon.png"));
        Image memoryImage = memoryIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton memoryButton = new JButton(new ImageIcon(memoryImage));
        memoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        memoryButton.setPreferredSize(new Dimension(150, 100));
        memoryButton.addActionListener(e -> {
            launcherFrame.dispose();
            SwingUtilities.invokeLater(() -> new MemoryGame().setVisible(true));
        });
        JLabel memoryLabel = new JLabel("Memory Game", SwingConstants.CENTER);
        memoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        memoryLabel.setForeground(Color.BLACK);
        memoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(memoryLabel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(memoryButton);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        launcherFrame.add(scrollPane);

        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setVisible(true);
    }
}
