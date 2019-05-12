package Main;
import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.lang.NullPointerException;



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
    private ArrayList<Integer> nextPieces;

    {
        nextPieces = new ArrayList<>();
    }

    private Map<Integer, Boolean> playMusicTracker;

    private long score;
    private static Color[][] well;
    private int x;
    private int y;

    // Creates a border around the well and initializes the dropping piece
    private void init() {
        well = new Color[20][24];
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
    private void rotate(int i) {
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
    private void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    // Drops the piece one line or fixes it to the well if it can't drop
    private void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        }
        else {
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
    private static void gameover() {

        JFrame gover = new JFrame("GAMEOVER");
        gover.setSize(600,600);
        gover.setVisible(true);
        gover.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gover.setBackground(Color.CYAN);
        JPanel p2 = new JPanel(new GridBagLayout());
        p2.setLayout(null);
        p2.setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();

        JButton exit = new JButton("EXIT");
        JButton restart = new JButton("RESTART");
        exit.setBounds(150,300,100,50);
        restart.setBounds(350,300,100,50);
        exit.setBackground(Color.GREEN);
        restart.setBackground(Color.YELLOW);
        exit.addActionListener(actionEvent -> {
                gover.setVisible(false);
                gover.dispose();
        });
        restart.addActionListener(actionEvent -> {
            gover.setVisible(false);
            gover.dispose();
            startGame();
        });
        p2.add(exit);
        p2.add(restart);
        gover.add(p2);
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
                PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/blockClear.wav");
                break;
            case 2:
                score += 300;
                PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/blockClear.wav");
                break;
            case 3:
                score += 500;
                PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/blockClear.wav");
                break;
            case 4:
                score += 800;
                PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/blockClear.wav");
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
        g.setColor(Color.RED);
        g.clearRect(0,0,26*17, 25);
        g.drawString("SCORE:" + score, 26*17, 25);

        // Draw the currently falling piece
        drawPiece(g);
    }


    public static void main(String[] args) {
        JFrame m = new JFrame ("TETRIS");
        m.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        m.setSize(600, 600);
        m.setVisible(true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setLayout(null);
        p.setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();


        JButton play=new JButton("PLAY");
        play.setBackground(Color.YELLOW);
        play.setBounds(250, 260, 100, 50);
        JButton help = new JButton("HELP");
        help.setBounds(250, 320, 100,50);
        help.setBackground(Color.RED);
        p.add(help);
        p.add(play);
        m.add(p);
        help.addActionListener (actionEvent -> {


            JFrame t = new JFrame("HELP");
            t.setVisible(true);
            t.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            t.setSize(600,600);
            t.setBackground(Color.BLACK);
            JPanel p3 = new JPanel();
            p3.setBackground(Color.WHITE);
            p3.setSize(600,600);
            JTextArea jt = new JTextArea();
            jt.setText("Tetris (derived from \"tetramino\" and \"tennis\") - developed by the inventor and developer of the USSR programmer Alexey Pajitnov\non June 6, 1984. \nIn this game we use tetramino figures as:\n \n I: four blocks in a straight line.\n \n O: four blocks in a 2×2 square. \n \n T: a row of three blocks with one added below the center.\n \n J: a row of three blocks with one added below the right side. \n \n L: a row of three blocks with one added below the left side.\n \n S: two stacked horizontal dominoes with the top one offset to the right. \n \n Z: two stacked horizontal dominoes with the top one offset to the left.\n\nThe S, Z and J, L figures are reflection of the each other.\n\n\nHow to play this game?\n\nButton_RIGHT => move right\n\nButton_Left => move left\n\nButton_Down => move down faster\n\nButton_Up => rotate piece\n\nAfter every +500 score the speed increases.\n\nmove_down_faster => score +1\n\n1 filled row => score +100\n\n2 filled row => score +300\n\n3 filled row => score +500\n\n4 filled row => score +800\n\nYour goal is to fill a row with blocks of figures using the top buttons and collect the most points.\n\n\nThe benefits of playing TETRIS\n\nAccording to the study Mind research network, regular Main.Tetris game allows you to \nimprove your planning skills, develop critical thinking, quick mental response. \nMoreover, Main.Tetris can increase brain size. \"Main.Tetris\" for the brain is very complicated. \nA combination of many brain processes is required: attention, visual and motor coordination,\nmemory. All this should work together very quickly. Not surprisingly, Main.Tetris develops \nseveral areas of the brain. Besides, every time when it is possible to lower the figure and \nachieve the disappearance of the level, the person feels satisfied with the solution of the \nproblem. Since the process is cyclical, it is rather difficult to break away from the game. \nАs well as playing tetris helps to overcome post-traumatic disorder and stress, as well as \ncravings for drugs, cigarettes and even food. \n");
            jt.setBackground(Color.WHITE);
            jt.setColumns(20);
            jt.setRows(100);
            JScrollPane js = new JScrollPane();
            js.setViewportView(jt);
            p3.add(jt);
            t.add(p3);
        });

        play.addActionListener(actionEvent -> {

            m.setForeground(Color.WHITE);
            startGame();
        });

    }

    private static void startGame()
    {
        JFrame f = new JFrame("TETRIS");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 600);
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
                    game.rotate(-1);
                    break;
                case KeyEvent.VK_DOWN:
                    game.rotate(+1);
                    break;
                case KeyEvent.VK_LEFT:
                    game.move(-1);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.move(+1);
                    break;
                case KeyEvent.VK_SPACE:
                    game.dropDown();
                    game.score += 1;
                    PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/drop.wav");
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
        }
    });

    // Make the falling piece drop every second
        new Thread(() -> {
            PlayMusic.playMusic("/home/aliya/Git/TETRIS/Main/wav/bgMusic.wav");
            while (true) try {
                for (int r = 0; r < 22; r++) {

                    if (game.score >= 0 && game.score <= 500) {
                        Thread.sleep(1000);
                        game.dropDown();
                    } else if (game.score >= 501 && game.score <= 1000) {
                        Thread.sleep(900);
                        game.dropDown();
                    } else if (game.score >= 1001 && game.score <= 1500) {
                        Thread.sleep(800);
                        game.dropDown();
                    } else if (game.score >= 1501 && game.score <= 2000) {
                        Thread.sleep(700);
                        game.dropDown();
                    } else if (game.score >= 2001 && game.score <= 2500) {
                        Thread.sleep(600);
                        game.dropDown();
                    } else if (game.score >= 2501 && game.score <= 3000) {
                        Thread.sleep(550);
                        game.dropDown();
                    } else if (game.score >= 3001 && game.score <= 3500) {
                        Thread.sleep(500);
                        game.dropDown();
                    } else if (game.score >= 3501 && game.score <= 4000) {
                        Thread.sleep(400);
                        game.dropDown();
                    } else if (game.score >= 4001 && game.score <= 4500) {
                        Thread.sleep(350);
                        game.dropDown();
                    } else if (game.score >= 4501 && game.score <= 5000) {
                        Thread.sleep(300);
                        game.dropDown();
                    } else if (game.score >= 5001 && game.score <= 5500) {
                        Thread.sleep(250);
                        game.dropDown();
                    } else if (game.score >= 5501 && game.score <= 6000) {
                        Thread.sleep(200);
                        game.dropDown();
                    } else if (game.score >= 6001 && game.score <= 6500) {
                        Thread.sleep(150);
                        game.dropDown();
                    } else {
                        Thread.sleep(100);
                        game.dropDown();
                    }
                }
                for (int i = 1; i<=11; i++){
                    if(well[i][21]!=Color.BLACK){gameover();}
                }
            }catch (InterruptedException | NullPointerException ignored) {
            }
        }).start();
}
}

