public class chessboard {
    private final char[][] board = new char[8][8];

    public chessboard() {
        initializeBoard();
    }

    private void initializeBoard() {
        String whiteBackRow = "RNBQKBNR";  // Fixed standard chess setup
        String blackBackRow = "rnbqkbnr";  // Lowercase for black pieces

        // Row 0 - White back row
        for (int i = 0; i < 8; i++) board[0][i] = whiteBackRow.charAt(i);

        // Row 1 - White pawns
        for (int i = 0; i < 8; i++) board[1][i] = 'P';

        // Row 6 - Black pawns
        for (int i = 0; i < 8; i++) board[6][i] = 'p';

        // Row 7 - Black back row
        for (int i = 0; i < 8; i++) board[7][i] = blackBackRow.charAt(i);

        // Empty spaces
        for (int i = 2; i < 6; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = '.';
    }

    public void printBoard() {
        System.out.println("\n  a b c d e f g h");
        for (int i = 7; i >= 0; i--) {  // Print from rank 8 to 1
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println((i + 1));
        }
        System.out.println("  a b c d e f g h\n");
    }
    
    // Convert chess notation (e.g., "e4") to array indices
    public int[] notationToIndices(String notation) {
        if (notation == null || notation.length() != 2) return null;
        
        char col = notation.charAt(0);
        char row = notation.charAt(1);
        
        if (col < 'a' || col > 'h' || row < '1' || row > '8') {
            return null;
        }
        
        int rowIndex = row - '1';  // Convert '1' to 0, '2' to 1, etc.
        int colIndex = col - 'a';  // Convert 'a' to 0, 'b' to 1, etc.
        
        return new int[]{rowIndex, colIndex};
    }
    
    // Get piece at position
    public char getPiece(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return ' ';
    }
    
    // Set piece at position
    public void setPiece(int row, int col, char piece) {
        if (isValidPosition(row, col)) {
            board[row][col] = piece;
        }
    }
    
    // Check if position is valid
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    
    // Check if square is empty
    public boolean isEmpty(int row, int col) {
        return getPiece(row, col) == '.';
    }
    
    // Check if piece is white (uppercase)
    public boolean isWhitePiece(char piece) {
        return piece != '.' && Character.isUpperCase(piece);
    }
    
    // Check if piece is black (lowercase)
    public boolean isBlackPiece(char piece) {
        return piece != '.' && Character.isLowerCase(piece);
    }
    
    // Make a move on the board
    public boolean makeMove(String from, String to) {
        int[] fromPos = notationToIndices(from);
        int[] toPos = notationToIndices(to);
        
        if (fromPos == null || toPos == null) {
            return false;
        }
        
        char piece = getPiece(fromPos[0], fromPos[1]);
        if (piece == '.') {
            return false; // No piece at source
        }
        
        // Move the piece
        setPiece(toPos[0], toPos[1], piece);
        setPiece(fromPos[0], fromPos[1], '.');
        
        return true;
    }
    
    // Check if king is still on board
    public boolean hasKing(boolean isWhite) {
        char kingSymbol = isWhite ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == kingSymbol) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Find king position
    public int[] findKing(boolean isWhite) {
        char kingSymbol = isWhite ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == kingSymbol) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    
    // Count pieces for a player
    public int countPieces(boolean isWhite) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece != '.' && isWhitePiece(piece) == isWhite) {
                    count++;
                }
            }
        }
        return count;
    }
}