 import javax.swing.JFrame;

public class GameFrame {
    public static void main(String[] args) {
        JFrame gameFrame = new JFrame("Hop Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(1200, 640);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setUndecorated(true);
        gameFrame.setLayout(null);

        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 1200, 640);
        gameFrame.add(gamePanel);

        gameFrame.setVisible(true);
    }
}

