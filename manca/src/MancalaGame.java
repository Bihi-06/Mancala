
import java.util.Scanner;
import java.io.*;

public class MancalaGame {
    public static void main(String[] args) {
        Mancala game = null;
        AlphaBetaAI ai = new AlphaBetaAI();
        Scanner scanner = new Scanner(System.in);

        int maxHints = 3; // Nombre maximal d'aides
        int hintsUsed = 0; // nombre de hint utilise

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
                hintsUsed = hints[0];
                maxHints = hints[1];
                System.out.println("Partie chargée avec succès !");
            } catch (IOException e) {
                System.out.println("Erreur lors du chargement de la partie. Une nouvelle partie sera commencée.");
                game = new Mancala();
            }
        } else {
            game = new Mancala();
        }

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

        int aiDepth = switch (difficulty) {
            case 1 -> 2; // Facile
            case 2 -> 4; // Moyen
            case 3 -> 8; // Difficile
            default -> 4; // Par défaut (moyen)
        };

        game.printBoard();

        while (!game.isGameOver()) {
            if (game.isPlayerTurn()) {
                System.out.println("C'est votre tour ! Sélectionnez un trou (0 à 5), demandez de l'aide (-1) ou sauvegardez la partie (-2) :");

                int playerMove = -3;
                while (true) {
                    System.out.print("Entrez votre choix : ");
                    playerMove = scanner.nextInt();

                    if (playerMove == -1) {
                        if (hintsUsed < maxHints) {
                            int hint = ai.getBestMove(game.getBoard(), 8 ); // Utilise l'IA pour suggérer le meilleur coup
                            System.out.println("Conseil de l'IA : jouez le trou " + hint);
                            hintsUsed++;
                            System.out.println("Il vous reste " + (maxHints - hintsUsed) + " aide(s).");
                        } else {
                            System.out.println("Vous avez utilisé toutes vos aides !");
                        }
                    } else if (playerMove == -2) {
                        try {
                            game.saveGame("savefile.txt", hintsUsed, maxHints);
                            System.out.println("Partie sauvegardée !");
                        } catch (IOException e) {
                            System.out.println("Erreur lors de la sauvegarde de la partie.");
                        }
                    } else if (playerMove >= 0 && playerMove <= 5 && game.isValidMove(playerMove, true)) {
                        break; // Mouvement valide
                    } else {
                        System.out.println("Entrez un mouvement valide, -1 pour demander de l'aide ou -2 pour sauvegarder.");
                    }
                }

                if (playerMove >= 0 && playerMove <= 5) {
                    game.makeMove(playerMove, true); // Le joueur joue son tour
                }
            }



            else {
                System.out.println("C'est le tour de l'IA...");
                int bestMove = ai.getBestMove(game.getBoard(), aiDepth); // Utilise la profondeur choisie
                System.out.println("L'IA joue le trou : " + bestMove);
                game.makeMove(bestMove, false); // L'IA joue son tour
            }






            game.printBoard();

            if (game.isGameOver()) {
                System.out.println("La partie est terminée !");
                int playerScore = game.getPlayerScore();
                int aiScore = game.getAIScore();
                System.out.println("Score Joueur : " + playerScore);
                System.out.println("Score IA : " + aiScore);
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
}
