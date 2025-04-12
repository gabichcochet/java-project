import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {
        JFrame launcherFrame = new JFrame("Game Launcher");
        launcherFrame.setSize(500, 350);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.setLayout(new GridLayout(3, 1));

        JLabel title = new JLabel("Choisissez un jeu", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        launcherFrame.add(title);

        JButton flappyButton = new JButton("Flappy Bird");
        flappyButton.setFont(new Font("Arial", Font.PLAIN, 18));
        flappyButton.addActionListener(e -> {
            launcherFrame.dispose();
            FlappyBird.main(null);
        });

        JButton plusOuMoinsButton = new JButton("Play Plus ou Moins");
        plusOuMoinsButton.setFont(new Font("Arial", Font.PLAIN, 18));
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

        launcherFrame.add(flappyButton);
        launcherFrame.add(plusOuMoinsButton);

        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setVisible(true);
    }
}
