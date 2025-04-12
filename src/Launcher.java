import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        JFrame launcherFrame = new JFrame("Game Launcher");
        launcherFrame.setSize(500, 350);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

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

        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        launcherFrame.add(scrollPane);

        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setVisible(true);
    }
}
