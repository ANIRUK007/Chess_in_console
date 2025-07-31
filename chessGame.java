import java.util.Scanner;

public class chessGame {
    private player player1Obj, player2Obj;
    private chessboard board;
    private Scanner s = new Scanner(System.in);
    private boolean gameEnded = false;
    private player winner = null;
    private player loser = null;
    
    public chessGame(player p1, player p2) {
        this.board = new chessboard();
        this.player1Obj = p1;
        this.player2Obj = p2;
        
        startGame();
    }
    
    public void handleWin(player winner) {
        System.out.println("\nGAME OVER!");
        System.out.println("Winner is: " + winner.getName() + " (" + winner.getUserId() + ")");
        
        // Update winner's stats
        winner.setWins(winner.getWins() + 1);
        System.out.println(winner.getName() + " now has " + winner.getWins() + " wins!");
        
        // Update in file
        winner.updateInFile();
    }

    public void handleLoss(player loser) {
        System.out.println("Loser is: " + loser.getName() + " (" + loser.getUserId() + ")");
        
        // Update loser's stats
        loser.setLosses(loser.getLosses() + 1);
        System.out.println(loser.getName() + " now has " + loser.getLosses() + " losses.");
        
        // Update in file
        loser.updateInFile();
    }

    public void startGame() {
        System.out.println("Starting chess game between " + player1Obj.getName() + " (White) and " + player2Obj.getName() + " (Black)");
        board.printBoard();
        
        boolean turn = true; // true = player1 (White), false = player2 (Black)
        int moveCount = 0;
        
        while (!gameEnded) {
            player currentPlayerObj = turn ? player1Obj : player2Obj;
            String currentPlayerName = currentPlayerObj.getName();
            String colorName = turn ? "White" : "Black";
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Move " + (++moveCount) + " - " + currentPlayerName + "'s turn (" + colorName + ")");
            
            // Check for check before move
            if (chessRules.isInCheck(board, turn)) {
                System.out.println("CHECK! " + currentPlayerName + "'s king is in danger!");
                
                // Check for checkmate
                if (chessRules.isCheckmate(board, turn)) {
                    System.out.println("CHECKMATE!");
                    winner = turn ? player2Obj : player1Obj;  // Opponent wins
                    loser = currentPlayerObj;
                    gameEnded = true;
                    break;
                }
            }
            
            boolean validMove = false;
            while (!validMove && !gameEnded) {
                System.out.println("\n" + currentPlayerName + ", enter your move:");
                System.out.print("From square (e.g., e2) or 'resign': ");
                String curr = s.nextLine().trim().toLowerCase();
                
                // Check for resignation
                if (curr.equals("resign") || curr.equals("quit")) {
                    winner = turn ? player2Obj : player1Obj;  // Opponent wins
                    loser = currentPlayerObj;
                    System.out.println(currentPlayerName + " resigned!");
                    gameEnded = true;
                    break;
                }
                
                System.out.print("To square (e.g., e4): ");
                String dest = s.nextLine().trim().toLowerCase();
                
                if (safeState(board, curr, dest, turn)) {
                    makeMove(board, curr, dest);
                    
                    // Check win conditions after move
                    if (checkWinConditions(turn)) {
                        winner = currentPlayerObj;
                        loser = turn ? player2Obj : player1Obj;
                        gameEnded = true;
                        break;
                    }
                    
                    board.printBoard();
                    validMove = true;
                    turn = !turn;  // Switch turns
                } else {
                    System.out.println("Invalid move! Please try again.");
                    System.out.println("Use format like 'e2' to 'e4', or type 'resign' to forfeit");
                }
            }
            
            // Draw condition - simplified (100 moves without progress)
            if (moveCount > 100) {
                System.out.println("Draw! Game has exceeded move limit.");
                gameEnded = true;
            }
        }
        
        // Handle game end
        if (winner != null && loser != null) {
            handleWin(winner);
            handleLoss(loser);
            
            // Update rankings
            ranking.updateRanking(winner.getUserId(), loser.getUserId());
        } else {
            System.out.println("Game ended in a draw!");
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Final Board:");
        board.printBoard();
        System.out.println("Thank you for playing!");
        
        // Return to main menu
        System.out.println("\nPress Enter to return to main menu...");
        s.nextLine();
    }
    
    // Check various win conditions
    private boolean checkWinConditions(boolean playerJustMoved) {
        boolean opponentColor = !playerJustMoved;
        
        // 1. King captured
        if (!board.hasKing(opponentColor)) {
            System.out.println("King captured! Victory!");
            return true;
        }
        
        // 2. Checkmate (opponent king in check with no moves)
        if (chessRules.isCheckmate(board, opponentColor)) {
            System.out.println("Checkmate! Victory!");
            return true;
        }
        
        // 3. Opponent has very few pieces left (simplified condition)
        int opponentPieces = board.countPieces(opponentColor);
        if (opponentPieces <= 2) { // Only king and maybe one piece left
            System.out.println("Opponent has insufficient pieces! Victory!");
            return true;
        }
        
        return false;
    }

    // Enhanced safeState method
    public boolean safeState(chessboard board, String curr, String dest, boolean isWhiteTurn) {
        // Basic validation
        if (curr == null || dest == null || curr.isEmpty() || dest.isEmpty()) {
            System.out.println("Invalid input format!");
            return false;
        }
        
        // Check if move is valid according to chess rules
        if (!chessRules.isValidMove(board, curr, dest, isWhiteTurn)) {
            return false;
        }
        
        // Additional safety check: don't allow moves that put own king in check
        return simulateMoveSafety(board, curr, dest, isWhiteTurn);
    }
    
    private boolean simulateMoveSafety(chessboard board, String curr, String dest, boolean isWhiteTurn) {
        int[] fromPos = board.notationToIndices(curr);
        int[] toPos = board.notationToIndices(dest);
        
        if (fromPos == null || toPos == null) return false;
        
        // Save original state
        char originalPiece = board.getPiece(fromPos[0], fromPos[1]);
        char originalTarget = board.getPiece(toPos[0], toPos[1]);
        
        // Make temporary move
        board.setPiece(toPos[0], toPos[1], originalPiece);
        board.setPiece(fromPos[0], fromPos[1], '.');
        
        // Check if this puts own king in check
        boolean putsKingInCheck = chessRules.isInCheck(board, isWhiteTurn);
        
        // Restore original state
        board.setPiece(fromPos[0], fromPos[1], originalPiece);
        board.setPiece(toPos[0], toPos[1], originalTarget);
        
        if (putsKingInCheck) {
            System.out.println("That move would put your king in check!");
            return false;
        }
        
        return true;
    }

    public void makeMove(chessboard board, String curr, String dest) {
        int[] fromPos = board.notationToIndices(curr);
        int[] toPos = board.notationToIndices(dest);
        
        char piece = board.getPiece(fromPos[0], fromPos[1]);
        char capturedPiece = board.getPiece(toPos[0], toPos[1]);
        
        // Show move notation
        String moveNotation = curr + " -> " + dest;
        if (capturedPiece != '.') {
            moveNotation += " (captures " + capturedPiece + ")";
        }
        System.out.println("Move: " + moveNotation);
        
        // Execute the move
        board.makeMove(curr, dest);
    }
    
    // Getters
    public player getWinner() { return winner; }
    public player getLoser() { return loser; }
    public boolean isGameEnded() { return gameEnded; }
}
