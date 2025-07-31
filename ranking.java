import java.io.*;
import java.util.*;

public class ranking {

    public static int winnerRankCalc(int rankA, int gamesb) {
        return rankA + (int)((rankA * 0.08) / gamesb);
    }

    public static int losserRankCalc(int rankB, int gamesa) {
        return rankB - (int)((rankB * 0.05) / gamesa);
    }

    public static int drawRankCalc(int rankB, int gamesb) {
        return losserRankCalc(rankB, gamesb);
    }

    // Update player records after match
    public static void updateRanking(String winnerId, String loserId) {
        List<String> updatedRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("players.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> record = parseLine(line);
                String userId = record.get("userid");

                if (userId.equals(winnerId)) {
                    int rank = Integer.parseInt(record.get("rank"));
                    int wins = Integer.parseInt(record.get("wins"));
                    int newRank = winnerRankCalc(rank, wins + 1);
                    wins += 1;
                    record.put("rank", String.valueOf(newRank));
                    record.put("wins", String.valueOf(wins));
                } else if (userId.equals(loserId)) {
                    int rank = Integer.parseInt(record.get("rank"));
                    int losses = Integer.parseInt(record.get("losses"));
                    int newRank = losserRankCalc(rank, losses + 1);
                    losses += 1;
                    record.put("rank", String.valueOf(newRank));
                    record.put("losses", String.valueOf(losses));
                }

                updatedRecords.add(buildLine(record));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // Rewrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("players.txt"))) {
            for (String rec : updatedRecords) {
                writer.write(rec + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }

        System.out.println("✅ Player rankings updated!");
    }

    // Helper to parse line into key-value map
    private static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = line.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    // Helper to rebuild line from map
    private static String buildLine(Map<String, String> map) {
        return String.format("name:%s,userid:%s,rank:%s,wins:%s,losses:%s",
                map.get("name"),
                map.get("userid"),
                map.get("rank"),
                map.get("wins"),
                map.get("losses"));
    }
}
