import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainFrame extends JFrame implements MouseListener {
    private Image backgroundImage;
    private BufferedImage scorePlateImg, playNormal, quitNormal, settingsNormal, playHover, quitHover, settingsHover, instructionsImg, backBtn, backHover, forwardBtn, forwardHover, musicBtn, musicHover, muteBtn, muteHover, instructionBtn, instructionHover;
    private ArrayList<FrameImage> images = new ArrayList<>();
    private JPanel settingsPanel, instructionsPanel;
    private static int bestScore = 0;
    private int score = 0;
    private int instrucNum = 1;
    private int settingsClickCount = 0;
    private int instructionsClickCount = 0;
    private int musicClickCount = 0;
    private String musicFilePath = "music/mainMusic.wav";

    public MainFrame() {
        setSize(1200, 640);
        setLayout(null);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Load all the images in the frame
        loadImage();
        if(!MusicManager.isMuted())
            MusicManager.playMusic(musicFilePath);

        BufferedImage[] normalImgs = { playNormal, quitNormal, settingsNormal, backBtn, musicBtn, instructionBtn,
                backBtn, backBtn, forwardBtn };
        BufferedImage[] hoverImgs = { playHover, quitHover, settingsHover, backHover, musicHover, instructionHover,
                backHover, backHover, forwardHover };

        if (score > bestScore) { // best score condition
            bestScore = score;
        }

        //add images to the array list
        images.add(new FrameImage(playNormal, 250, 440));
        images.add(new FrameImage(quitNormal, 570, 440));
        images.add(new FrameImage(settingsNormal, 8, 8));

        // custom fonts
        Font normalFont = loadFont("/fonts/PixeloidMono-aYe1R.ttf", 14f);
        Font scoreFont = loadFont("/fonts/PixeloidSansBold-OG894.ttf", 27f);
        Font header = loadFont("/fonts/AeogoBox-yYmGe.ttf", 110f);

        // panel to draw everything (Main Panel)
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // to draw the images
                g.drawImage(backgroundImage, -5, 0, getWidth() + 5, getHeight(), this);
                g.drawImage(scorePlateImg, 1005, 8, scorePlateImg.getWidth(), scorePlateImg.getHeight(), this);
                //for all the images in this panel (3 images from the array)
                for (int i = 0; i < 3; i++)
                    g.drawImage(images.get(i).img, images.get(i).x, images.get(i).y, images.get(i).img.getWidth(),
                            images.get(i).img.getHeight(), this);

                g.setColor(Color.decode("#ffffde"));
                g.setFont(normalFont);
                g.drawString("SETTINGS", 11, 102);
                g.drawString("BEST SCORE", 1050, 40);

                g.setFont(scoreFont);
                FontMetrics fm = g.getFontMetrics();
                int x = 1095 - fm.stringWidth(String.valueOf(bestScore)) / 2;
                g.drawString(String.valueOf(bestScore), x, 65);

                g.setFont(header);
                g.drawString("HOPSTACLE", 210, 220);
                g.drawString("COURSE", 340, 310);
            }
        };
        panel.setLayout(null);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (int i = 0; i < 3; i++) {
                    FrameImage img = images.get(i);
                    boolean hovering = e.getX() >= img.x && e.getX() <= img.x + img.img.getWidth() &&
                            e.getY() >= img.y && e.getY() <= img.y + img.img.getHeight();
                    if (hovering)
                        img.img = hoverImgs[i];
                    else
                        img.img = normalImgs[i];
                }
                repaint();
            }
        });

        //settings panel
        settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 250));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // to draw the images
                g2.setColor(Color.decode("#ffffde"));
                g2.setFont(loadFont("/fonts/PixeloidSans-E40en.ttf", 50f));
                g2.drawString("MUSIC", 480, 250);
                g2.drawString("INSTRUCTIONS", 480, 390);

                //for all images in this panel (images with index 3 to 5 in the array)
                for (int i = 3; i < 6; i++)
                    g2.drawImage(images.get(i).img, images.get(i).x, images.get(i).y, images.get(i).img.getWidth(),
                            images.get(i).img.getHeight(), this);
            }
        };
        settingsPanel.setBounds(0, 0, 1200, 640);
        settingsPanel.setLayout(null);
        settingsPanel.setOpaque(false);
        settingsPanel.setVisible(false);
        settingsPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (int i = 3; i < 6; i++) { //loop through all of the 3 images for hover effect
                    FrameImage img = images.get(i);
                    boolean hovering = e.getX() >= img.x && e.getX() <= img.x + img.img.getWidth() &&
                            e.getY() >= img.y && e.getY() <= img.y + img.img.getHeight();
                    if (hovering)
                        img.img = hoverImgs[i];
                    else
                        img.img = normalImgs[i];
                }
                repaint();
            }
        });
        settingsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // back button
                if (e.getX() >= images.get(3).x && e.getX() <= images.get(3).x + images.get(3).img.getWidth() &&
                    e.getY() >= images.get(3).y && e.getY() <= images.get(3).y + images.get(3).img.getHeight()) 
                        settingsPanel.setVisible(false);

                // music button
                else if (e.getX() >= images.get(4).x && e.getX() <= images.get(4).x + images.get(4).img.getWidth() &&
                    e.getY() >= images.get(4).y && e.getY() <= images.get(4).y + images.get(4).img.getHeight()) {
                        musicClickCount++;
                        if(musicClickCount % 2 != 0){
                            images.set(4, new FrameImage(muteHover, 350, 170));
                            normalImgs[4] = muteBtn;
                            hoverImgs[4] = muteHover;
                        } else{
                            images.set(4, new FrameImage(musicHover, 350, 170));
                            normalImgs[4] = musicBtn;
                            hoverImgs[4] = musicHover;
                        }
                        MusicManager.toggleMute();
                        repaint();

                // intruction button
                } else if (e.getX() >= images.get(5).x && e.getX() <= images.get(5).x + images.get(5).img.getWidth() && 
                    e.getY() >= images.get(5).y && e.getY() <= images.get(5).y + images.get(5).img.getHeight()) {
                        instructionsClickCount++;
                        if (instructionsClickCount == 1) { // add the aditional images needed in the array list
                            images.add(new FrameImage(backBtn, 8, 8));
                            images.add(new FrameImage(backBtn, 40, 520));
                            images.add(new FrameImage(forwardBtn, 1080, 520));
                        }
                        instructionsPanel.setVisible(true);
                        settingsPanel.setVisible(false);
                }
            }
        });

        // instructions panel
        instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 250));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // to draw the images
                g2.setColor(Color.decode("#ffffde"));
                g2.setFont(loadFont("/fonts/AeogoBox-yYmGe.ttf", 60f));
                g2.drawString("How to play?", 360, 130);

                g2.drawImage(instructionsImg, 0, 180, instructionsImg.getWidth(), instructionsImg.getHeight(), this);
                //for all the icons in this panel with index 6 to the end of the array
                for (int i = 6; i < images.size(); i++)
                    g.drawImage(images.get(i).img, images.get(i).x, images.get(i).y, images.get(i).img.getWidth(),images.get(i).img.getHeight(), this);
            }
        };
        instructionsPanel.setBounds(0, 0, 1200, 640);
        instructionsPanel.setOpaque(false);
        instructionsPanel.setVisible(false);
        instructionsPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (int i = 6; i < images.size(); i++) { //loop through all images from 6 to the last for hover effect
                    FrameImage img = images.get(i);
                    boolean hovering = e.getX() >= img.x && e.getX() <= img.x + img.img.getWidth() &&
                            e.getY() >= img.y && e.getY() <= img.y + img.img.getHeight();
                    if (instrucNum == 1 && hovering && img == images.get(7)
                            || instrucNum == 5 && hovering && img == images.get(8)) {
                        return;
                    }
                    if (hovering)
                        img.img = hoverImgs[i];
                    else
                        img.img = normalImgs[i];
                }
                repaint();
            }
        });
        instructionsPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //back to settings button
                if (e.getX() >= images.get(6).x && e.getX() <= images.get(6).x + images.get(6).img.getWidth() &&
                        e.getY() >= images.get(6).y && e.getY() <= images.get(6).y + images.get(6).img.getHeight()) {
                    instructionsPanel.setVisible(false);
                    settingsPanel.setVisible(true);
                //prev image button
                } else if (instrucNum > 1 && e.getX() >= images.get(7).x
                        && e.getX() <= images.get(7).x + images.get(7).img.getWidth() &&
                        e.getY() >= images.get(7).y && e.getY() <= images.get(7).y + images.get(7).img.getHeight()) {
                    instrucNum--;
                //next image button
                } else if (instrucNum < 5 && e.getX() >= images.get(8).x
                        && e.getX() <= images.get(8).x + images.get(8).img.getWidth() &&
                        e.getY() >= images.get(8).y && e.getY() <= images.get(8).y + images.get(8).img.getHeight()) {
                    instrucNum++;
                }
                loadImage();
            }
        });

        panel.add(instructionsPanel);
        panel.add(settingsPanel);
        setContentPane(panel);
    }

    //get Best Score Method
    public static int getBestScore() {
        return bestScore;
    }
    //set Best Score Method
    public static void setBestScore(int score) {
        bestScore = score;
    }

    //(Main) panel mouse click
    @Override
    public void mouseClicked(MouseEvent e) {
        // play button
        if (e.getX() >= images.get(0).x && e.getX() <= images.get(0).x + images.get(0).img.getWidth() &&
            e.getY() >= images.get(0).y && e.getY() <= images.get(0).y + images.get(0).img.getHeight()) {
                //Open Game Frame
                GameFrame.main(null);
                this.dispose();
        // quit button
        } else if (e.getX() >= images.get(1).x && e.getX() <= images.get(1).x + images.get(1).img.getWidth() &&
            e.getY() >= images.get(1).y && e.getY() <= images.get(1).y + images.get(1).img.getHeight()) 
                System.exit(0); // exit game
        // settings button
        else if (e.getX() >= images.get(2).x && e.getX() <= images.get(2).x + images.get(2).img.getWidth() &&
            e.getY() >= images.get(2).y && e.getY() <= images.get(2).y + images.get(2).img.getHeight()) {
                settingsClickCount++;
                if (settingsClickCount == 1) {
                    images.add(new FrameImage(backBtn, 8, 8));
                    images.add(new FrameImage(musicBtn, 350, 170));
                    images.add(new FrameImage(instructionBtn, 350, 310));
                }
                settingsPanel.setVisible(true);
        }
    }

    // loading images
    private void loadImage() {
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/background.gif")).getImage();
            scorePlateImg = ImageIO.read(getClass().getResource("/images/score.png"));
            playNormal = ImageIO.read(getClass().getResource("/images/play1.png"));
            playHover = ImageIO.read(getClass().getResource("/images/play2.png"));
            quitNormal = ImageIO.read(getClass().getResource("/images/quit1.png"));
            quitHover = ImageIO.read(getClass().getResource("/images/quit2.png"));
            settingsNormal = ImageIO.read(getClass().getResource("/images/settings1.png"));
            settingsHover = ImageIO.read(getClass().getResource("/images/settings2.png"));
            instructionsImg = ImageIO.read(getClass().getResource("/images/i" + instrucNum + ".png"));
            backBtn = ImageIO.read(getClass().getResource("/images/back1.png"));
            backHover = ImageIO.read(getClass().getResource("/images/back2.png"));
            musicBtn = ImageIO.read(getClass().getResource("/images/musicOn1.png"));
            musicHover = ImageIO.read(getClass().getResource("/images/musicOn2.png"));
            muteBtn = ImageIO.read(getClass().getResource("/images/musicMute1.png"));
            muteHover = ImageIO.read(getClass().getResource("/images/musicMute2.png"));
            instructionBtn = ImageIO.read(getClass().getResource("/images/instruc1.png"));
            instructionHover = ImageIO.read(getClass().getResource("/images/instruc2.png"));
            forwardBtn = ImageIO.read(getClass().getResource("/images/forward1.png"));
            forwardHover = ImageIO.read(getClass().getResource("/images/forward2.png"));
        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    // Custom font loader
    public static Font loadFont(String path, float size) {
        try {
            InputStream is = MainFrame.class.getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    class FrameImage {
        BufferedImage img;
        int x, y;

        FrameImage(BufferedImage img, int x, int y) {
            this.img = img;
            this.x = x;
            this.y = y;
        }
    }
}
