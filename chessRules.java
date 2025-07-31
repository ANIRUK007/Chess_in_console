public class chessRules {
    
    // Basic move validation
    public static boolean isValidMove(chessboard board, String from, String to, boolean isWhiteTurn) {
        int[] fromPos = board.notationToIndices(from);
        int[] toPos = board.notationToIndices(to);
        
        if (fromPos == null || toPos == null) {
            System.out.println("Invalid notation format!");
            return false;
        }
        
        char piece = board.getPiece(fromPos[0], fromPos[1]);
        char targetPiece = board.getPiece(toPos[0], toPos[1]);
        
        // Check if there's a piece at source
        if (piece == '.') {
            System.out.println("No piece at source position!");
            return false;
        }
        
        // Check if player is moving their own piece
        boolean isPieceWhite = board.isWhitePiece(piece);
        if (isWhiteTurn != isPieceWhite) {
            System.out.println("You can only move your own pieces!");
            return false;
        }
        
        // Check if trying to capture own piece
        if (targetPiece != '.') {
            boolean isTargetWhite = board.isWhitePiece(targetPiece);
            if (isWhiteTurn == isTargetWhite) {
                System.out.println("Cannot capture your own piece!");
                return false;
            }
        }
        
        // Basic piece movement validation
        return isValidPieceMove(board, piece, fromPos, toPos);
    }
    
    private static boolean isValidPieceMove(chessboard board, char piece, int[] from, int[] to) {
        int fromRow = from[0], fromCol = from[1];
        int toRow = to[0], toCol = to[1];
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        char pieceType = Character.toLowerCase(piece);
        
        switch (pieceType) {
            case 'p': // Pawn
                return isValidPawnMove(board, from, to, board.isWhitePiece(piece));
            case 'r': // Rook
                return (rowDiff == 0 || colDiff == 0) && isPathClear(board, from, to);
            case 'n': // Knight
                return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
            case 'b': // Bishop
                return (rowDiff == colDiff) && isPathClear(board, from, to);
            case 'q': // Queen
                return ((rowDiff == 0 || colDiff == 0) || (rowDiff == colDiff)) && 
                       isPathClear(board, from, to);
            case 'k': // King
                return rowDiff <= 1 && colDiff <= 1;
            default:
                return false;
        }
    }
    
    private static boolean isValidPawnMove(chessboard board, int[] from, int[] to, boolean isWhite) {
        int fromRow = from[0], fromCol = from[1];
        int toRow = to[0], toCol = to[1];
        char targetPiece = board.getPiece(toRow, toCol);
        
        int direction = isWhite ? 1 : -1; // White moves up (+1), black moves down (-1)
        int startRow = isWhite ? 1 : 6;
        
        // Forward move
        if (fromCol == toCol) {
            if (toRow == fromRow + direction && targetPiece == '.') {
                return true; // One square forward
            }
            if (fromRow == startRow && toRow == fromRow + 2 * direction && targetPiece == '.') {
                return true; // Two squares forward from starting position
            }
        }
        // Diagonal capture
        else if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction) {
            return targetPiece != '.'; // Can only move diagonally to capture
        }
        
        return false;
    }
    
    private static boolean isPathClear(chessboard board, int[] from, int[] to) {
        int fromRow = from[0], fromCol = from[1];
        int toRow = to[0], toCol = to[1];
        
        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);
        
        int currentRow = fromRow + rowStep;
        int currentCol = fromCol + colStep;
        
        while (currentRow != toRow || currentCol != toCol) {
            if (board.getPiece(currentRow, currentCol) != '.') {
                return false; // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }
    
    // Simple check detection
    public static boolean isInCheck(chessboard board, boolean isWhiteKing) {
        int[] kingPos = board.findKing(isWhiteKing);
        if (kingPos == null) return false;
        
        // Check if any opponent piece can attack the king
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board.getPiece(i, j);
                if (piece != '.' && board.isWhitePiece(piece) != isWhiteKing) {
                    if (canAttack(board, new int[]{i, j}, kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean canAttack(chessboard board, int[] from, int[] to) {
        char piece = board.getPiece(from[0], from[1]);
        return isValidPieceMove(board, piece, from, to);
    }
    
    // Simple checkmate detection
    public static boolean isCheckmate(chessboard board, boolean isWhiteKing) {
        return isInCheck(board, isWhiteKing) && !hasValidMoves(board, isWhiteKing);
    }
    
    private static boolean hasValidMoves(chessboard board, boolean isWhite) {
        // Simplified: check if player has any pieces that can move
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board.getPiece(i, j);
                if (piece != '.' && board.isWhitePiece(piece) == isWhite) {
                    // Try moving this piece to adjacent squares
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            if (di == 0 && dj == 0) continue;
                            int newRow = i + di;
                            int newCol = j + dj;
                            if (board.isValidPosition(newRow, newCol)) {
                                // Quick test if this move is potentially valid
                                char targetPiece = board.getPiece(newRow, newCol);
                                if (targetPiece == '.' || board.isWhitePiece(targetPiece) != isWhite) {
                                    return true; // Found at least one possible move
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}