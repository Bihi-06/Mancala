
import java.util.ArrayList;
import java.util.List;

public class AlphaBetaAI {

    // Fonction principale de l'algorithme Alpha-Beta
    private int alphaBeta(int[] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || isGameOver(board)) {
            return evaluate(board, isMaximizingPlayer); // Heuristique d'évaluation
        }

        int start = isMaximizingPlayer ? 0 : 7;
        int end = isMaximizingPlayer ? 5 : 12;

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = start; i <= end; i++) {
                if (board[i] > 0) {
                    int[] newBoard = board.clone();
                    makeMove(newBoard, i, true);
                    int eval = alphaBeta(newBoard, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) break; // Coupure Alpha-Beta
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = start; i <= end; i++) {
                if (board[i] > 0) {
                    int[] newBoard = board.clone();
                    makeMove(newBoard, i, false);
                    int eval = alphaBeta(newBoard, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) break; // Coupure Alpha-Beta
                }
            }
            return minEval;
        }
    }

    // Fonction d'évaluation : Différence des graines dans les magasins
    private int evaluate(int[] board, boolean isPlayer1) {
        int player1Store = board[6]; // Grenier du joueur 1
        int player2Store = board[13]; // Grenier du joueur 2

        // Somme des graines sur les côtés respectifs
        int player1Side = 0;
        int player2Side = 0;

        for (int i = 0; i < 6; i++) {
            player1Side += board[i]; // Trous du joueur 1
        }
        for (int i = 7; i < 13; i++) {
            player2Side += board[i]; // Trous du joueur 2
        }

        // Évaluation différenciée pour chaque joueur
        if (isPlayer1) {
            return (player1Store - player2Store) // Avantage des greniers
                    + player1Side               // Graines restantes pour Joueur 1
                    - player2Side;              // Graines restantes pour Joueur 2
        } else {
            return (player2Store - player1Store) // Avantage des greniers
                    + player2Side               // Graines restantes pour Joueur 2
                    - player1Side;              // Graines restantes pour Joueur 1
        }
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
    public int getBestMove(int[] board, boolean isPlayer1, int depth) {
        int start = isPlayer1 ? 0 : 7;
        int end = isPlayer1 ? 5 : 12;

        int bestMove = -1;
        int bestValue = isPlayer1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = start; i <= end; i++) {
            if (board[i] > 0) { // Vérifie que le trou n'est pas vide
                int[] newBoard = board.clone(); // Clone l'état du plateau
                makeMove(newBoard, i, isPlayer1); // Simule le coup

                // Évalue le coup avec Alpha-Beta
                int value = alphaBeta(newBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isPlayer1);

                // Mise à jour du meilleur mouvement selon le joueur
                if (isPlayer1 && value > bestValue) {
                    bestValue = value;
                    bestMove = i;
                } else if (!isPlayer1 && value < bestValue) {
                    bestValue = value;
                    bestMove = i;
                }
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


