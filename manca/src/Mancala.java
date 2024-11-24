import java.io.*;

public class Mancala {
    private int[] board; // Tableau représentant les trous et magasins.
    private int playerStore; // Index du magasin du joueur.
    private int aiStore;     // Index du magasin de l'IA.
    private boolean isPlayerTurn; // Indique si c'est le tour du joueur.

    // constructer
    public Mancala() {
        // Initialisation : 6 trous par côté + 2 magasins
        board = new int[14];
        for (int i = 0; i < 6; i++) {
            board[i] = 4;        // 4 graines par trou pour le joueur
            board[i + 7] = 4;    // 4 graines par trou pour l'IA
        }
        playerStore = 6; // Magasin du joueur
        aiStore = 13;    // Magasin de l'IA
        isPlayerTurn = true; // Le joueur commence
    }

    // Affiche le plateau
    public void printBoard() {
        System.out.println("-------------------------------------------------------------------" );
        System.out.println("IA:   " + board[12] + " " + board[11] + " " + board[10] + " " + board[9] + " " + board[8] + " " + board[7]);
        System.out.println("Magasin IA: " + board[aiStore] + "              Magasin Joueur: " + board[playerStore]);
        System.out.println("Joueur: " + board[0] + " " + board[1] + " " + board[2] + " " + board[3] + " " + board[4] + " " + board[5]);
        System.out.println("-------------------------------------------------------------------" );

    }

    // Vérifie si un mouvement est valide
    public boolean isValidMove(int move, boolean isPlayer) {
        if (isPlayer && move >= 0 && move < 6) {
            return board[move] > 0;
        } else if (!isPlayer && move >= 7 && move < 13) {
            return board[move] > 0;
        }
        return false;
    }

    // Effectue un mouvement
    public void makeMove(int move, boolean isPlayer) {
        int seeds = board[move];
        board[move] = 0;
        int currentIndex = move;

        // Distribuer les graines
        while (seeds > 0) {
            currentIndex = (currentIndex + 1) % 14;

            // Sauter le magasin adverse
            if (isPlayer && currentIndex == aiStore) continue;
            if (!isPlayer && currentIndex == playerStore) continue;


            board[currentIndex]++;
            seeds--;
        }

        // Capture des graines si la dernière tombe dans un trou vide
        if (isPlayer && currentIndex >= 0 && currentIndex < 6 && board[currentIndex] == 1) {
            int oppositeIndex = 12 - currentIndex;
            if (board[oppositeIndex] > 0) {
                board[playerStore] += board[oppositeIndex] + 1;
                board[oppositeIndex] = 0;
                board[currentIndex] = 0;
            }
        } else if (!isPlayer && currentIndex >= 7 && currentIndex < 13 && board[currentIndex] == 1) {
            int oppositeIndex = 12 - currentIndex;
            if (board[oppositeIndex] > 0) {
                board[aiStore] += board[oppositeIndex] + 1;
                board[oppositeIndex] = 0;
                board[currentIndex] = 0;
            }
        }

        // Vérifiez si le joueur peut rejouer
        if ((isPlayer && currentIndex == playerStore) || (!isPlayer && currentIndex == aiStore)) {
            // Le joueur garde son tour
            isPlayerTurn = isPlayer;
        } else {
            // Sinon, changer de tour
            isPlayerTurn = !isPlayer;
        }
    }




    // Vérifie si la partie est terminée
    public boolean isGameOver() {
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

        // Si un des côtés est vide, la partie est terminée
        return playerSideEmpty || aiSideEmpty;
    }

    // Gère les graines restantes lorsque la partie se termine
    public void finalizeGame() {
        for (int i = 0; i < 6; i++) {
            board[playerStore] += board[i];
            board[i] = 0;
        }

        for (int i = 7; i < 13; i++) {
            board[aiStore] += board[i];
            board[i] = 0;
        }
    }

    // Retourne le score du joueur
    public int getPlayerScore() {
        return board[playerStore];
    }

    // Retourne le score de l'IA
    public int getAIScore() {
        return board[aiStore];
    }

    // Retourne une copie de l'état actuel du plateau
    public int[] getBoard() {
        return board.clone();
    }

    // Retourne si c'est le tour du joueur
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }



    public void saveGame(String filename, int hintsUsedPlayer1, int hintsUsedPlayer2, int maxHints) throws IOException {
        FileWriter writer = new FileWriter(filename);

        // Sauvegarder l'état du plateau
        for (int i = 0; i < board.length; i++) {
            writer.write(board[i] + " ");
        }
        writer.write("\n");

        // Sauvegarder les aides et le nombre maximal d'aides
        writer.write(hintsUsedPlayer1 + " " + hintsUsedPlayer2 + " " + maxHints + "\n");

        // Sauvegarder l'état du joueur actif
        writer.write((isPlayerTurn ? "1" : "0") + "\n");

        writer.close();
    }


    public int[] loadGame(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // Charger l'état du plateau
        String[] boardState = reader.readLine().split(" ");
        for (int i = 0; i < boardState.length; i++) {
            board[i] = Integer.parseInt(boardState[i]);
        }

        // Charger les aides et le nombre maximal d'aides
        String[] hintsState = reader.readLine().split(" ");
        int hintsUsedPlayer1 = Integer.parseInt(hintsState[0]);
        int hintsUsedPlayer2 = Integer.parseInt(hintsState[1]);
        int maxHints = Integer.parseInt(hintsState[2]);

        // Charger l'état du joueur actif
        isPlayerTurn = reader.readLine().equals("1");

        reader.close();

        return new int[]{hintsUsedPlayer1, hintsUsedPlayer2, maxHints};
    }


}



