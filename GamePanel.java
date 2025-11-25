import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private final int TILE_SIZE = 80;
    private final int WIDTH = 15;
    private final int HEIGHT = 8;
    private final int SCREEN_WIDTH = WIDTH * TILE_SIZE;
    private final int SCREEN_HEIGHT = HEIGHT * TILE_SIZE;
    private final int WATER_SPEED = 4;
    private final int VERTICAL_SPEED = 1;

    private Frog frog;
    private ArrayList<Rectangle> trees;
    private ArrayList<Rectangle> lands;
    private ArrayList<WaterRow> waterRows;

    private BufferedImage treeImg, landImg, waterImg, lilyPadImg, scorePlateImg, lotusImg, pauseImg, playNormal,quitNormal, playHover, quitHover, playImg, quitImg, playAgainNormal, playAgainHover, playAgainImg, quitImg2, fakeLilyPadImg;
    private Image crocImgLeft, crocImgRight, fakeLilyPad;
    private JPanel pausePanel;
    private JPanel gameOverPanel;
    private static Timer timer;
    
    private int score = 0;
    private int currentStep;
    private int scrollOffset = 0;
    private int lastDirection = 1;
    private boolean gameStart = false;
    private int num = 1;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean fakeLilyPadFound = false;
    private String musicFilePath = "music/mainMusic.wav";

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        setLayout(null);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        loadImage();
        if(!MusicManager.isMuted()){ //play music if its not muted
            MusicManager.playMusic(musicFilePath);
        }

        playImg = playNormal;
        playAgainImg = playAgainNormal;
        quitImg = quitNormal;
        quitImg2 = quitNormal;

        //PAUSE PANEL
        pausePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // to draw the images
                g2.setBackground(Color.black);
                g2.drawImage(playImg, 470, 175, playNormal.getWidth(), playNormal.getHeight(), this);
                g2.drawImage(quitImg, 470, 300, quitNormal.getWidth(), quitNormal.getHeight(), this);
            }
        };
        pausePanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        pausePanel.setOpaque(false);
        pausePanel.setVisible(false);
        pausePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) { //for hover effect
                if (e.getX() >= 470 && e.getX() <= 470 + playNormal.getWidth() &&
                        e.getY() >= 175 && e.getY() <= 175 + playNormal.getHeight()) {
                    playImg = playHover;
                } else {
                    playImg = playNormal;
                }

                if (e.getX() >= 470 && e.getX() <= 470 + quitNormal.getWidth() &&
                        e.getY() >= 300 && e.getY() <= 300 + quitNormal.getHeight()) {
                    quitImg = quitHover;
                } else {
                    quitImg = quitNormal;
                }

                repaint();
            }
        });
        pausePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //start the game (continue)
                if (e.getX() >= 470 && e.getX() <= 470 + playNormal.getWidth() && e.getY() >= 175
                        && e.getY() <= 175 + playNormal.getHeight()) {
                    isPaused = false;
                    timer.start();
                    num = 2;
                    pausePanel.setVisible(false);
                    loadImage();
                //quit button
                } else if (e.getX() >= 470 && e.getX() <= 470 + quitNormal.getWidth() && e.getY() >= 300
                        && e.getY() <= 300 + quitNormal.getHeight()) {
                    MainFrame mf = new MainFrame();
                    mf.setVisible(true);
                    SwingUtilities.getWindowAncestor(GamePanel.this).dispose();

                }
            }
        });
        add(pausePanel);

        //GAME OVER PANEL
        gameOverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // to draw the images
                g2.setColor(Color.decode("#ff0000"));
                g2.setFont(MainFrame.loadFont("/fonts/AeogoBox-yYmGe.ttf", 130f));
                g2.drawString("GAME OVER", 132, 220);

                g2.setFont(MainFrame.loadFont("/fonts/PixeloidSansBold-OG894.ttf", 30f));
                g2.drawString("Best Score: " + MainFrame.getBestScore(), 350, 280);
                g2.drawString("Score: " + score, 450, 320);

                g2.drawImage(playAgainImg, 330, 350, playAgainImg.getWidth(), playAgainImg.getHeight(), this);
                g2.drawImage(quitImg2, 620, 350, quitImg2.getWidth(), quitImg2.getHeight(), this);
            }
        };
        gameOverPanel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameOverPanel.setOpaque(false);
        gameOverPanel.setVisible(false);
        gameOverPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) { //for hover effect
                if (e.getX() >= 330 && e.getX() <= 330 + playAgainImg.getWidth() &&
                        e.getY() >= 350 && e.getY() <= 350 + playAgainImg.getHeight())
                    playAgainImg = playAgainHover;
                else
                    playAgainImg = playAgainNormal;

                if (e.getX() >= 620 && e.getX() <= 620 + quitImg2.getWidth() &&
                        e.getY() >= 350 && e.getY() <= 350 + quitImg2.getHeight())
                    quitImg2 = quitHover;
                else
                    quitImg2 = quitNormal;
                repaint();
            }
        });
        gameOverPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //play again button
                if (e.getX() >= 330 && e.getX() <= 330 + playAgainImg.getWidth() && e.getY() >= 350
                        && e.getY() <= 350 + playAgainImg.getHeight()) {
                    startGame();
                //quit button (back to mainframe)
                } else if (e.getX() >= 620 && e.getX() <= 620 + quitNormal.getWidth() && e.getY() >= 350
                        && e.getY() <= 350 + quitNormal.getHeight()) {
                    MainFrame mf = new MainFrame();
                    mf.setVisible(true);
                    SwingUtilities.getWindowAncestor(GamePanel.this).dispose();
                }
            }
        });
        add(gameOverPanel);

        startGame(); // start game
    }

    private void startGame() {
        trees = new ArrayList<>(); //initialize array
        lands = new ArrayList<>();
        waterRows = new ArrayList<>();

        score = 0; //re-initialize these variable for when the player wants to play again
        currentStep = -2;
        gameOverPanel.setVisible(false);
        gameStart = false;
        isPaused = false;
        isGameOver = false;
        fakeLilyPadFound = false;
        scrollOffset = 0;
        lastDirection = 1;

        if(!MusicManager.isMuted()) //play game over music when it is not muted
            MusicManager.playMusic(musicFilePath);

        frog = new Frog(TILE_SIZE * 7, TILE_SIZE * 7); //place the frog in the middle of the land

        // --- Land area display/map (bottom) ---
        int[][] landMap = {
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1 },
        };

        for (int row = 0; row < landMap.length; row++) {
            for (int col = 0; col < landMap[row].length; col++) {
                int y = SCREEN_HEIGHT - (landMap.length - row) * TILE_SIZE; // to calculate the y position (bottom to top)
                int x = col * TILE_SIZE; // to calculate the x position
                if (landMap[row][col] == 1)
                    trees.add(new Rectangle(x, y, TILE_SIZE, TILE_SIZE));
                else
                    lands.add(new Rectangle(x, y, TILE_SIZE, TILE_SIZE));
            }
        }

        // --- Water area display/map (top) ---
        int rowsToCreate = HEIGHT - landMap.length + 1; //1 for extra row
        for (int i = 0; i < rowsToCreate; i++) { //to draw the initial first rows of water
            int y = (SCREEN_HEIGHT - (landMap.length + i) * TILE_SIZE) - TILE_SIZE; // to calculate the y position of each row
            addWaterRow(y);
        }

        // Animate
        timer = new Timer(100, e -> updateWater());
    }

    // to add water rows in the array list
    private void addWaterRow(int yPosition) {
        int cols = WIDTH + 1;
        int[] pattern = new int[cols];
        int num = 0;

        if (score >= 0 && score <= 5) // stage 1 lilypad tile, lotus tile, and water tile
            num = 3;
        else if (score >= 6 && score <= 10) // stage 2 add fake lily pad tile
            num = 4;
        else if (score > 10) // stage 3 add crocodile tile
            num = 5;

        for (int c = 0; c < cols; c++) {
            int val = (int) (Math.random() * num); // to generate random tile 0 = lily pad tile, 1 = lotus flower tile,
                                                   // 2 = water tile, 3 = fake lilypad 4 = crocodile tile
            if (val == 4) { // crocodile tile occupies 2 tiles
                pattern[c] = 4;
                pattern[c + 1] = 4;
                c++;
            } else if (c == cols - 1) { //if its the last column it is water tile automatically
                pattern[c] = 2;
            } else
                pattern[c] = val;
        }
        int dir = -lastDirection; // to alternate the direction of the water rows //invert the value of lastDirection variable negative (to left) odd no. of rows, positive (to right) even no. of rows
        lastDirection = dir;
        waterRows.add(new WaterRow(pattern, yPosition, dir)); // add it to the arraylist
    }

    // to animate the panel
    private void updateWater() {
        for (WaterRow wr : waterRows) {
            wr.offset += wr.dir * WATER_SPEED; //horizontal movement
            wr.offset %= (SCREEN_WIDTH + 80); // so that offset loops back within screen width
            if (wr.offset < 0) // to ensure that it stays in a positive range
                wr.offset += (SCREEN_WIDTH + 80);

            wr.y += VERTICAL_SPEED; // moves the row downwards
        }

        // to move the trees and lands downwards
        for (Rectangle t : trees)
            t.y += VERTICAL_SPEED;
        for (Rectangle l : lands)
            l.y += VERTICAL_SPEED;

        // to remove rows that are out of the screen (lambda)
        waterRows.removeIf(waterTile -> waterTile.y > SCREEN_HEIGHT);
        trees.removeIf(tree -> tree.y > SCREEN_HEIGHT);
        lands.removeIf(land -> land.y > SCREEN_HEIGHT);

        // to move the whole panel downwards and to add new water rows at the top whenone row disappear in the bottom
        scrollOffset += VERTICAL_SPEED;
        if (scrollOffset >= TILE_SIZE) {
            addWaterRow(-TILE_SIZE);
            scrollOffset = 0;
        }
        frog.scrollDown(VERTICAL_SPEED); // so that frog will scroll down as well when its in still position
        updateFrogOnWaterRow(); //call the method for frog
        repaint();
    }

    private void updateFrogOnWaterRow() {
        Rectangle frogRect = new Rectangle(frog.getX(), frog.getY(), TILE_SIZE, TILE_SIZE); //the size and position of the frog on the screen
        WaterRow attachedRow = null;
        Rectangle bestTile = null;
        int bestTileType = -1;
        int maxIntersectArea = 0; //how much the frog overlaps the tile

        // Loop through all water rows
        for (WaterRow wr : waterRows) { //loops through water rows
            for (int i = 0; i < wr.pattern.length; i++) { //loops through all tiles in the row
                for (int repeat = -1; repeat <= 1; repeat++) { //3 copies of the row for wrapping or for smooth animation just like in water rows
                    int shift = repeat * (SCREEN_WIDTH + 80);
                    int tileX = i * TILE_SIZE + wr.offset + shift; //the actual x posiiton of the tile on the screen

                    if (tileX + TILE_SIZE < 0 || tileX > SCREEN_WIDTH) //skip off screen tiles
                        continue;

                    Rectangle tileRect = new Rectangle(tileX, wr.y, TILE_SIZE, TILE_SIZE); //create rectangle for the tile

                    if (frogRect.intersects(tileRect)) { //if the frog overlaps with the tile
                        Rectangle intersection = frogRect.intersection(tileRect); //create a ractangle for that intersection
                        int intersectArea = intersection.width * intersection.height; //area of the intersection in pixel

                        if (intersectArea > maxIntersectArea) { //determine which tile the frog overlap the most
                            maxIntersectArea = intersectArea; //assignment of which tile the frog is on the most
                            bestTile = tileRect; //from the inner loop
                            bestTileType = wr.pattern[i]; //from the middle loop
                            attachedRow = wr; //from the outer loop
                        }
                    }
                }
            }
        }

        if (bestTile != null) { //check if the frog is on any tile
            if ((double) maxIntersectArea / (TILE_SIZE * TILE_SIZE) >= 0.6) { //to ensure that 60% of the frog is on a tile
                if (bestTileType == 0) { // safe
                    frog.attachToRow(attachedRow);
                    if (currentStep > score)
                        score++;
                } else if (bestTileType == 3) { //fake lilypad
                    fakeLilyPadFound = true;
                    frog.detachFromRow();
                    gameOver();
                } else { // dangerous
                    gameOver();
                }
            } else {
                gameOver();
            }
        }
        // out of screen validation (formula is to determine if half of the frog is off screen)
        if (frog.getX() <= (TILE_SIZE / -2) || frog.getX() >= SCREEN_WIDTH - (TILE_SIZE / 2) ||
                frog.getY() <= (TILE_SIZE / -2) || frog.getY() >= SCREEN_HEIGHT - (TILE_SIZE / 2)) {
            gameOver();
        }
    }

    public void gameOver() {
        timer.stop(); //the screen stops moving
        if(!MusicManager.isMuted()) //play game over music when it is not muted
            MusicManager.playMusic("music/gameOver.wav");
        isGameOver = true; //set this to true for condition in keypress
        if (score > MainFrame.getBestScore()) //condition if the current score is greater than the best score
            MainFrame.setBestScore(score); //set new best score
        gameOverPanel.setVisible(true); //display the panel
    }

    //load image
    private void loadImage() {
        try {
            treeImg = ImageIO.read(getClass().getResource("/images/tree.png"));
            landImg = ImageIO.read(getClass().getResource("/images/land.png"));
            waterImg = ImageIO.read(getClass().getResource("/images/water.png"));
            lotusImg = ImageIO.read(getClass().getResource("/images/lotus.png"));
            lilyPadImg = ImageIO.read(getClass().getResource("/images/water_lilypad.png"));
            crocImgLeft = new ImageIcon(getClass().getResource("/images/crocodileLeft.gif")).getImage();
            crocImgRight = new ImageIcon(getClass().getResource("/images/crocodileRight.gif")).getImage();
            scorePlateImg = ImageIO.read(getClass().getResource("/images/score.png"));
            pauseImg = ImageIO.read(getClass().getResource("/images/pauseIcon" + num + ".png"));
            playNormal = ImageIO.read(getClass().getResource("/images/sPlay1.png"));
            playHover = ImageIO.read(getClass().getResource("/images/sPlay2.png"));
            playAgainNormal = ImageIO.read(getClass().getResource("/images/playAgain1.png"));
            playAgainHover = ImageIO.read(getClass().getResource("/images/playAgain2.png"));
            quitNormal = ImageIO.read(getClass().getResource("/images/sQuit1.png"));
            quitHover = ImageIO.read(getClass().getResource("/images/sQuit2.png"));
            fakeLilyPad = new ImageIcon(getClass().getResource("/images/crackLilypad.gif")).getImage();
            fakeLilyPadImg = ImageIO.read(getClass().getResource("/images/crackLilypad.png"));
        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    @Override //draw images in the main panel of the game
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.blue);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (WaterRow wr : waterRows) {
            for (int repeat = -1; repeat <= 1; repeat++) { // draws the row 3 times
                int shift = repeat * (SCREEN_WIDTH + 80); // shift is the x positions of the row
                for (int col = 0; col < WIDTH + 1; col++) { // looping through each tile in one row
                    int normalX = col * TILE_SIZE; // original x position of the tile
                    int finalX = normalX + wr.offset + shift; // final x position with offset and shift in all the copies of the row when animating

                    if (finalX + TILE_SIZE < -TILE_SIZE || finalX > SCREEN_WIDTH + TILE_SIZE) // if the tile is out of the screen skip it so that even if it has 3 copies of row those that need to be visible is the one that is draw
                        continue;

                    if (wr.pattern[col] == 0)
                        g.drawImage(lilyPadImg, finalX, wr.y, TILE_SIZE, TILE_SIZE, this);
                    else if (wr.pattern[col] == 1)
                        g.drawImage(lotusImg, finalX, wr.y, TILE_SIZE, TILE_SIZE, this);
                    else if (wr.pattern[col] == 2)
                        g.drawImage(waterImg, finalX, wr.y, TILE_SIZE, TILE_SIZE, this);
                    else if (wr.pattern[col] == 3) {
                        if (fakeLilyPadFound) { //condition that if the player steps on a fake lily the gif will play
                            g.drawImage(fakeLilyPad, finalX, wr.y, TILE_SIZE, TILE_SIZE, this);
                        } else {
                            g.drawImage(fakeLilyPadImg, finalX, wr.y, TILE_SIZE, TILE_SIZE, this);
                        }
                    } else {
                        if (wr.dir == -1) //for direction of the croc
                            g.drawImage(crocImgLeft, finalX, wr.y, TILE_SIZE * 2, TILE_SIZE, this);
                        else
                            g.drawImage(crocImgRight, finalX, wr.y, TILE_SIZE * 2, TILE_SIZE, this);
                        col++; // crocodile occupies two tiles
                    }
                }
            }
        }

        // draw lands and trees
        for (Rectangle land : lands)
            g.drawImage(landImg, land.x, land.y, TILE_SIZE, TILE_SIZE, this);
        for (Rectangle tree : trees)
            g.drawImage(treeImg, tree.x, tree.y, TILE_SIZE, TILE_SIZE, this);

        // score design
        g.drawImage(scorePlateImg, 1010, 8, scorePlateImg.getWidth(), scorePlateImg.getHeight(), this);
        g.setColor(Color.decode("#f5edd0"));
        g.setFont(MainFrame.loadFont("/fonts/PixeloidSansBold-OG894.ttf", 20f));
        g.drawString("Score: " + score, 1043, 55);
        g.drawImage(pauseImg, 5, 5, 65, 65, this);
        frog.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (!gameStart) { //start timer when the game start
            gameStart = true;
            timer.start();
        }

        if (isGameOver || isPaused) // so that the frog will not move when its game over or when its paused
            return;

        switch (code) {
            case KeyEvent.VK_UP:
                currentStep++;
                frog.setDirection("up");
                break;
            case KeyEvent.VK_DOWN:
                currentStep--;
                frog.setDirection("down");
                break;
            case KeyEvent.VK_LEFT:
                frog.setDirection("left");
                break;
            case KeyEvent.VK_RIGHT:
                frog.setDirection("right");
                break;
            default:
                break;
        }

        frog.setMoving(true); //method to determin if the frog is moving or not for the gif animation
        frog.move(trees); //method in frog class to move the frog
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        frog.setMoving(false); //to stop the GIF
        frog.detachFromRow();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Check if mouse is over the pause button area and when clicked the game will be pause
        if (e.getX() >= 5 && e.getX() <= 65 && e.getY() >= 5 && e.getY() <= 65) {
            isPaused = true;
            timer.stop();
            pausePanel.setVisible(true);
            num = 4;
            loadImage();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isPaused) { //for hover animation
            if (e.getX() >= 5 && e.getX() <= 65 && e.getY() >= 5 && e.getY() <= 65) {
                num = 4;
            }
        } else {
            if (e.getX() >= 5 && e.getX() <= 65 && e.getY() >= 5 && e.getY() <= 65) {
                num = 2;
            } else {
                num = 1;
            }
        }
        loadImage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GamePanel::new);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    //Water row class
    class WaterRow {
        int[] pattern; // array to store 0 = water tile, 1 = lily pad tile, and 2 = crocodile tile
        int y; // y position of the row
        int dir; // direction of the row
        int offset; // movement of the row of tile

        WaterRow(int[] pattern, int y, int dir) {
            this.pattern = pattern;
            this.y = y;
            this.dir = dir;

            // Start positions (-80 and SCREEN_WIDTH+80)
            if (dir > 0) {
                // moving right → start off the left
                this.offset = -80;
            } else {
                // moving left → start off the right
                this.offset = SCREEN_WIDTH;
            }
        }
    }
}