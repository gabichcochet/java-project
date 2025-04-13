import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import javax.swing.Timer;

public class MemoryGame extends JFrame {
    private final List<CardButton> cards = new ArrayList<>();
    private CardButton firstSelected = null;
    private CardButton secondSelected = null;
    private Timer flipBackTimer;
    private int essais = 0;
    private JLabel essaisLabel;
    private JLabel instructionsLabel; // Label pour les règles du jeu

    public MemoryGame() {
        setTitle("Jeu de Memory - Animaux");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Instructions du jeu affichées au départ
        instructionsLabel = new JLabel("<html><center>Objectif du Jeu :<br><br>" + 
            "Le but est de trouver toutes les paires de cartes correspondantes en un minimum de coups.<br>" +
            "Retournez deux cartes à la fois, mémorisez leur position et cherchez les paires.<br>" +
            "Lorsque vous trouvez une paire, elles restent retournées.<br>" +
            "Le jeu se termine lorsque toutes les paires sont trouvées.<br><br>" +
            "Instructions :<br>" +
            "1. Cliquez sur une carte pour la retourner.<br>" +
            "2. Cliquez sur une deuxième carte pour la retourner.<br>" +
            "3. Si elles correspondent, elles restent visibles.<br>" +
            "4. Si elles ne correspondent pas, elles se retournent après un court délai.<br>" +
            "5. Le jeu est terminé quand toutes les paires sont trouvées.<br><br>" +
            "Bonne chance !</center></html>");
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Ajouter instructionsLabel au centre de la fenêtre
        add(instructionsLabel, BorderLayout.CENTER); 

        // ESC key returns to launcher
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnToLauncher");
        getRootPane().getActionMap().put("returnToLauncher", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Launcher.main(null);
            }
        });

        essaisLabel = new JLabel("Essais : 0");
        essaisLabel.setFont(new Font("Arial", Font.BOLD, 16));
        essaisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(essaisLabel, BorderLayout.NORTH);

        initializeGame();
    }

    private void initializeGame() {
        essais = 0;
        updateEssaisLabel();

        List<String> animals = Arrays.asList(
            "hare", "gorille", "polarbear", "elephant", "camel",
            "bird", "cheetah", "seal", "tiger"
        );

        List<String> icons = new ArrayList<>();
        for (String animal : animals) {
            icons.add(animal);
            icons.add(animal);
        }

        Collections.shuffle(icons);
        getContentPane().removeAll();
        cards.clear();

        setLayout(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(3, 6));
        for (String iconName : icons) {
            CardButton card = new CardButton(iconName);
            card.addActionListener(e -> handleCardClick(card));
            cards.add(card);
            gridPanel.add(card);
        }

        add(essaisLabel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void handleCardClick(CardButton clicked) {
        // Masquer les instructions dès que la première carte est retournée
        if (instructionsLabel.isVisible()) {
            instructionsLabel.setVisible(false);
        }

        if (clicked.isMatched() || clicked.isShowingFace()) return;

        clicked.showFace();

        if (firstSelected == null) {
            firstSelected = clicked;
        } else if (secondSelected == null && clicked != firstSelected) {
            secondSelected = clicked;
            essais++;
            updateEssaisLabel();

            if (firstSelected.getIconName().equals(secondSelected.getIconName())) {
                firstSelected.setMatched(true);
                secondSelected.setMatched(true);
                resetSelection();

                if (isGameWon()) {
                    JOptionPane.showMessageDialog(this, "Bravo ! Vous avez gagné en " + essais + " essais !");
                    restartGame();
                }
            } else {
                flipBackTimer = new Timer(1000, e -> {
                    firstSelected.hideFace();
                    secondSelected.hideFace();
                    resetSelection();
                });
                flipBackTimer.setRepeats(false);
                flipBackTimer.start();
            }
        }
    }

    private void updateEssaisLabel() {
        essaisLabel.setText("Essais : " + essais);
    }

    private void resetSelection() {
        firstSelected = null;
        secondSelected = null;
    }

    private boolean isGameWon() {
        return cards.stream().allMatch(CardButton::isMatched);
    }

    private void restartGame() {
        initializeGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryGame().setVisible(true));
    }

    // ---- Inner class for card buttons ----
    static class CardButton extends JButton {
        private final String iconName;
        private boolean matched = false;
        private boolean showingFace = false;

        private static final ImageIcon BACK_ICON = new ImageIcon("bin/image/card_back.png");

        public CardButton(String iconName) {
            this.iconName = iconName;
            hideFace();
        }

        public String getIconName() {
            return iconName;
        }

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }

        public boolean isShowingFace() {
            return showingFace;
        }

        public void showFace() {
            setIcon(new ImageIcon("bin/image/" + iconName + ".png"));
            showingFace = true;
        }

        public void hideFace() {
            setIcon(BACK_ICON);
            showingFace = false;
        }
    }
}
