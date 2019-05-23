package Main;

import javax.swing.*;
import java.awt.*;

import static java.lang.Integer.valueOf;

public class Gameover extends Tetris {
    protected static void gameover() {
        isGameOver = true;
        f.dispose();

        gover = new JFrame("GAMEOVER");
        gover.setSize(310, 600);
        gover.setVisible(true);
        gover.setResizable(false);
        gover.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gover.setBackground(Color.CYAN);
        JPanel p2 = new JPanel(new GridBagLayout());
        p2.setLayout(null);
        p2.setBackground(Color.BLACK);

        JButton exit = new JButton("EXIT");
        JButton restart = new JButton("RESTART");
        restart.setBounds(100, 250, 100, 50);
        restart.setBackground(Color.YELLOW);
        restart.addActionListener(actionEvent -> {
            gover.dispose();
            startGame();
        });
        exit.setBounds(100, 310, 100, 50);
        exit.setBackground(Color.GREEN);
        exit.addActionListener(actionEvent -> {
            System.exit(1);
        });
        p2.add(exit);
        p2.add(restart);
        gover.add(p2);
        if(Highest.getHighest()!=null) {
            if (score > valueOf(Highest.getHighest())) {
                Highest.setHighest(String.valueOf(score));
            }
        }else{
            Highest.setHighest(String.valueOf(0));
        }
    }
}