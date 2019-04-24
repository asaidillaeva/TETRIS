package Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.lang.NullPointerException;

import static Main.PlayMusic.playMusic;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final Point[][][] Tetraminos = {
            // I-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
            },

            // J-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
            },

            // L-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
            },

            // O-Piece
            {
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
            },

            // S-Piece
            {
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
            },

            // T-Piece
            {
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
            },

            // Z-Piece
            {
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
            }
    };

    private final Color[] tetraminoColors = {
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
    private Map<Integer, Boolean> playMusicTracker;

    private long score;
    private Color[][] well;

    // Creates a border around the well and initializes the dropping piece
    private void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    // Put a new, random piece into the dropping position
    private void newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        if (nextPieces.isEmpty()) {
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);
        nextPieces.remove(0);
    }

    // Collision test for the dropping piece
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    // Rotate the piece clockwise or counterclockwise
    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    // Move the piece left or right
    public void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    private void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
        }
        repaint();
    }

    // Make the dropping piece part of the well, so it is available for
    // collision detection.
    private void fixToWell() {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();
        newPiece();
    }

    private void deleteRow(int row) {
        for (int j = row-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j+1] = well[i][j];
            }
        }
    }


    // Clear completed rows from the field and award score according to
    // the number of simultaneously cleared rows.
    private void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        // Paint the well
        g.fillRect(0, 0, 26*12, 26*23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26*i, 26*j, 25, 25);
            }
        }

        // Display the score
        g.setColor(Color.WHITE);
        g.drawString("" + score, 19*12, 25);

        // Draw the currently falling piece
        drawPiece(g);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("TETRIS");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*26+10, 26*23+25);
        f.setVisible(true);

        final Tetris game = new Tetris();
        game.init();
        f.add(game);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        playMusic("wav/rotate.wav", false);
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        playMusic("wav/rotate.wav", false);
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        playMusic("wav/rotate.wav", false);
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        playMusic("wav/rotate.wav", false);
                        game.move(+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        playMusic("wav/drop.wav", false);
                        game.dropDown();
                        game.score += 1;
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (game.score >= 0 && game.score <= 500) {
                            playMusic("wav/holy_alphabet.wav", false);
                            Thread.sleep(1000);
                            game.dropDown();
                            game.playMusicTracker.put(500, true);

                        } else if (game.score >= 501 && game.score <= 1000) {
                            playMusic("wav/holy_alphabet.wav", false);
                            Thread.sleep(900);
                            game.dropDown();
                            game.playMusicTracker.put(1000, true);

                        } else if (game.score >= 1001 && game.score <= 1500) {
                            playMusic("wav/holy_caffeine.wav", false);
                            Thread.sleep(800);
                            game.dropDown();
                            game.playMusicTracker.put(1500, true);

                        } else if (game.score >= 1501 && game.score <= 2000) {
                            playMusic("wav/holy_caffeine.wav", false);
                            Thread.sleep(700);
                            game.dropDown();
                            game.playMusicTracker.put(2000, true);

                        } else if (game.score >= 2001 && game.score <= 2500) {
                            playMusic("wav/holy_fruit_salad.wav", false);
                            Thread.sleep(600);
                            game.dropDown();
                            game.playMusicTracker.put(2500, true);
                        } else if (game.score >= 2501 && game.score <= 3000) {
                            playMusic("wav/holy_fruit_salad.wav", false);
                            Thread.sleep(550);
                            game.dropDown();
                            game.playMusicTracker.put(3000, true);
                        } else if (game.score >= 3001 && game.score <= 3500) {
                            playMusic("wav/holy_heart_failure.wav", false);
                            Thread.sleep(500);
                            game.dropDown();
                            game.playMusicTracker.put(3500, true);
                        } else if (game.score >= 3501 && game.score <= 4000) {
                            playMusic("wav/holy_heart_failure.wav", false);
                            Thread.sleep(400);
                            game.dropDown();
                            game.playMusicTracker.put(4000, true);
                        } else if (game.score >= 4001 && game.score <= 4500) {
                            playMusic("wav/holy_mashed_potatoes.wav", false);
                            Thread.sleep(350);
                            game.dropDown();
                            game.playMusicTracker.put(4500, true);
                        } else if (game.score >= 4501 && game.score <= 5000) {
                            playMusic("wav/holy_mashed_potatoes.wav", false);
                            game.playMusicTracker.put(5000, true);
                            Thread.sleep(300);
                            game.dropDown();
                        } else if (game.score >= 5001 && game.score <= 5500) {
                            playMusic("wav/holy_nightmare.wav", false);
                            game.playMusicTracker.put(5500, true);
                            Thread.sleep(250);
                            game.dropDown();
                        } else if (game.score >= 5501 && game.score <= 6000) {
                            playMusic("wav/holy_nightmare.wav", false);
                            game.playMusicTracker.put(6000, true);
                            Thread.sleep(200);
                            game.dropDown();
                        } else if (game.score >= 6001 && game.score <= 6500) {
                            playMusic("wav/bitchin.wav", false);
                            game.playMusicTracker.put(6500, true);
                            Thread.sleep(150);
                            game.dropDown();
                        } else {
                            playMusic("wav/bitchin.wav", false);
                            game.playMusicTracker.put(7000, true);
                            Thread.sleep(100);
                            game.dropDown();
                        }
                    } catch (InterruptedException | NullPointerException ignored) {
                    }
                }
            }
        }.start();
    }
}
