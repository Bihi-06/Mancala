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



    public void saveGame(String fileName, int hintsUsed, int maxHints) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Sauvegarder le plateau
            for (int i = 0; i < board.length; i++) {
                writer.write(board[i] + " ");
            }
            writer.newLine();

            // Sauvegarder le tour actuel
            writer.write(isPlayerTurn + "\n");

            // Sauvegarder le nombre d'aides utilisées et maximales
            writer.write(hintsUsed + " " + maxHints + "\n");
        }
        System.out.println("La partie a été sauvegardée dans le fichier " + fileName);
    }


    public int[] loadGame(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Charger le plateau
            String[] boardData = reader.readLine().split(" ");
            for (int i = 0; i < board.length; i++) {
                board[i] = Integer.parseInt(boardData[i]);
            }

            // Charger le tour actuel
            isPlayerTurn = Boolean.parseBoolean(reader.readLine());

            // Charger les informations sur les aides
            String[] hintData = reader.readLine().split(" ");
            int[] hints = new int[2];
            hints[0] = Integer.parseInt(hintData[0]); // Aides utilisées
            hints[1] = Integer.parseInt(hintData[1]); // Aides maximales

            System.out.println("La partie a été chargée depuis le fichier " + fileName);
            return hints; // Retourne les informations des aides
        }
    }


}
