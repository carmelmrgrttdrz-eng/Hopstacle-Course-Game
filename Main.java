import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class Main extends JFrame implements Runnable {

    private JProgressBar loadingBar;
    private final String GAME_COLOR = "#22B14C";
    private Image logoImage;

    public Main() {
        setSize(285, 205);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        try {
            logoImage = new ImageIcon(getClass().getResource("images/logo.png")).getImage();
        } catch (Exception e) {
            System.out.println("Failed to load player image: " + e.getMessage());
        }

        // Custom panel to paint image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw logo
                int x = (getWidth() - 100) / 2;
                int y = 30;
                g2.drawImage(logoImage, x, y, 100, 100, this);
            }
        };
        panel.setLayout(null);

        // Progress Bar
        loadingBar = new JProgressBar(0, 100);
        loadingBar.setBounds(50, 155, 180, 20);
        loadingBar.setValue(0);
        loadingBar.setStringPainted(true);
        loadingBar.setBackground(Color.WHITE);

        // Rounded corners for progress bar
        loadingBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                int width = loadingBar.getWidth();
                int height = loadingBar.getHeight();
                int amountFull = getAmountFull(loadingBar.getInsets(), width, height);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, width, height, 10, 10);

                // Foreground progress
                g2.setColor(Color.decode(GAME_COLOR));
                g2.fillRoundRect(0, 0, amountFull, height, 10, 10);

                // Border
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

                // Text
                String text = loadingBar.getString();
                if (loadingBar.isStringPainted() && text != null) {
                    FontMetrics fm = g2.getFontMetrics();
                    int stringWidth = fm.stringWidth(text);
                    int stringHeight = fm.getAscent();
                    g2.setColor(Color.BLACK);
                    g2.drawString(text, (width - stringWidth) / 2, (height + stringHeight / 2) - 12);
                }
            }
        });

        panel.add(loadingBar);
        add(panel);

        Thread t = new Thread(this); // Start progress thread
        t.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadingBar.setValue(i);
        }

        MainFrame f2 = new MainFrame();
        f2.setVisible(true);
        dispose();
    }
}
