import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack extends JFrame {
    private class Carte {
        String valeur;
        String type;

        Carte(String valeur, String type) {
            this.valeur = valeur;
            this.type = type;
        }

        public String toString() {
            return valeur + "-" + type;
        }

        public int getValeur() {
            if ("AJQK".contains(valeur)) {
                if (valeur.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(valeur);
        }

        public boolean estAs() {
            return valeur.equals("A");
        }

        public String getImagePath() {
            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Carte> deck;
    Random random = new Random();

    Carte carteCachee;
    ArrayList<Carte> mainCroupier;
    int sommeCroupier;
    int nbAsCroupier;

    ArrayList<Carte> mainJoueur;
    int sommeJoueur;
    int nbAsJoueur;

    int mise = 0;
    int solde = 1000;

    int largeur = 1000;
    int hauteur = 700;

    int carteLargeur = 110;
    int carteHauteur = 154;

    JPanel panneauJeu = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Image imgCarteCachee = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!boutonRester.isEnabled()) {
                    imgCarteCachee = new ImageIcon(getClass().getResource(carteCachee.getImagePath())).getImage();
                }
                g.drawImage(imgCarteCachee, 20, 20, carteLargeur, carteHauteur, null);

                for (int i = 0; i < mainCroupier.size(); i++) {
                    Carte carte = mainCroupier.get(i);
                    Image imgCarte = new ImageIcon(getClass().getResource(carte.getImagePath())).getImage();
                    g.drawImage(imgCarte, carteLargeur + 25 + (carteLargeur + 5) * i, 20, carteLargeur, carteHauteur, null);
                }

                for (int i = 0; i < mainJoueur.size(); i++) {
                    Carte carte = mainJoueur.get(i);
                    Image imgCarte = new ImageIcon(getClass().getResource(carte.getImagePath())).getImage();
                    g.drawImage(imgCarte, 20 + (carteLargeur + 5) * i, 320, carteLargeur, carteHauteur, null);
                }

                if (!boutonRester.isEnabled()) {
                    sommeCroupier = reduireAsCroupier();
                    sommeJoueur = reduireAsJoueur();

                    String message = "";
                    if (sommeJoueur > 21) {
                        message = "Vous avez perdu !";
                        solde -= mise;
                    } else if (sommeCroupier > 21) {
                        message = "Vous avez gagner !";
                        solde += mise;
                    } else if (sommeJoueur == sommeCroupier) {
                        message = "Egalite !";
                    } else if (sommeJoueur > sommeCroupier) {
                        message = "Vous avez gagner !";
                        solde += mise;
                    } else {
                        message = "Vous avez perdu !";
                        solde -= mise;
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                    g.drawString("Solde : " + solde + " euros", 220, 290);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    JPanel panneauBoutons = new JPanel();
    JButton boutonTirer = new JButton("Tirer");
    JButton boutonRester = new JButton("Rester");
    JButton boutonRejouer = new JButton("Rejouer");

    BlackJack() {
        setTitle("Black Jack");
        setSize(largeur, hauteur);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panneauJeu.setLayout(new BorderLayout());
        panneauJeu.setBackground(new Color(53, 101, 77));
        add(panneauJeu);

        boutonTirer.setFocusable(false);
        panneauBoutons.add(boutonTirer);
        boutonRester.setFocusable(false);
        panneauBoutons.add(boutonRester);
        boutonRejouer.setFocusable(false);
        panneauBoutons.add(boutonRejouer);
        add(panneauBoutons, BorderLayout.SOUTH);

        boutonTirer.addActionListener(e -> {
            Carte carte = deck.remove(deck.size() - 1);
            sommeJoueur += carte.getValeur();
            nbAsJoueur += carte.estAs() ? 1 : 0;
            mainJoueur.add(carte);
            if (reduireAsJoueur() > 21) {
                boutonTirer.setEnabled(false);
            }
            panneauJeu.repaint();
        });

        boutonRester.addActionListener(e -> {
            boutonTirer.setEnabled(false);
            boutonRester.setEnabled(false);
            while (sommeCroupier < 17) {
                Carte carte = deck.remove(deck.size() - 1);
                sommeCroupier += carte.getValeur();
                nbAsCroupier += carte.estAs() ? 1 : 0;
                mainCroupier.add(carte);
            }
            panneauJeu.repaint();
        });

        boutonRejouer.addActionListener(e -> redemarrerJeu());

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                    Launcher.main(null);
                }
            }
        });

        setFocusable(true);
        redemarrerJeu();
        setVisible(true);
    }

    public void redemarrerJeu() {
        if (solde <= 0) {
            JOptionPane.showMessageDialog(this, "Vous n'avez plus d'argent", "Fin de jeu", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        String input = JOptionPane.showInputDialog(this, "Entrez votre mise (solde : " + solde + ")", "Nouvelle Mise", JOptionPane.PLAIN_MESSAGE);
        try {
            mise = Integer.parseInt(input);
            if (mise <= 0 || mise > solde) {
                JOptionPane.showMessageDialog(this, "Montant de mise invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                redemarrerJeu();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Montant de mise invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            redemarrerJeu();
            return;
        }

        construireDeck();
        melangerDeck();

        mainCroupier = new ArrayList<>();
        sommeCroupier = 0;
        nbAsCroupier = 0;

        carteCachee = deck.remove(deck.size() - 1);
        sommeCroupier += carteCachee.getValeur();
        nbAsCroupier += carteCachee.estAs() ? 1 : 0;

        Carte carte = deck.remove(deck.size() - 1);
        sommeCroupier += carte.getValeur();
        nbAsCroupier += carte.estAs() ? 1 : 0;
        mainCroupier.add(carte);

        mainJoueur = new ArrayList<>();
        sommeJoueur = 0;
        nbAsJoueur = 0;

        for (int i = 0; i < 2; i++) {
            carte = deck.remove(deck.size() - 1);
            sommeJoueur += carte.getValeur();
            nbAsJoueur += carte.estAs() ? 1 : 0;
            mainJoueur.add(carte);
        }

        boutonTirer.setEnabled(true);
        boutonRester.setEnabled(true);
        panneauJeu.repaint();
    }

    public void construireDeck() {
        deck = new ArrayList<>();
        String[] valeurs = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};
        for (String type : types) {
            for (String valeur : valeurs) {
                deck.add(new Carte(valeur, type));
            }
        }
    }

    public void melangerDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Carte temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    public int reduireAsJoueur() {
        while (sommeJoueur > 21 && nbAsJoueur > 0) {
            sommeJoueur -= 10;
            nbAsJoueur--;
        }
        return sommeJoueur;
    }

    public int reduireAsCroupier() {
        while (sommeCroupier > 21 && nbAsCroupier > 0) {
            sommeCroupier -= 10;
            nbAsCroupier--;
        }
        return sommeCroupier;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackJack());
    }
} 