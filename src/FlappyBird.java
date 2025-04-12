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
    private Timer timer;
    private int score = 0;
    private static int bestScore = 0;
    private int gameSpeed = 5; 
    private Random rand = new Random();

    private Image capybaraUpImage; // The image of the capybara with wings up
    private Image capybaraDownImage; // The image of the capybara with wings down
    
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

        long currentTime = System.currentTimeMillis();

        // If jumping (velocity is negative)
        if (birdVelocity < 0) { 
            // Display the wings up image during jump
            g.drawImage(capybaraUpImage, 100, birdY, this); // Draw wings-up image
        } 
        // If falling (velocity is positive)
        else if (birdVelocity > 0) { 
            // Display wings-down image when falling
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
                pipe.x -= gameSpeed;
                if (pipe.x + pipe.width < 0) toRemove.add(pipe);

                if (pipe.intersects(new Rectangle(100, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    gameOver = true;
                    if (score > bestScore) bestScore = score;
                }
            }

            pipes.removeAll(toRemove);

            if (pipes.size() < 4) addPipe(false);

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0) { 
                    if (pipe.x + pipe.width < 100 && pipe.x + pipe.width + gameSpeed >= 100) {
                        score++;
                    }
                }
            }

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
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                birdY = 300;
                birdVelocity = 0;
                score = 0;
                pipes.clear();
                addPipe(true);
                addPipe(true);
                gameOver = false;
                gameStarted = false;
            } else if (!gameStarted) {
                gameStarted = true;
                birdVelocity = JUMP;
                pipes.clear();
                addPipe(true);
                addPipe(true);
            } else {
                birdVelocity = JUMP;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            gameSpeed = Math.min(gameSpeed + 1, 15);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            gameSpeed = Math.max(gameSpeed - 1, 1);
        }
    }

    public void keyReleased(KeyEvent e) {
        // Reset jumping state when spacebar is released
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // isJumping = false; // Optional, only if you need this variable
        }
    }

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FlappyBird();
    }
}
