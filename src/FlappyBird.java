import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 640;

    private JFrame frame;
    private JPanel gamePanel;

    private int birdY = 300, birdVelocity = 0;
    private final int GRAVITY = 1, JUMP = -12;
    private final int BIRD_SIZE = 40;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    private ArrayList<Rectangle> pipes;
    private ArrayList<Rectangle> coins;
    private Timer timer;
    private int score = 0;
    private static int bestScore = 0;
    private int gameSpeed = 5;
    private Random rand = new Random();

    private Image capybaraUpImage;
    private Image capybaraDownImage;
    private Image coinImage;
    private Image pipeTopImage;
    private Image pipeBottomImage;
    private Image backgroundImage;
    private Image background100Image;
    private Image background200Image;

    public FlappyBird() {
        frame = new JFrame("Flappy Bird");

        gamePanel = new GamePanel();

        frame.setSize(GAME_WIDTH, GAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setResizable(false);
        frame.setVisible(true);

        // Load best score from file when game starts
        loadBestScore();

        // Try to load saved game state
        loadGameState();
    }

    private class GamePanel extends JPanel implements ActionListener, KeyListener {
        public GamePanel() {
            pipes = new ArrayList<>();
            coins = new ArrayList<>();

            timer = new Timer(20, this);
            timer.start();

            frame.addKeyListener(this);
            frame.setFocusable(true); 

            capybaraUpImage = new ImageIcon(getClass().getResource("/image/capybara_wing_up.png")).getImage();
            capybaraDownImage = new ImageIcon(getClass().getResource("/image/capybara_wing_down.png")).getImage();
            capybaraUpImage = capybaraUpImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);
            capybaraDownImage = capybaraDownImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);

            pipeTopImage = new ImageIcon(getClass().getResource("/image/pipe_top.png")).getImage();
            pipeBottomImage = new ImageIcon(getClass().getResource("/image/pipe_bottom.png")).getImage();
            coinImage = new ImageIcon(getClass().getResource("/image/coin.png")).getImage();
            backgroundImage = new ImageIcon(getClass().getResource("/image/background.png")).getImage();
            background100Image = new ImageIcon(getClass().getResource("/image/background_100.png")).getImage();
            background200Image = new ImageIcon(getClass().getResource("/image/background_200.png")).getImage();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (score >= 300) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); 
            } else if (score >= 200) {
                g.drawImage(background200Image, 0, 0, getWidth(), getHeight(), this);
            } else if (score >= 100) {
                g.drawImage(background100Image, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); 
            }

            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 500, new Color(255, 165, 0), 0, 600, new Color(255, 223, 0));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 500, getWidth(), 100);

            if (birdVelocity < 0) {
                g.drawImage(capybaraUpImage, 100, birdY, this);
            } else {
                g.drawImage(capybaraDownImage, 100, birdY, this);
            }

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0) {
                    g.drawImage(pipeTopImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
                } else {
                    g.drawImage(pipeBottomImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
                }
            }

            for (Rectangle coin : coins) {
                g.drawImage(coinImage, coin.x, coin.y, 30, 30, this);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Score: " + score, 10, 30);
            g.drawString("Best: " + bestScore, 10, 60);
            g.drawString("Speed: " + gameSpeed, 10, 90);
            if (!gameStarted) {
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString("Cliquer sur ESPACE pour jouer", 250, 100);
                g.drawString("Cliquer sur ESC pour retourner au Launcher", 250, 115); 
                g.drawString("Vous pouvez ajuster la vitesse avec les fleches HAUT/BAS(1-15)", 250, 130); 
            }

            if (gameOver) {
                String gameOverText = "Game Over! Press SPACE to Restart";
                FontMetrics metrics = g.getFontMetrics(new Font("Arial", Font.BOLD, 30));
                int textWidth = metrics.stringWidth(gameOverText);
                int x = (getWidth() - textWidth) / 2;
                int y = 300;

                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString(gameOverText, x, y);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameStarted && !gameOver) {
                birdVelocity += GRAVITY;
                birdY += birdVelocity;

                ArrayList<Rectangle> toRemove = new ArrayList<>();
                for (Rectangle pipe : pipes) {
                    pipe.x -= gameSpeed;
                    if (pipe.x + pipe.width < 0) toRemove.add(pipe);

                    if (pipe.intersects(new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE))) {
                        gameOver = true;
                        if (score > bestScore) bestScore = score;
                    }
                }

                pipes.removeAll(toRemove);

                if (pipes.size() < 4) addPipe(false);

                for (Rectangle coin : coins) {
                    coin.x -= gameSpeed;
                }

                ArrayList<Rectangle> coinsToRemove = new ArrayList<>();
                for (Rectangle coin : coins) {
                    if (new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE).intersects(coin)) {
                        score++;
                        coinsToRemove.add(coin);
                    }
                }
                coins.removeAll(coinsToRemove);

                if (birdY > 500 || birdY < 0) {
                    gameOver = true;
                    if (score > bestScore) bestScore = score;
                }
            }

            repaint();
        }

        private void addPipe(boolean start) {
            int space = 150;
            int width = 80;

            int minHeight = 100;
            int maxHeight = 300;

            int prevHeight = pipes.size() > 0 ? pipes.get(pipes.size() - 1).height : 200;

            int variation = 40;
            int height = prevHeight + rand.nextInt(variation * 2 + 1) - variation;
            height = Math.max(minHeight, Math.min(maxHeight, height));

            int x = start ? 800 + pipes.size() * 300 : pipes.get(pipes.size() - 1).x + 300;

            pipes.add(new Rectangle(x, 0, width, height));
            pipes.add(new Rectangle(x, height + space, width, 600 - height - space));

            int coinY = height + rand.nextInt(space - 50) + 30;
            addCoin(x + width, coinY);
        }

        private void addCoin(int x, int y) {
            coins.add(new Rectangle(x, y, 30, 30));
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                frame.dispose();
                Launcher.main(null); 
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (gameOver) {
                    birdY = 300;
                    birdVelocity = 0;
                    score = 0;
                    pipes.clear();
                    coins.clear();
                    addPipe(true);
                    addPipe(true);
                    gameOver = false;
                    gameStarted = false;
                } else if (!gameStarted) {
                    gameStarted = true;
                    birdVelocity = JUMP;
                    pipes.clear();
                    coins.clear();
                    addPipe(true);
                    addPipe(true);
                } else {
                    birdVelocity = JUMP;
                }
            }

            if (!gameStarted) {
                if (e.getKeyCode() == KeyEvent.VK_UP && gameSpeed < 15) {
                    gameSpeed++;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN && gameSpeed > 1) {
                    gameSpeed--;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyTyped(KeyEvent e) {}
    }

    private void loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("best_score.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                bestScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            bestScore = 0; // Default score if file does not exist or is invalid
        }
    }

    @SuppressWarnings("unchecked")
    private void loadGameState() {
        File gameStateFile = new File("game_state.ser");
        if (gameStateFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(gameStateFile))) {
                pipes = (ArrayList<Rectangle>) in.readObject();
                coins = (ArrayList<Rectangle>) in.readObject();
                score = in.readInt();
                gameSpeed = in.readInt();  // Optionally load saved game speed
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // Si le fichier n'existe pas, on commence avec un jeu sans sauvegarde
            pipes = new ArrayList<>();
            coins = new ArrayList<>();
            score = 0;
            gameSpeed = 8;  // Valeur par défaut de la vitesse du jeu
        }
    }


    private void saveGameState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("game_state.ser"))) {
            out.writeObject(pipes);
            out.writeObject(coins);
            out.writeInt(score);
            out.writeInt(gameSpeed);  // Optionally save game speed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBestScore() {
        if (score > bestScore) {
            bestScore = score;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("best_score.txt"))) {
                writer.write(String.valueOf(bestScore));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new FlappyBird();
    }
}
