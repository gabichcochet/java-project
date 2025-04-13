import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class BlackJack extends JFrame {
    private ArrayList<Card> playerCards;
    private ArrayList<Card> dealerCards;
    private Deck deck;
    private JLabel statusLabel;

    public BlackJack() {
        setTitle("Jeu de Blackjack");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        statusLabel = new JLabel("Bienvenue au Blackjack !");
        add(statusLabel);

        // ESC key returns to launcher
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnToLauncher");
        getRootPane().getActionMap().put("returnToLauncher", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close Blackjack window
                Launcher.main(null); // Reopen the launcher
            }
        });

        // Initialize game state
        initializeGame();

        JButton hitButton = new JButton("Tirer");
        JButton standButton = new JButton("Rester");
        JButton restartButton = new JButton("Recommencer");

        hitButton.addActionListener(e -> hit());
        standButton.addActionListener(e -> stand());
        restartButton.addActionListener(e -> initializeGame());

        add(hitButton);
        add(standButton);
        add(restartButton);

        updateGame();
    }

    private void initializeGame() {
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();
        deck = new Deck();
        dealInitialCards();
    }

    private void dealInitialCards() {
        playerCards.add(deck.dealCard());
        playerCards.add(deck.dealCard());
        dealerCards.add(deck.dealCard());
        dealerCards.add(deck.dealCard());
        updateGame();
    }

    private void hit() {
        playerCards.add(deck.dealCard());
        updateGame();
        int playerScore = calculateScore(playerCards);
        if (playerScore > 21) {
            statusLabel.setText("Vous avez dépassé 21 ! Le croupier gagne.");
        }
    }

    private void stand() {
        while (calculateScore(dealerCards) < 17) {
            dealerCards.add(deck.dealCard());
        }

        int playerScore = calculateScore(playerCards);
        int dealerScore = calculateScore(dealerCards);

        if (dealerScore > 21) {
            statusLabel.setText("Le croupier a dépassé 21 ! Vous gagnez.");
        } else if (playerScore > dealerScore) {
            statusLabel.setText("Vous gagnez !");
        } else if (playerScore < dealerScore) {
            statusLabel.setText("Le croupier gagne.");
        } else {
            statusLabel.setText("Égalité.");
        }

        updateGame();
    }

    private int calculateScore(ArrayList<Card> cards) {
        int score = 0;
        int aceCount = 0;

        for (Card card : cards) {
            String rank = card.getRank();
            if (rank.equals("jack") || rank.equals("queen") || rank.equals("king")) {
                score += 10;
            } else if (rank.equals("ace")) {
                aceCount++;
                score += 11;
            } else {
                score += Integer.parseInt(rank);
            }
        }

        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }

    private void updateGame() {
        getContentPane().removeAll();

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.X_AXIS));

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        JLabel playerLabel = new JLabel("Cartes du joueur :");
        playerPanel.add(playerLabel);
        for (Card card : playerCards) {
            ImageIcon cardImage = new ImageIcon(getScaledImage(card.getImagePath(), 80, 120));
            JLabel cardLabel = new JLabel(cardImage);
            playerPanel.add(cardLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        JPanel dealerPanel = new JPanel();
        dealerPanel.setLayout(new BoxLayout(dealerPanel, BoxLayout.Y_AXIS));
        JLabel dealerLabel = new JLabel("Cartes du croupier :");
        dealerPanel.add(dealerLabel);
        for (Card card : dealerCards) {
            ImageIcon cardImage = new ImageIcon(getScaledImage(card.getImagePath(), 80, 120));
            JLabel cardLabel = new JLabel(cardImage);
            dealerPanel.add(cardLabel);
            dealerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        gamePanel.add(playerPanel);
        gamePanel.add(Box.createHorizontalStrut(50));
        gamePanel.add(dealerPanel);

        add(statusLabel);
        add(gamePanel);

        JButton hitButton = new JButton("Tirer");
        JButton standButton = new JButton("Rester");
        JButton restartButton = new JButton("Recommencer");

        hitButton.addActionListener(e -> hit());
        standButton.addActionListener(e -> stand());
        restartButton.addActionListener(e -> initializeGame());

        add(hitButton);
        add(standButton);
        add(restartButton);

        int playerScore = calculateScore(playerCards);
        int dealerScore = calculateScore(dealerCards);
        statusLabel.setText("Joueur : " + playerScore + " | Croupier : " + dealerScore);

        revalidate();
        repaint();
    }

    private Image getScaledImage(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        return originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackJack().setVisible(true));
    }

    static class Card {
        private String rank;
        private String suit;
        private String imagePath;

        public Card(String rank, String suit) {
            this.rank = rank;
            this.suit = suit;
            this.imagePath = "bin/image/" + rank + "_of_" + suit + ".png";
        }

        public String getRank() {
            return rank;
        }

        public String getSuit() {
            return suit;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    static class Deck {
        private ArrayList<Card> cards;

        public Deck() {
            cards = new ArrayList<>();
            String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};
            String[] suits = {"clubs", "spades", "hearts", "diamonds"};

            for (String rank : ranks) {
                for (String suit : suits) {
                    cards.add(new Card(rank, suit));
                }
            }

            Collections.shuffle(cards);
        }

        public Card dealCard() {
            return cards.remove(0);
        }
    }
}
