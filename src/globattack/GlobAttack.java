package globattack;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

public class GlobAttack extends PApplet {
    float playerX = 256;
    float playerY = 352;
    boolean left, right, up, down;

    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    float enemySpeed = 1f;

    float bulletSpeed = 5;
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();

    float spawnRate = 300;
    PImage backgroundImg;

    PImage[] playerAnim = new PImage[6];
    int animationFrame = 1;

    PImage[][] enemyAnimations = new PImage[3][6];

    PImage[] explosionAnimation = new PImage[6];



    int score = 0;
    int attempt = 1;
    int highScore = 0;
    PFont scoreFont;

    enum GameState {
        OVER, RUNNING
    }

    static GameState currentState;

    PImage gameOverImg;
    PImage restartButton;

    PImage[] plants = new PImage[5];

    int health = 100;
    int kills = 0;
    float healthBoostTimer = 600;

    public static void main(String[] args) {
        PApplet.main("globattack.GlobAttack");
    }

    public void settings() {
        size(512, 704);
    }

    public void setup() {
        backgroundImg = loadImage("Images/Background.png");
        backgroundImg.resize(512, 704);
        for (int i = 1; i <= 6; i++) {
            playerAnim[i - 1] = loadImage("Images/Bat_Brains_" + i + ".png");
            playerAnim[i - 1].resize(60, 0);
        }
        for (int j = 1; j <= 6; j++) {
            enemyAnimations[0][j - 1] = loadImage("Images/Bat_Purple" + j + ".png");
            enemyAnimations[1][j - 1] = loadImage("Images/Bat_Square" + j + ".png");
            enemyAnimations[2][j - 1] = loadImage("Images/Bat_Booger" + j + ".png");

            enemyAnimations[0][j - 1].resize(60, 0);
            enemyAnimations[1][j - 1].resize(60, 0);
            enemyAnimations[2][j - 1].resize(60, 0);
        }
        for (int i = 1; i <= 6; i++) {
            explosionAnimation[i - 1] = loadImage("Images/Explosion_FX" + i + ".png");
            explosionAnimation[i - 1].resize(60, 0);
        }
        for (int p = 1; p <= 5; p++) {
            plants[p - 1] = loadImage("Images/plant" + p + ".png");
        }
        currentState = GameState.RUNNING;
        gameOverImg = loadImage("Images/GameOverImg.png");
        gameOverImg.resize(300, 0);
        restartButton = loadImage("Images/WoodButton.png");
        restartButton.resize(240, 50);
    }

    public void draw() {
        drawBackground();
        drawPlant();
        switch (currentState) {
            case OVER:
                drawGameOver();
                break;

            case RUNNING:
                drawScore();
                noStroke();
                if (frameCount % 5 == 0) {
                    animationFrame++;
                    animationFrame = animationFrame % 6;
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy en = enemies.get(i);
                        if (en.isDead == true) {
                            en.explosionFrame++;
                            if (en.explosionFrame == 5) {
                                enemies.remove(i);
                            }
                        }
                    }
                }
                drawPlayer();
                increaseDifficulty();
                increaseHealth();

                for (int b = 0; b < bullets.size(); b++) {
                    Bullet bull = bullets.get(b);
                    bull.move();
                    bull.drawBullet();
                    if (bull.x < 0 || bull.x > width || bull.y < 0 || bull.y > height) {
                        bullets.remove(b);
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy en = enemies.get(i);
                    en.move(playerX, playerY);
                    en.drawEnemy();
                    for (int j = 0; j < bullets.size(); j++) {
                        Bullet b = bullets.get(j);
                        if (abs(b.x - en.x) < 15 && abs(b.y - en.y) < 15 && !en.isDead) {
                            en.isDead = true;
                            bullets.remove(j);

                            if (kills <= 15) {
                                score++;
                            }
                            if (kills > 15 && kills <= 40) {
                                score+= 2;
                            }
                            if (kills > 40 && kills <= 90) {
                                score+= 3;
                            }
                            if (kills > 90 && kills <= 120) {
                                score += 5;
                            }
                            if (kills > 120) {
                                score += 15;
                            }

                            kills++;
                            Random random = new Random();
                            random.nextInt(100);
                            if (random.nextInt() > 50) {
                                System.out.println("Plus one health boost");
                                health++;
                            }
                            break;
                        }
                    }
                    if (abs(playerX - en.x) < 15 && abs(playerY - en.y) < 15) {
                        health--;
                        en.isDead = true;
                        if (health <= 0) {
                            if (score > highScore) {
                                highScore = score;
                            }
                            currentState = GameState.OVER;
                        }
                    }
                }
                break;
        }

    }

    public void drawGameOver() {
        imageMode(CENTER);
        image(gameOverImg, width / 2, height / 2);
        fill(122, 64, 51);
        textAlign(CENTER);
        text("Game Over ", width / 2, height / 2 - 100);
        text("Score: " + score, width / 2, height / 2 - 50);
        text("Kills: " + kills, width / 2, height / 2 - 20);
        text("High Score: " + highScore, width / 2, height / 2 + 10);
        image(restartButton, width / 2, height / 2 + 100);
        fill(255, 255, 255);
        text("Restart ", width / 2, height / 2 + 105);
    }

    public void drawScore() {
        scoreFont = createFont("Leelawadee UI Bold", 26, true);
        textFont(scoreFont);
        fill(255,215,0);
        textAlign(CENTER);
        text("Score: " + score, width - 90, 40);
        text("Attempt: " + attempt, width - 425, 675);
        text ("Health: " + health, width - 425, 40);
        text("Kills: " + kills, width - 90, 675);
    }

