package Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Main.Tetris.score;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Highest {
    public static String getHighest(){

        // The name of the file to open.
        String fileName = "/home/lab/TETRIS/Main/wav/HighestScore.txt";

        // This will reference one line at a time
        String newScore=String.valueOf(0);
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            newScore = bufferedReader.readLine();
            while(newScore != null) {
                newScore = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return newScore;
    }
    public static void setHighest(String score){
        byte data[] = score.getBytes();
        Path p = Paths.get("/home/lab/TETRIS/Main/wav/HighestScore.txt");

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data);
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}
