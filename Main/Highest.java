package Main;

import java.io.*;

public class Highest {
    public static String getHighest(){

        // The name of the file to open.
        String fileName = "/home/aliya/Git/TETRIS/Main/wav/HighestScore.txt";

        // This will reference one line at a time
        String newScore=String.valueOf(0);
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            newScore = bufferedReader.readLine();
            while(newScore != null) {
                newScore = bufferedReader.readLine();
                System.out.println(newScore);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        return newScore;
    }
    public static void setHighest(String newScore){

        // The name of the file to open.
        String fileName = "/home/aliya/Git/TETRIS/Main/wav/HighestScore.txt";

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            bufferedWriter.write(newScore);

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }
}
