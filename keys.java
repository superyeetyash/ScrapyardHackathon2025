import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.awt.Transparency;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class FinalGame extends Drawing {
    public static void main(String[] args) {
        Runner.project = new FinalGame();
        Runner.project.drawGrid = drawGrid;
        Runner.project.directions = directions;
        Runner.project.windowTitle = windowTitle;
        Runner.main();
    }

    // Change this to false to turn off the grid
    static boolean drawGrid = false;
    // Change the strings in the array to display your directions for your project
    static String[] directions = {"Hold the W, A, S, and D keys to move the player", "Hold the left and right arrow keys to rotate the light", "Hold E to open the door", "", ""};
    static String windowTitle = "Anxiety by Alex Jameson";

    // Global Variables go here
    int screen = 0;
    int coolDown = 0;
    int numOfRays = 60;
    int mouseXPos = Mouse.x;
    int mouseYPos = Mouse.y;
    int endX = 0;
    int endY = 0;
    int length = 150;
    double angle = 0;
    int counter = 0;
    int[] player = {375, 275, 50, 50, 5};
    int playerMX = player[0] + 25;
    int playerMY = player[1] + 25;
    int width = endX - playerMX;
    int height = endY - playerMY;
    int min = 100;
    int max = 500;
    int randomPos = 0;
    int[] randomPositions = {(int) (Math.random() * (max - min + 1)) + min};
    int[][] enemies = { //700, 300
        {randomPos, randomPos, 10, 10, 5}
    };
    int[][] walls = {
        {40, 20, 200, 20},
        {200, 0, 800, 50},
        {0, 0, 50, 650},
        {50, 550, 800, 50},
        {750, 50, 50, 600}
    };
    int[][] doors = {
        {40, 20, 200, 20}
    };
    int[][] triggerBoxes = {
        {40, 20, 200, 60}
    };

    int frame = 0; // each frame is 10 ms

    int[] ray = {playerMX, playerMY, endX, endY};
    //int[] rayRect = {playerMX, playerMY, 1, length};

    boolean isCollision = false;

    final long DURATION = 1000; // 2 seconds in milliseconds
    Line2D.Double rayLine = new Line2D.Double(ray[0], ray[1], ray[2], ray[3]);

    int numFrames = 0;
    int frameCounter = 0;
    int seconds = 0;
    int frameCountdown = 1000;
    String doorString = "Obtain 20 points to unlock!";

    Color transparent = new Color(1f,0f,0f,1f);

    Color crimson = new Color(139, 0, 0);
    Color offwhite = new Color(240, 240, 200);

    @Override
    public void drawPerFrame(Graphics2D g2d) {
        if (numFrames == 0) {
            //drawEnemy(g2d);
        }

        // Drawing code goes here
        if (screen == 0) {
            renderHomeScreen(g2d);
            counter = 0;
            seconds = 0;
            frameCounter = 0;
            numOfRays = 60;
            frameCountdown = 1000;
            if (Keys.g) {
                screen++;
            }
        } else if (screen == 1) {
            g2d.setStroke(new BasicStroke(8));
            drawTriggers(g2d);
            updatePlayer(g2d);
            render(g2d);
            openDoor(g2d);
            frameCountdown--;
        }
        if (screen == 2) {
            renderEndScreen(g2d);
            if (Keys.t) {
                screen = 0;
            }
        }
        if (screen == 3) {
            renderWinScreen(g2d);
            if (Keys.t) {
                screen = 0;
            }
        }

        numFrames++;
        frameCounter++;
    }

    // Methods go HERE

    public void renderEndScreen(Graphics g2d) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        g2d.setColor(crimson);
        g2d.drawString("Game Over. You lost.", 250, 250);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
        g2d.drawString("Press 'T' to play again...", 350, 400);
    }

    public void renderWinScreen(Graphics g2d) {
        g2d.setColor(offwhite);
        g2d.setFont(new Font("TimesRoman", Font.BOLD, 40)); 
        g2d.drawString("YOU WON!", 300, 100);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2d.drawString("Your points: " + counter, 260, 250);
        g2d.drawString("Your time alive: " + seconds + " seconds", 480, 250);
        g2d.drawString("Would you like to play again? Press 'T'", 270, 400);
    }

    public void renderHomeScreen(Graphics g2d) {
        g2d.setColor(offwhite);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        g2d.drawString("Welcome! There are invisible enemies that randomly spawn around the map every few seconds!", 100, 100);
        g2d.drawString("Don't let them become visible in your light! If you have seen them too many times, then game over!", 100, 200);
        g2d.drawString("Consume them (by touching them with the player) to gain points, and get 20 points to unlock the door...", 100, 300);
        g2d.drawString("Good luck! (press 'G' to start)", 300, 400);
    }

    public void drawPlayer(Graphics g2d) {
        g2d.setColor(crimson);
        g2d.fillOval(player[0], player[1], player[2], player[3]);
    }

    public void drawWalls(Graphics g2d) {
        Color brown = new Color(150, 75, 0);
        Color darkRed = new Color(82, 20, 20);
        for (int i = 0; i < walls.length; i++) {
            if (i == 0) {
                g2d.setColor(brown);
            } else {
                g2d.setColor(darkRed);
            }
            g2d.fillRect(walls[i][0], walls[i][1], walls[i][2], walls[i][3]);
        }
    }

    public int randomizer(Graphics g2d) {
        int random = (int) (Math.random() * (max - min + 1)) + min;
        return random;
    }

    public void drawEnemy(Graphics g2d) {
        isCollision = false; // Reset collision state before checking


        for (int i = 0; i < enemies.length; i++) {
            if (numFrames >= 200) {
                randomPos = (int) (Math.random() * (max - min + 1)) + min;
                enemies[i][0] = (int) (Math.random() * (max - min + 1)) + min;;
                enemies[i][1] = (int) (Math.random() * (max - min + 1)) + min;; 
                numFrames = 0;
            }
        }



        for (int i = 0; i < enemies.length; i++) {
            if (collide(player, enemies[i])) {
                g2d.setColor(Color.BLACK);
                enemies[i][0] = 9999999;
                enemies[i][1] = 9999999;
                counter++;
                frameCountdown = 1000;
                //System.out.println(counter);
                //System.out.println(angle);
            } else if (checkRayCollision(endX, endY) == true) {
                g2d.setColor(Color.RED);
            } else {
                Color lightGray = new Color(10, 10, 10);
                g2d.setColor(lightGray);
            }
            g2d.fillOval(enemies[i][0], enemies[i][1], enemies[i][2], enemies[i][3]);
        }
    }

    public void render(Graphics g2d) {
        drawWalls(g2d);
        drawPlayer(g2d);
        drawEnemy(g2d);
        textDisplay(g2d);
    }

    public void textDisplay(Graphics g2d) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2d.setColor(Color.RED);
        g2d.drawString("Points: " + counter, 100, 570);
        if (frameCounter == 61) {
            seconds++;
            frameCounter = 0;
        }
        g2d.drawString("Time Alive: " + seconds + " seconds", 500, 570);
        g2d.drawString("Time left to consume an enemy: " + frameCountdown, 300, 40);
        if (frameCountdown <= 0) {
            screen = 2;
        }
    }

    public void changeRay(Graphics g2d, double angle) {
        endX = (int) Math.round(Math.cos(Math.toRadians(angle)) * length);
        endY = (int) Math.round(Math.sin(Math.toRadians(angle)) * length);

        endX += playerMX;
        endY += playerMY;

        width = endX - playerMX;
        height = endY - playerMY;

        checkRayCollision(endX, endY);

        g2d.setColor(offwhite);

        ray[0] = playerMX;
        ray[1] = playerMY;
        ray[2] = endX;
        ray[3] = endY;

        //rayRect[0] = playerMX;
        //rayRect[1] = playerMY;
        //rayRect[2] = 5;
        //rayRect[3] = length;
        rayLine.setLine(ray[0], ray[1], ray[2], ray[3]); // Update rayLine here

        //g2d.fillRect(rayRect[0], rayRect[1], rayRect[2], rayRect[3]);
        g2d.drawLine(ray[0], ray[1], ray[2], ray[3]);
    }

    public boolean checkRayCollision(int endX, int endY) {
        for (int i = 0; i < enemies.length; i++) {
            int enemyX = enemies[i][0];
            int enemyY = enemies[i][1];
            int enemyWidth = enemies[i][2];
            int enemyHeight = enemies[i][3];

            Rectangle2D.Double enemyRect = new Rectangle2D.Double(enemyX, enemyY, enemyWidth, enemyHeight);

            if (enemyRect.intersectsLine(rayLine)) {
                isCollision = true;
                frame++;
                if (frame == 10 && numOfRays <= 360) {
                    numOfRays += 1;
                    frame = 0;
                } else if (numOfRays >= 360 || frameCounter <= 0) {
                    screen = 2;
                    numOfRays = 60;
                }
                break;
            }
        }

        if (isCollision) {
            //System.out.println("The line intersects with an enemy!");
            return true;
        } else {
            //System.out.println("The line does not intersect with any enemies.");
            return false;
        }
    }

    public void updatePlayer(Graphics g2d) {
        playerMX = player[0] + 25;
        playerMY = player[1] + 25;

        for (int i = 0; i < numOfRays; i++) {
            changeRay(g2d, (angle + i) - 30);
        }

        if (Keys.d) {
            player[0] += player[4];
        } else if (Keys.a) {
            player[0] -= player[4];
        }

        if (Keys.left) {
            angle -= 6;
        }
        if (Keys.right) {
            angle += 6;
        }
        if (angle > 360) {
            angle -= 360;
        }
        if (angle < 0) {
            angle += 360;
        }

        if (Keys.w) {
            player[1] -= player[4];
        } else if (Keys.s) {
            player[1] += player[4];
        }

        for (int i = 0; i < walls.length; i++) {
            if (collide(player, walls[i])) {
                if (Keys.d) {
                    player[0] -= player[4];
                } else if (Keys.a) {
                    player[0] += player[4];
                }
            }
        }

        for (int i = 0; i < walls.length; i++) {
            if (collide(player, walls[i])) {
                if (Keys.w) {
                    player[1] += player[4];
                } else if (Keys.s) {
                    player[1] -= player[4];
                }
            }
        }

        if (player[1] < 0) {
            screen = 3;
            player[1] = 275;
        }
    }

    public void drawTriggers(Graphics g2d) {

        g2d.setColor(Color.WHITE);

        for (int i = 0; i < triggerBoxes.length; i++) {
                if (collide(player, triggerBoxes[i])) {
                    if (counter >= 20) {
                        g2d.drawString("Hold E to unlock!", 260, 80);
                    }
                     else if (counter < 20) {
                         g2d.drawString(doorString, 260, 80);
                    }
                }
            }

        g2d.setColor(Color.BLACK);

        if (Keys.e) {
            for (int i = 0; i < triggerBoxes.length; i++) {
                if (collide(player, triggerBoxes[i])) {
                    if (counter >= 20) {
                        g2d.setColor(transparent);
                        walls[0][2] = 20;
                        walls[0][3] = 200;  
                    }
                }
            }
        } else {
            walls[0][2] = 200;
            walls[0][3] = 20;
        }
        for (int i = 0; i < triggerBoxes.length; i++) {
            g2d.fillRect(triggerBoxes[i][0], triggerBoxes[i][1], triggerBoxes[i][2], triggerBoxes[i][3]);
        }
    }

    public void openDoor(Graphics g2d) {
        for (int i = 0; i < triggerBoxes.length; i++) {
            if (collide(player, triggerBoxes[i])) {
                // Open the door logic here
            }
        }
    }

    public boolean collide(int[] rect1, int[] rect2) {
        if (rect1.length == 0 || rect2.length == 0) return false;

        if (rect1[0] + rect1[2] <= rect2[0]) {
            return false;
        }
        if (rect1[0] >= rect2[0] + rect2[2]) {
            return false;
        }
        if (rect1[1] + rect1[3] <= rect2[1]) {
            return false;
        }
        if (rect2[1] + rect2[3] <= rect1[1]) {
            return false;
        }
        return true;
    }
}
