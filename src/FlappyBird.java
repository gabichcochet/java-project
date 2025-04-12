import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private int birdY = 300, birdVelocity = 0;
    private final int GRAVITY = 1, JUMP = -12;
    private final int BIRD_SIZE = 40;  // Adjusted size for the image
    private boolean gameOver = false;
    private boolean gameStarted = false;

    private ArrayList<Rectangle> pipes;
    private ArrayList<Rectangle> coins; // List to hold coins
    private Timer timer;
    private int score = 0;
    private static int bestScore = 0;
    private int gameSpeed = 5; 
    private Random rand = new Random();

    private Image capybaraUpImage; // The image of the capybara with wings up
    private Image capybaraDownImage; // The image of the capybara with wings down
    private Image coinImage; // The image for the coin
    
    private Image pipeTopImage; // The image of the top part of the pipe
    private Image pipeBottomImage; // The image of the bottom part of the pipe
    
    private Image backgroundImage; // The image for the background

    public FlappyBird() {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(800, 640); 
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        pipes = new ArrayList<>();
        coins = new ArrayList<>(); // Initialize the coins list

        timer = new Timer(20, this);
        timer.start();

        frame.addKeyListener(this);

        // Load both capybara images (with wings up and wings down)
        capybaraUpImage = new ImageIcon(getClass().getResource("/image/capybara_wing_up.png")).getImage();  
        capybaraDownImage = new ImageIcon(getClass().getResource("/image/capybara_wing_down.png")).getImage();  

        // Resize the images to match the size of the bird
        capybaraUpImage = capybaraUpImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);
        capybaraDownImage = capybaraDownImage.getScaledInstance(BIRD_SIZE, BIRD_SIZE, Image.SCALE_SMOOTH);

        // Load pipe images (ensure they have transparent backgrounds)
        pipeTopImage = new ImageIcon(getClass().getResource("/image/pipe_top.png")).getImage();
        pipeBottomImage = new ImageIcon(getClass().getResource("/image/pipe_bottom.png")).getImage();

        // Load coin image (ensure it has transparent background)
        coinImage = new ImageIcon(getClass().getResource("/image/coin.png")).getImage();

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource("/image/background.png")).getImage(); // PNG image
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw the ground and grass over the background
        g.setColor(Color.orange);
        g.fillRect(0, 500, 800, 100); // Ground

        g.setColor(Color.green);
        g.fillRect(0, 490, 800, 10); // Grass on the ground

        // Draw the capybara
        if (birdVelocity < 0) { 
            g.drawImage(capybaraUpImage, 100, birdY, this); // Draw wings-up image
        } else if (birdVelocity > 0) { 
            g.drawImage(capybaraDownImage, 100, birdY, this); // Draw wings-down image
        }

        // Drawing pipes with Mario-style look
        for (Rectangle pipe : pipes) {
            if (pipe.y == 0) {
                // Draw the top part of the pipe (use the loaded image)
                g.drawImage(pipeTopImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
            } else {
                // Draw the bottom part of the pipe (use the loaded image)
                g.drawImage(pipeBottomImage, pipe.x, pipe.y, pipe.width, pipe.height, this);
            }
        }

        // Draw coins (the coins move with the pipes)
        for (Rectangle coin : coins) {
            g.drawImage(coinImage, coin.x, coin.y, 30, 30, this); // Size the coin image to fit
        }

        // Draw the score
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

            // Calculate the width of the "Game Over" text to center it
            int textWidth = metrics.stringWidth(gameOverText);

            // Calculate the X position to center the text
            int x = (getWidth() - textWidth) / 2;

            // Adjust Y position so it's in the center vertically
            int y = 300;

            // Draw centered "Game Over" text
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(gameOverText, x, y);

            // Adjust the Y position for the smaller restart prompt text
            String restartPrompt = "Cliquer sur ESPACE pour rejouer";
            int restartTextWidth = metrics.stringWidth(restartPrompt);
            int restartX = (getWidth() - restartTextWidth) / 2;
            int restartY = y + 40;

            // Draw centered restart prompt
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
                pipe.x -= gameSpeed; // Move the pipes leftward
                if (pipe.x + pipe.width < 0) toRemove.add(pipe);

                if (pipe.intersects(new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    gameOver = true;
                    if (score > bestScore) bestScore = score;
                }
            }

            pipes.removeAll(toRemove);

            if (pipes.size() < 4) addPipe(false);

            // Move the coins with the pipes
            for (Rectangle coin : coins) {
                coin.x -= gameSpeed; // Move coins leftward as well
            }

            // Check if the bird collects a coin
            ArrayList<Rectangle> coinsToRemove = new ArrayList<>();
            for (Rectangle coin : coins) {
                if (new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE).intersects(coin)) {
                    score++; // Increment score when coin is collected
                    coinsToRemove.add(coin); // Remove the collected coin
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

        int x;
        if (start) {
            x = 800 + pipes.size() * 300;
        } else {
            x = pipes.get(pipes.size() - 1).x + 300;
        }

        pipes.add(new Rectangle(x, 0, width, height));
        pipes.add(new Rectangle(x, height + space, width, 600 - height - space));

        // Add coins with a gap from the pipes (adjust the Y range)
        int coinY = height + rand.nextInt(space - 50) + 30; // Ensure coin is not too close to the pipes
        addCoin(x + width, coinY); // Add coin within safe zone between the pipes
    }

    private void addCoin(int x, int y) {
        coins.add(new Rectangle(x, y, 30, 30)); // Add a coin with a fixed size
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                birdY = 300;
                birdVelocity = 0;
                score = 0;
                pipes.clear();
                coins.clear(); // Clear the coins as well
                addPipe(true);
                addPipe(true);
                gameOver = false;
                gameStarted = false;
            } else if (!gameStarted) {
                gameStarted = true;
                birdVelocity = JUMP;
                pipes.clear();
                coins.clear(); // Clear the coins when the game starts
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
