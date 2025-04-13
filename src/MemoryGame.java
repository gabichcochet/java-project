import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List; // <-- EXPLICIT java.util.List
import javax.swing.Timer; // <-- EXPLICIT javax.swing.Timer

public class MemoryGame extends JFrame {
    private final List<CardButton> cards = new ArrayList<>();
    private CardButton firstSelected = null;
    private CardButton secondSelected = null;
    private Timer flipBackTimer;

    public MemoryGame() {
        setTitle("Jeu de Memory - Animaux");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ESC key returns to launcher
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnToLauncher");
        getRootPane().getActionMap().put("returnToLauncher", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close Memory game window
                Launcher.main(null); // Reopen the launcher
            }
        });

        initializeGame();
    }

    private void initializeGame() {
        // Animal names matching the image files (no extensions)
        List<String> animals = Arrays.asList(
            "hare", "gorille", "polarbear", "elephant", "camel",
            "bird", "cheetah", "seal", "tiger"
        );

        // Duplicate to create pairs
        List<String> icons = new ArrayList<>();
        for (String animal : animals) {
            icons.add(animal);
            icons.add(animal);
        }

        Collections.shuffle(icons);
        getContentPane().removeAll();
        cards.clear();

        setLayout(new GridLayout(3, 6)); // 3 rows x 6 columns = 18 cards

        for (String iconName : icons) {
            CardButton card = new CardButton(iconName);
            card.addActionListener(e -> handleCardClick(card));
            cards.add(card);
            add(card);
        }

        revalidate();
        repaint();
    }

    private void handleCardClick(CardButton clicked) {
        if (clicked.isMatched() || clicked.isShowingFace()) return;

        clicked.showFace();

        if (firstSelected == null) {
            firstSelected = clicked;
        } else if (secondSelected == null && clicked != firstSelected) {
            secondSelected = clicked;

            if (firstSelected.getIconName().equals(secondSelected.getIconName())) {
                firstSelected.setMatched(true);
                secondSelected.setMatched(true);
                resetSelection();

                if (isGameWon()) {
                    JOptionPane.showMessageDialog(this, "Bravo ! Vous avez gagné !");
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
