
import java.util.ArrayList;
import java.util.List;

public class AlphaBetaAI {

    // Fonction principale de l'algorithme Alpha-Beta
    public int alphaBeta(int[] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || isGameOver(board)) {
            return evaluate(board); // Évalue l'état du plateau
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int move : getPossibleMoves(board, false)) {
                MoveResult result = makeMove(board, move, false);
                int eval;
                if (result.isExtraTurn) {
                    eval = alphaBeta(result.board, depth - 1, alpha, beta, true); // L'IA rejoue
                } else {
                    eval = alphaBeta(result.board, depth - 1, alpha, beta, false); // Tour du joueur
                }
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Élagage
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int move : getPossibleMoves(board, true)) {
                MoveResult result = makeMove(board, move, true);
                int eval;
                if (result.isExtraTurn) {
                    eval = alphaBeta(result.board, depth - 1, alpha, beta, false); // Le joueur rejoue
                } else {
                    eval = alphaBeta(result.board, depth - 1, alpha, beta, true); // Tour de l'IA
                }
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Élagage
            }
            return minEval;
        }

    }

    // Fonction d'évaluation : Différence des graines dans les magasins
    private int evaluate(int[] board) {
        return board[13] - board[6]; // IA (index 13) - Joueur (index 6)
    }

    // Vérifie si la partie est terminée
    private boolean isGameOver(int[] board) {
        boolean playerSideEmpty = true;
        boolean aiSideEmpty = true;

        for (int i = 0; i < 6; i++) {
            if (board[i] > 0) {
                playerSideEmpty = false;
                break;
            }
        }

        for (int i = 7; i < 13; i++) {
            if (board[i] > 0) {
                aiSideEmpty = false;
                break;
            }
        }

        return playerSideEmpty || aiSideEmpty;
    }

    // Retourne les mouvements possibles
    private int[] getPossibleMoves(int[] board, boolean isAI) {
        List<Integer> possibleMoves = new ArrayList<>();
        if (isAI) { // AI chooses from indices 7 to 12
            for (int i = 7; i < 13; i++) {
                if (board[i] > 0) possibleMoves.add(i);
            }
        } else { // Player chooses from indices 0 to 5
            for (int i = 0; i < 6; i++) {
                if (board[i] > 0) possibleMoves.add(i);
            }
        }
        return possibleMoves.stream().mapToInt(i -> i).toArray();
    }



    // Simule un mouvement et retourne le nouveau plateau
    private MoveResult makeMove(int[] board, int move, boolean isAI) {
        int[] newBoard = board.clone();
        int seeds = newBoard[move];
        newBoard[move] = 0;
        int currentIndex = move;

        while (seeds > 0) {
            currentIndex = (currentIndex + 1) % 14;

            // Sauter le magasin adverse
            if (isAI && currentIndex == 6) continue;
            if (!isAI && currentIndex == 13) continue;

            newBoard[currentIndex]++;
            seeds--;
        }

        // Capture des graines
        if (isAI && currentIndex >= 7 && currentIndex < 13 && newBoard[currentIndex] == 1) {
            int oppositeIndex = 12 - currentIndex;
            if (newBoard[oppositeIndex] > 0) {
                newBoard[13] += newBoard[oppositeIndex] + 1;
                newBoard[oppositeIndex] = 0;
                newBoard[currentIndex] = 0;
            }
        } else if (!isAI && currentIndex >= 0 && currentIndex < 6 && newBoard[currentIndex] == 1) {
            int oppositeIndex = 12 - currentIndex;
            if (newBoard[oppositeIndex] > 0) {
                newBoard[6] += newBoard[oppositeIndex] + 1;
                newBoard[oppositeIndex] = 0;
                newBoard[currentIndex] = 0;
            }
        }

        // Vérifie si le joueur a un tour bonus
        boolean isExtraTurn = (isAI && currentIndex == 13) || (!isAI && currentIndex == 6);

        return new MoveResult(newBoard, isExtraTurn);
    }





    // Trouve le meilleur mouvement pour l'IA
    public int getBestMove(int[] board, int depth) {
        int bestMove = -1;
        int maxEval = Integer.MIN_VALUE;

        // Pass `true` for isAI since this is the AI's turn
        for (int move : getPossibleMoves(board, true)) {
            MoveResult result = makeMove(board, move, true);
            int eval = alphaBeta(result.board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !result.isExtraTurn);
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }

}

class MoveResult {
    int[] board;
    boolean isExtraTurn;

    public MoveResult(int[] board, boolean isExtraTurn) {
        this.board = board;
        this.isExtraTurn = isExtraTurn;
    }
}


