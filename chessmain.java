import java.util.Scanner;

public class chessmain {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("Welcome to Chess Game!");
        System.out.println("=====================");
        
        while (true) {
            showMainMenu();
            int choice = getChoice();
            
            switch (choice) {
                case 1:
                    createNewUser();
                    break;
                case 2:
                    startGame();
                    break;
                case 3:
                    viewAllPlayers();
                    break;
                case 4:
                    System.out.println("Thank you for playing!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(30));
        System.out.println("1. Create New User");
        System.out.println("2. Start Game");
        System.out.println("3. View All Players");
        System.out.println("4. Exit");
        System.out.print("\nEnter your choice (1-4): ");
    }
    
    private static int getChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }
    
    private static void createNewUser() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("CREATE NEW USER");
        System.out.println("=".repeat(30));
        
        System.out.print("Enter player name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty!");
            return;
        }
        
        if (playermanager.playerExists(name)) {
            System.out.println("Player with name '" + name + "' already exists!");
            System.out.print("Do you want to view existing player details? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            
            if (choice.equals("y") || choice.equals("yes")) {
                player existingPlayer = playermanager.loadPlayer(name);
                if (existingPlayer != null) {
                    displayPlayerInfo(existingPlayer);
                }
            }
            return;
        }
        
        // Create new player
        player newPlayer = new player(name);
        System.out.println("\nNew player created successfully!");
        displayPlayerInfo(newPlayer);
    }
    
    private static void startGame() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("START GAME");
        System.out.println("=".repeat(30));
        
        // Get Player 1
        System.out.print("Enter Player 1 name (White): ");
        String p1Name = scanner.nextLine().trim();
        
        if (p1Name.isEmpty()) {
            System.out.println("Player name cannot be empty!");
            return;
        }
        
        player player1 = getPlayerForGame(p1Name, "Player 1");
        if (player1 == null) return;
        
        // Get Player 2
        System.out.print("Enter Player 2 name (Black): ");
        String p2Name = scanner.nextLine().trim();
        
        if (p2Name.isEmpty()) {
            System.out.println("Player name cannot be empty!");
            return;
        }
        
        if (p2Name.equals(p1Name)) {
            System.out.println("Player 2 must be different from Player 1!");
            return;
        }
        
        player player2 = getPlayerForGame(p2Name, "Player 2");
        if (player2 == null) return;
        
        // Start the game
        System.out.println("\nStarting game...");
        System.out.println("Player 1 (White): " + player1.getName() + " - Rank: " + player1.getRank());
        System.out.println("Player 2 (Black): " + player2.getName() + " - Rank: " + player2.getRank());
        
        chessGame game = new chessGame(player1, player2);
    }
    
    private static player getPlayerForGame(String name, String playerLabel) {
        if (playermanager.playerExists(name)) {
            // Player exists, load them
            player existingPlayer = playermanager.loadPlayer(name);
            if (existingPlayer != null) {
                System.out.println("Loaded existing " + playerLabel + ": " + name);
                System.out.println("Stats - Rank: " + existingPlayer.getRank() + 
                                 ", Wins: " + existingPlayer.getWins() + 
                                 ", Losses: " + existingPlayer.getLosses());
                return existingPlayer;
            }
        } else {
            // Player doesn't exist, ask if they want to create
            System.out.println("Player '" + name + "' not found.");
            System.out.print("Do you want to create this player? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            
            if (choice.equals("y") || choice.equals("yes")) {
                player newPlayer = new player(name);
                System.out.println("Created new " + playerLabel + ": " + name);
                return newPlayer;
            } else {
                System.out.println("Cannot start game without " + playerLabel + ".");
                return null;
            }
        }
        
        System.out.println("Error loading " + playerLabel + ".");
        return null;
    }
    
    private static void viewAllPlayers() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ALL REGISTERED PLAYERS");
        System.out.println("=".repeat(50));
        
        playermanager.displayAllPlayers();
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void displayPlayerInfo(player p) {
        System.out.println("Player Details:");
        System.out.println("Name: " + p.getName());
        System.out.println("User ID: " + p.getUserId());
        System.out.println("Rank: " + p.getRank());
        System.out.println("Wins: " + p.getWins());
        System.out.println("Losses: " + p.getLosses());
        System.out.println("Win Rate: " + calculateWinRate(p) + "%");
    }
    
    private static double calculateWinRate(player p) {
        int totalGames = p.getWins() + p.getLosses();
        if (totalGames == 0) return 0.0;
        return (double) p.getWins() / totalGames * 100;
    }
}