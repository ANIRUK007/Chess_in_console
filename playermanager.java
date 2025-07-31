import java.io.*;
import java.util.*;

public class playermanager {
    
    // Load existing player from file
    public static player loadPlayer(String name) {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> playerData = parseLine(line);
                if (playerData.get("name").equals(name)) {
                    // Create player object with existing data
                    player existingPlayer = new player(name, false); // Don't save to file again
                    existingPlayer.setUserIdFromFile(playerData.get("userid"));
                    existingPlayer.setRank(Integer.parseInt(playerData.get("rank")));
                    existingPlayer.setWins(Integer.parseInt(playerData.get("wins")));
                    existingPlayer.setLosses(Integer.parseInt(playerData.get("losses")));
                    return existingPlayer;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading players file: " + e.getMessage());
        }
        return null; // Player not found
    }
    
    // Check if player exists
    public static boolean playerExists(String name) {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> playerData = parseLine(line);
                if (playerData.get("name").equals(name)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, that's okay
            return false;
        }
        return false;
    }
    
    // Get or create player
    public static player getOrCreatePlayer(String name, Scanner scanner) {
        if (playerExists(name)) {
            System.out.print("Player " + name + " already exists. Load existing player? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            
            if (choice.equals("y") || choice.equals("yes")) {
                player existingPlayer = loadPlayer(name);
                if (existingPlayer != null) {
                    System.out.println("Loaded existing player: " + name);
                    System.out.println("Stats - Rank: " + existingPlayer.getRank() + 
                                     ", Wins: " + existingPlayer.getWins() + 
                                     ", Losses: " + existingPlayer.getLosses());
                    return existingPlayer;
                }
            }
        }
        
        // Create new player
        System.out.println("Creating new player: " + name);
        return new player(name);
    }
    
    // Display all players from file
    public static void displayAllPlayers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            boolean hasPlayers = false;
            
            System.out.printf("%-15s %-20s %-8s %-6s %-8s %-10s%n", 
                            "Name", "User ID", "Rank", "Wins", "Losses", "Win Rate");
            System.out.println("-".repeat(70));
            
            while ((line = reader.readLine()) != null) {
                Map<String, String> playerData = parseLine(line);
                String name = playerData.get("name");
                String userid = playerData.get("userid");
                int rank = Integer.parseInt(playerData.get("rank"));
                int wins = Integer.parseInt(playerData.get("wins"));
                int losses = Integer.parseInt(playerData.get("losses"));
                
                double winRate = 0.0;
                int totalGames = wins + losses;
                if (totalGames > 0) {
                    winRate = (double) wins / totalGames * 100;
                }
                
                System.out.printf("%-15s %-20s %-8d %-6d %-8d %.1f%%%n", 
                                name, userid, rank, wins, losses, winRate);
                hasPlayers = true;
            }
            
            if (!hasPlayers) {
                System.out.println("No players registered yet.");
            }
            
        } catch (IOException e) {
            System.out.println("No players file found or error reading file.");
        }
    }
    
    // Helper method to parse a line from players.txt
    private static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = line.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
    
    // Update existing player data in file
    public static void updatePlayerInFile(player playerToUpdate) {
        List<String> updatedRecords = new ArrayList<>();
        boolean playerFound = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> playerData = parseLine(line);
                if (playerData.get("userid").equals(playerToUpdate.getUserId())) {
                    // Update this player's record
                    String updatedLine = String.format("name:%s,userid:%s,rank:%d,wins:%d,losses:%d",
                            playerToUpdate.getName(),
                            playerToUpdate.getUserId(),
                            playerToUpdate.getRank(),
                            playerToUpdate.getWins(),
                            playerToUpdate.getLosses());
                    updatedRecords.add(updatedLine);
                    playerFound = true;
                } else {
                    updatedRecords.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }
        
        // If player wasn't found in file, add them
        if (!playerFound) {
            String newLine = String.format("name:%s,userid:%s,rank:%d,wins:%d,losses:%d",
                    playerToUpdate.getName(),
                    playerToUpdate.getUserId(),
                    playerToUpdate.getRank(),
                    playerToUpdate.getWins(),
                    playerToUpdate.getLosses());
            updatedRecords.add(newLine);
        }
        
        // Write updated data back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("players.txt"))) {
            for (String record : updatedRecords) {
                writer.write(record + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}