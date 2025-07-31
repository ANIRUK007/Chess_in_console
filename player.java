import java.io.Serializable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class player implements Serializable {
    private static final Random rand = new Random();
    private String name;
    private String userid;
    private int rank;
    private int wins;
    private int losses;

    // Constructor for new players
    public player(String name) {
        this.name = name;
        this.userid = wordLibrary.getRandomWord() + "_" + rand.nextInt(0, 10000000);
        this.rank = 500;
        this.wins = 0;
        this.losses = 0;

        // Save to file when created
        saveToFile();
    }
    
    // Constructor for loading existing players (doesn't auto-save)
    public player(String name, boolean saveToFile) {
        this.name = name;
        this.userid = wordLibrary.getRandomWord() + "_" + rand.nextInt(0, 10000000);
        this.rank = 500;
        this.wins = 0;
        this.losses = 0;

        if (saveToFile) {
            saveToFile();
        }
    }

    // Save player data to file
    private void saveToFile() {
        try (FileWriter writer = new FileWriter("players.txt", true)) {
            String data = String.format("name:%s,userid:%s,rank:%d,wins:%d,losses:%d\n",
                    name, userid, rank, wins, losses);
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Error saving player: " + e.getMessage());
        }
    }
    
    // Update player data in file after game
    public void updateInFile() {
        playermanager.updatePlayerInFile(this);
    }

    // Getter methods
    public String getName() { return name; }
    public String getUserId() { return userid; }
    public int getRank() { return rank; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }

    // Setters for update
    public void setRank(int rank) { this.rank = rank; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    
    // Special setter for loading from file
    public void setUserIdFromFile(String userid) { this.userid = userid; }
}