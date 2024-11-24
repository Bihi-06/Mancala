
import java.util.Scanner;
import java.io.*;

public class MancalaGame {
    public static void main(String[] args) {
        Mancala game = null;
        AlphaBetaAI ai = new AlphaBetaAI();
        Scanner scanner = new Scanner(System.in);

        int maxHints = 3; // Nombre maximal d'aides
        int hintsUsedPlayer1 = 0; // nombre de hint utilisées par le joueur 1
        int hintsUsedPlayer2 = 0; // nombre de hint utilisées par le joueur 2


        System.out.println("Bienvenue au jeu de Mancala !");
        System.out.println("1. Nouvelle partie");
        System.out.println("2. Charger une partie");
        int choice = 0;
        while (true) {
            System.out.print("Entrez votre choix (1 ou 2) : ");
            choice = scanner.nextInt();
            if (choice == 1 || choice == 2) break;
            System.out.println("Choix invalide, veuillez réessayer.");
        }

        if (choice == 2) {
            try {
                game = new Mancala();
                int[] hints = game.loadGame("savefile.txt");
                hintsUsedPlayer1 = hints[0];
                hintsUsedPlayer2 = hints[1];
                maxHints = hints[2];
                System.out.println("Partie chargée avec succès !");
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de la partie. Une nouvelle partie sera commencée.");
                game = new Mancala();
            }
        } else {
            game = new Mancala();
        }

        System.out.println("Choisissez le mode de jeu: ");
        System.out.println("1. Humain vs AI");
        System.out.println("2. Humain vs Humain");

        int mode = 1; // Valeur par défaut
        while (true){
            System.out.println("Entrez votre choix (1 ou 2) :");
            mode = scanner.nextInt();
            if (mode == 1 || mode == 2) break;
            System.out.println("Choix invalide, veuillez réssayer!");
        }

        int aiDepth = 0;
        if (mode == 1) { //Paramétrage du mode Humain vs AI
            System.out.println("Choisissez la difficulté du jeu :");
            System.out.println("1. Facile (profondeur 2)");
            System.out.println("2. Moyen (profondeur 4)");
            System.out.println("3. Difficile (profondeur 8)");

            int difficulty = 2; // Valeur par défaut
            while (true) {
                System.out.print("Entrez votre choix (1, 2 ou 3) : ");
                difficulty = scanner.nextInt();
                if (difficulty == 1 || difficulty == 2 || difficulty == 3) break;
                System.out.println("Choix invalide, veuillez réessayer.");
            }

             aiDepth = switch (difficulty) {
                case 1 -> 2; // Facile
                case 2 -> 4; // Moyen
                case 3 -> 8; // Difficile
                default -> 4; // Par défaut (moyen)
            };
        }

        game.printBoard();

        while (!game.isGameOver()) {
            if (game.isPlayerTurn()) {
                System.out.println("C'est le tour du joueur 1 ! Sélectionnez un trou (0 à 5), demandez de l'aide (-1) ou sauvegardez la partie (-2) :");
                int playerMove = getPlayerMove(scanner, game, hintsUsedPlayer1, maxHints, true);

                if (playerMove == -1) {
                    if (hintsUsedPlayer1 < maxHints) {
                        int hint = ai.getBestMove(game.getBoard(), true,5);
                        System.out.println("Conseil: jouez le trou " + hint);
                        hintsUsedPlayer1++;
                        System.out.println("il vous reste " + (maxHints - hintsUsedPlayer1) + " aide(s).");
                    } else {
                        System.out.println("Vous avez utilisé toutes vos aides !");
                    }
                } else if (playerMove == -2) {
                    saveGame(game, hintsUsedPlayer1, hintsUsedPlayer2, maxHints);
                } else {
                    game.makeMove(playerMove, true);
                }
            } else if (mode == 2) { // Humain vs Humain
                System.out.println("C'est le tour du joueur 2 ! Sélectionnez un trou (7 à 12),demandez de l'aide (-1) ou sauvegardez la partie (-2) :");
                int playerMove = getPlayerMove(scanner, game, hintsUsedPlayer2, maxHints, false);

                if (playerMove == -1) {
                    if (hintsUsedPlayer2 < maxHints) {
                        int hint = ai.getBestMove(game.getBoard(), false,5);
                        System.out.println("Conseil : jouez le trou " + hint);
                        hintsUsedPlayer2++;
                        System.out.println("Il vous reste " + (maxHints - hintsUsedPlayer2) + " aide(s).");
                    } else {
                        System.out.println("Vous avez utilisé toutes vos aides !");
                    }
                } else if (playerMove == -2) {
                    saveGame(game, hintsUsedPlayer1, hintsUsedPlayer2, maxHints);
                } else {
                    game.makeMove(playerMove, false);
                }

            } else { // Mode Humain vs AI
                System.out.println("C'est le tour de l'AI...");
                int bestMove = ai.getBestMove(game.getBoard(),false ,aiDepth);
                System.out.println("L'AI joue le trou : " + bestMove);
                game.makeMove(bestMove,false);
            }

            game.printBoard();

            if (game.isGameOver()) {
                System.out.println("La partie est terminée !");
                int playerScore = game.getPlayerScore();
                int aiScore = game.getAIScore();
                System.out.println("Score Joueur : " + playerScore);
                System.out.println("Score IA/Joueur 2 : " + aiScore);
                if (playerScore > aiScore) {
                    System.out.println("Vous avez gagné !");
                } else if (aiScore > playerScore) {
                    System.out.println("L'IA a gagné !");
                } else {
                    System.out.println("C'est une égalité !");
                }
                break;
            }
        }

        scanner.close();
    }

    private static int getPlayerMove(Scanner scanner,Mancala game, int hintsUsed ,int maxHints, boolean isPlayer){
        int move;
        while (true) {
            System.out.println("Entrez votre choix : ");
            move = scanner.nextInt();

            //Vérification des entrées
            if (move == -1 || move == -2 ||
                    (isPlayer && move >= 0 && move <= 5 && game.isValidMove(move, true)) ||
                    (!isPlayer && move >= 7 && move <= 12 && game.isValidMove(move,false))) {
                break;
            }
            System.out.println("Entrez un mouvement valide, -1 pour demander de l'aide ou -2 pour sauvegarder.");
        }
        return move;
    }

    private static void saveGame(Mancala game, int hintsUsedPlayer1, int hintsUsedPlayer2, int maxHints) {
        try {
            game.saveGame("savefile.txt", hintsUsedPlayer1,hintsUsedPlayer2,maxHints);
            System.out.println("Partie sauvegardée !");
        } catch (IOException e){
            System.out.println("Erreur lors de la sauvegarde de la partie.");
        }
    }
}