    public void drawBackground() {
        background(250);
        imageMode(CORNER);
        image(backgroundImg, 0, 0);
    }
    public void drawPlant(){
        image(plants[0], 100, 90);
        image(plants[1], 140, 400);
        image(plants[2], 140, 240);
        image(plants[3], 340, 300);
        image(plants[4], 250, 490);
        image(plants[3], 200, 130);
        image(plants[4], 200, 390);
        image(plants[0], 330, 450);
        image(plants[1], 330, 120);
        image(plants[2], 340, 400);
        image(plants[3], 320, 200);
        image(plants[4], 240, 100);
    }

    public void increaseDifficulty() {
        if (frameCount % spawnRate == 0) {
            generateEnemy();
            if (enemySpeed < 5) {
                enemySpeed += 0.1f;
            }
            if (spawnRate > 50) {
                spawnRate -= 10;
            }
        }
    }

    public void increaseHealth() {
        if (frameCount % healthBoostTimer == 0) {
            health += 7;
        }
    }

    public void generateEnemy() {
        int side = (int) random(0, 2);
        int side2 = (int) random(0, 2);
        if (side % 2 == 0) { // top and bottom
            enemies.add(new Enemy(random(0, width), height * (side2 % 2), (int) random(0, 3)));
        } else { // sides
            enemies.add(new Enemy(width * (side2 % 2), random(0, height), (int) random(0, 3)));
        }
    }

    public void drawPlayer() {
        if (up) {
            playerY -= 5;
        }
        if (left) {
            playerX -= 5;
        }
        if (right) {
            playerX += 5;
        }
        if (down) {
            playerY += 5;
        }
        playerX = constrain(playerX, 70, width - 70);
        playerY = constrain(playerY, 70, height - 70);
        imageMode(CENTER);
        image(playerAnim[animationFrame], playerX, playerY);
    }

    public void mousePressed() {
        switch (currentState) {
            case RUNNING:
                float dx = mouseX - playerX;
                float dy = mouseY - playerY;
                float angle = atan2(dy, dx);
                float vx = bulletSpeed * cos(angle);
                float vy = bulletSpeed * sin(angle);
                bullets.add(new Bullet(playerX, playerY, vx, vy));
                break;
            case OVER:
                if (mouseX > (width / 2 - 120) && mouseX < (width / 2 + 120) && mouseY > height / 2 + 100 - 25 && mouseY < (height / 2 + 100 + 25)) {
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.remove(i);
                        score = 0;
                        enemySpeed = 1f;
                        spawnRate = 300;
                    }
                    health = 100;
                    kills = 0;
                    attempt++;
                    currentState = GameState.RUNNING;
                }
                break;
        }

    }

    public void keyPressed() {
        if (key == 'w' || keyCode == UP) {
            up = true;
        }
        if (key == 'a' || keyCode == LEFT) {
            left = true;
        }
        if (key == 's' || keyCode == DOWN) {
            down = true;
        }
        if (key == 'd' || keyCode == RIGHT) {
            right = true;
        }
        if (keyCode == 9) {
            score += 5000;
        }
    }

    public void keyReleased() {
        if (key == 'w' || keyCode == UP) {
            up = false;
        }
        if (key == 'a' || keyCode == LEFT) {
            left = false;
        }
        if (key == 's' || keyCode == DOWN) {
            down = false;
        }
        if (key == 'd' || keyCode == RIGHT) {
            right = false;
        }

    }

    class Enemy {
        float x, y, vx, vy;
        int enemyType = 0;
        boolean isDead = false;
        int explosionFrame = 0;

        Enemy(float x, float y, int enemyType) {
            this.x = x;
            this.y = y;
            this.enemyType = enemyType;
        }

        public void drawEnemy() {
            if (isDead == false) {
                imageMode(CENTER);
                image(enemyAnimations[enemyType][animationFrame], x, y);
            } else {
                image(explosionAnimation[explosionFrame], x, y);
            }
        }

        public void move(float px, float py) {
            if (isDead == false) {
                float angle = atan2(py - y, px - x);
                vx = cos(angle);
                vy = sin(angle);
                x += vx * enemySpeed;
                y += vy * enemySpeed;
            }
        }
    }

    class Bullet {
        float x, y, vx, vy;

        Bullet(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void drawBullet() {
            fill(255,0,0);
            ellipse(x, y, 10, 10);
        }

        void move() {
            x += vx;
            y += vy;
        }
    }
    /*
    class Pickups {
        float x, y, vx, vy;
        int pickupType = 0;
        int pickupFrame = 0;

        Pickups(float x, float y, int pickupType) {
            this.x = x;
            this.y = y;
            this.pickupType = pickupType;
            int disappear = 0;
        }

         void generatePickup() {
            int side = (int) random(0, 2);
            int side2 = (int) random(0, 2);
            if (side % 2 == 0) {
                pickups.add(new Pickups(random(0, width), height * (side2 % 2), (int) random(0, 3)));
            } else {
                pickups.add(new Pickups(width * (side2 % 2), random(0, height), (int) random(0, 3)));
            }
        }
        public void drawPickup() {
                imageMode(CENTER);
                image(enemyAnimations[pickupType][pickupFrame], x, y);
                image(explosionAnimation[disappear], x, y);
        }

    }
    */
}
