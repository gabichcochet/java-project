import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
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
    
    private Image alternateBackgroundImage;

    public FlappyBird() {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(800, 640); 
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        pipes = new ArrayList<>();
        coins = new ArrayList<>();

        timer = new Timer(20, this);
        timer.start();

        frame.addKeyListener(this);

        capybaraUpImage = new ImageIcon(getClass().getResource("/image/capybara_wing_up.png")).getImage();  
        capybaraDownImage = new ImageIcon(getClass().getResource("/image/capybara_wing_down.png")).getImage();  

        capybaraUpImage = capybaraUpImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);
        capybaraDownImage = capybaraDownImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);

        pipeTopImage = new ImageIcon(getClass().getResource("/image/pipe_top.png")).getImage();
        pipeBottomImage = new ImageIcon(getClass().getResource("/image/pipe_bottom.png")).getImage();

        coinImage = new ImageIcon(getClass().getResource("/image/coin.png")).getImage();

        backgroundImage = new ImageIcon(getClass().getResource("/image/background.png")).getImage();
        alternateBackgroundImage = new ImageIcon(getClass().getResource("/image/background_alt.png")).getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        if (score >= 10) {
            g.drawImage(alternateBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // ✅ Gradient ground
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 500, new Color(255, 165, 0), 0, 600, new Color(255, 223, 0));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 500, getWidth(), 100);

        // ✅ Dark green base grass
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 490, getWidth(), 10);

        // ✅ Light green grass blades
        g.setColor(new Color(144, 238, 144));
        for (int i = 0; i < getWidth(); i += 20) {
            g.fillOval(i, 485, 10, 10);
        }

        // ✅ Add decorative flowers
        Color[] flowerColors = {Color.red, Color.pink, Color.magenta, Color.yellow, Color.white};
        for (int i = 0; i < getWidth(); i += 80) {
            int flowerX = i + rand.nextInt(30);
            int flowerY = 500 + rand.nextInt(20);
            g.setColor(flowerColors[rand.nextInt(flowerColors.length)]);
            g.fillOval(flowerX, flowerY, 8, 8);
        }

        // Bird
        if (birdVelocity < 0) {
            g.drawImage(capybaraUpImage, 100, birdY, this);
        } else {
            g.drawImage(capybaraDownImage, 100, birdY, this);
        }

        // Pipes
        for (Rectangle pipe : pipes) {
            if (pipe.y == 0) {
                g.drawImage(pipeTopImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
            } else {
                g.drawImage(pipeBottomImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
            }
        }

        // Coins
        for (Rectangle coin : coins) {
            g.drawImage(coinImage, coin.x, coin.y, 30, 30, this);
        }

        // HUD
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Best: " + bestScore, 10, 60);
        g.drawString("Speed: " + gameSpeed, 10, 90);

        if (!gameStarted) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Cliquer SPACE pour jouer", 250, 250);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("utiliser les fleches HAUT/BAS pour changer la vitesse du jeu", 250, 280);
        }

        if (gameOver) {
            String gameOverText = "Game Over! Cliquer sur ESPACE pour rejouer";
            FontMetrics metrics = g.getFontMetrics(new Font("Arial", Font.BOLD, 30));
            int textWidth = metrics.stringWidth(gameOverText);
            int x = (getWidth() - textWidth) / 2;
            int y = 300;

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(gameOverText, x, y);

            String restartPrompt = "Cliquer sur ESPACE pour rejouer";
            int restartTextWidth = metrics.stringWidth(restartPrompt);
            int restartX = (getWidth() - restartTextWidth) / 2;
            int restartY = y + 40;

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString(restartPrompt, restartX, restartY);
        }
    }

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
        int prevHeight = pipes.size() >= 2 ? pipes.get(pipes.size() - 2).height : 200;
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

    public void keyPressed(KeyEvent e) {
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
        if (e.getKeyCode() == KeyEvent.VK_UP && !gameStarted && !gameOver) {
            gameSpeed = Math.min(gameSpeed + 1, 15);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && !gameStarted && !gameOver) {
            gameSpeed = Math.max(gameSpeed - 1, 1);
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FlappyBird();
    }
}
