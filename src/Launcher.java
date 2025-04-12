import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        // Create the launcher frame
        JFrame launcherFrame = new JFrame("Game Launcher");
        launcherFrame.setSize(500, 350);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up scroll pane and panel for the list of games
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        // Load the menu background image
        ImageIcon flappyIcon = new ImageIcon(Launcher.class.getResource("/image/menu_background.png"));
        Image flappyImage = flappyIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton flappyBirdButton = new JButton(new ImageIcon(flappyImage));
        flappyBirdButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button in the panel
        flappyBirdButton.setPreferredSize(new Dimension(150, 100));

        // Action for Flappy Bird button
        flappyBirdButton.addActionListener(e -> {
            launcherFrame.dispose(); // Close the launcher
            FlappyBird.main(null); // Start the Flappy Bird game
        });

        // Add Flappy Bird button to the game panel
        // Add title label and Flappy Bird button to the game panel
        JLabel flappyLabel = new JLabel("Flappy-Bara");
        flappyLabel.setForeground(Color.BLACK); // Make it white (if background is dark)
        flappyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flappyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gamePanel.add(flappyLabel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gamePanel.add(flappyBirdButton);


        // Add more games (placeholders)
        // You can add additional buttons for other games below
        ImageIcon plusMinusIcon = new ImageIcon(Launcher.class.getResource("/image/Plus_and_Minus.png"));
        Image plusMinusImage = plusMinusIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton plusOuMoinsButton = new JButton(new ImageIcon(plusMinusImage));
        plusOuMoinsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button in the panel
        plusOuMoinsButton.setPreferredSize(new Dimension(150, 100));

        // Action for Plus ou Moins button
        plusOuMoinsButton.addActionListener(e -> {
            launcherFrame.dispose(); // Close the launcher
            try {
                // Start the Plus ou Moins game (make sure the class is compiled and available in the classpath)
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", "bin", "PlusOuMoinsSwing");
                pb.inheritIO();
                pb.start();
            } catch (Exception ex) {
                ex.printStackTrace(); // Print any error that occurs while launching the game
            }
        });
        JLabel plusMinusTitle = new JLabel("Plus ou Moins", SwingConstants.CENTER);
        plusMinusTitle.setFont(new Font("Arial", Font.BOLD, 16));
        plusMinusTitle.setForeground(Color.BLACK);
        plusMinusTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
        gamePanel.add(plusMinusTitle);
        // Add the Plus ou Moins button to the game panel
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
        gamePanel.add(plusOuMoinsButton);

        // Set up scroll pane for the panel
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the scroll pane to the frame
        launcherFrame.add(scrollPane);

        // Display the frame
        launcherFrame.setLocationRelativeTo(null); // Center the window
        launcherFrame.setVisible(true);
    }
}
