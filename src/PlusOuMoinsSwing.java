import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PlusOuMoinsSwing extends JFrame {
    private int nombreMystere;
    private int essais;
    private int previousNombre = -1;
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton guessButton;

    public PlusOuMoinsSwing() {
        setTitle("Jeu du Plus ou Moins");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        nombreMystere = new Random().nextInt(101);
        essais = 0;

        JLabel titleLabel = new JLabel("Trouvez le nombre mystere entre 0 et 100 !", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setMargin(new Insets(10,10,10,10));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(10);
        guessButton = new JButton("Deviner");

        inputPanel.add(new JLabel("Votre proposition : "));
        inputPanel.add(inputField);
        inputPanel.add(guessButton);
        add(inputPanel, BorderLayout.SOUTH);

        guessButton.addActionListener(e -> makeGuess());
        inputField.addActionListener(e -> makeGuess());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose(); 
                    Launcher.main(null); 
                }
            }
        });

        setFocusable(true);  
    }

    private void makeGuess() {
        String input = inputField.getText().trim();
        try {
            int guess = Integer.parseInt(input);
            if (guess == previousNombre) {
                outputArea.append("Vous avez deja entrer ce nombre !\n");
                return;
            }

            essais++;

            if (guess < nombreMystere) {
                outputArea.append("C'est plus !\n");
            } else if (guess > nombreMystere) {
                outputArea.append("C'est moins !\n");
            } else {
                outputArea.append("Trouver en " + essais + " essais ! Bien jouer !\n");
                guessButton.setEnabled(false);
                inputField.setEnabled(false);
            }

            previousNombre = guess;
            inputField.setText("");
        } catch (NumberFormatException ex) {
            outputArea.append("Veuillez entrer un nombre valide !\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlusOuMoinsSwing().setVisible(true));
    }
}
