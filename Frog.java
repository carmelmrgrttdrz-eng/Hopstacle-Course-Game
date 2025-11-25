import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Frog {
    private final int TILE_SIZE = 80;

    private Image frogGIF;
    private BufferedImage frogImg;
    private boolean isMoving = false; // <--- NEW: track if frog is moving
    private String direction = "up";
    private int x, y;
    private int speed = TILE_SIZE;
    private GamePanel.WaterRow attachedRow = null; //explain this code to me

    public Frog(int x, int y){
        this.x = x;
        this.y = y;
        loadImage();
    }

    public void setX(int x){ //to set the x position of the frog
        this.x = x;
    }

    public void setY(int y){ //to set the y position of the frog
        this.y = y; 
    }

    public int getX(){ //to get the x position of the frog
        return x;
    }

    public int getY(){ //to get the y position of the frog
        return y;
    }

    public int getHeight(){ //to get the height of the frog
        return TILE_SIZE;
    }

    public int getWidth(){ //to get the width of the frog
        return TILE_SIZE;
    }

    public void move(ArrayList<Rectangle> trees) { //to move the frog using arrow keys
        isMoving = true; //set moving true when moving to start the gif

        int nextX = x, nextY = y;
        if (direction.equals("up")) nextY -= speed;
        else if (direction.equals("down")) nextY += speed;
        else if (direction.equals("left")) nextX -= speed;
        else if (direction.equals("right")) nextX += speed;

        //frog can't jump on the trees
        Rectangle nextPos = new Rectangle(nextX, nextY, 80, 80); 
        boolean blocked = false;

        for (Rectangle tree : trees)
            if (nextPos.intersects(tree)) 
                blocked = true;

        if (!blocked) {
            x = nextX;
            y = nextY;
        }
        loadImage();
    }

    public void setDirection(String dir) { //to set the direction of the frog
        this.direction = dir;
    }

    public void setMoving(boolean moving) {
        if (moving && !isMoving) {//if the frog will move now and it was not moving before
            // Restart GIF only once per move
            frogGIF = new ImageIcon(getClass().getResource("/images/frog_"+direction+".gif")).getImage();
        }
        isMoving = moving;
    }

    public void scrollDown(int ySpeed) { //to move the frog along the game panel
        // Move frog down with the map
        y += ySpeed;

        if (attachedRow != null) {
            x += attachedRow.dir * 4; // match WATER_SPEED for smooth ride
        }
    }

    public Rectangle getBounds() { //get bounds of the frog
        return new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
    }

    public void attachToRow(GamePanel.WaterRow row){ //to attach the frog to the row its on
        attachedRow = row;
    }

    public void detachFromRow(){ //detach it to move to the next row
        attachedRow = null;
    }

    public void draw(Graphics g) {
        if (isMoving)
            g.drawImage(frogGIF, x, y, TILE_SIZE, TILE_SIZE, null);
        else
            g.drawImage(frogImg, x, y, TILE_SIZE, TILE_SIZE, null);
    }

    private void loadImage() {
        try {
            frogGIF = new ImageIcon(getClass().getResource("/images/frog_"+direction+".gif")).getImage();
            frogImg = ImageIO.read(getClass().getResource("/images/frog_"+direction+".png"));
        } catch (Exception e) {
            System.out.println("Failed to load player image: " + e.getMessage());
        }
    }
}
