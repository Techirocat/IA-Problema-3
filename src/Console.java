import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * For playing in the console.
 */
public class Console {
    private Board board;
    private Scanner sc = new Scanner(System.in);
    private final Random rand = new Random();

    public Console() {
        board = new Board();
    }

    /**
     * Joga um jogo completo, do tabuleiro vazio até ao fim
     *
     * @return o vencedor do jogo, ou Blank em caso de empate
     * @post o jogo terminou
     */
    private Ilayout.ID play () {
        while (true) {
            playMove();
            if (board.isGameOver()) {
                printWinner();
                return board.getWinner();
            }
        }
    }

    /**
     * Mostra o número de vitórias de cada jogador e de empates
     *
     * @param vitoriasX número de jogos ganhos pelo X
     * @param empates número de jogos empatados
     * @param vitoriasO número de jogos ganhos pelo O
     */
    private static void printResultados(int vitoriasX, int empates, int vitoriasO) {
        IO.println("\nX: " + vitoriasX);
        IO.println("Draw: " + empates);
        IO.println("O: " + vitoriasO);
    }


    /**
     * Handle the move to be played
     */
    private void playMove () {
        int position;

        if (board.getTurn() == Ilayout.ID.X) { // Turno do X
            //position=getHumanMove();
            //position = getRandomMove();
            position = XAgent.play(board);
            board.move(position);

        } else { // Turno do O
            //position = XAgent.play(board);
            //position = getRandomMove();
            position = getHumanMove();
            board.move(position);
        }
    }

    private void printGameStatus () {
        IO.println("\n" + board + "\n");
        IO.println(board.getTurn().name() + "'s turn.");
    }

    /**
     * For reading in and interpreting the move that the user types into the console.
     */
    private int getHumanMove() {
        while (true) {
            printGameStatus();
            System.out.print("Index of move: ");

            if (!sc.hasNextInt()) {
                sc.next();
                IO.println("\nInvalid input. Please type a number.");
                continue;
            }
            int move = sc.nextInt();

            if (move < 0 || move >= Ilayout.N * Ilayout.M) {
                IO.println("\nInvalid move.");
                IO.println("\nThe index of the move must be between 0 and "
                        + (Ilayout.N * Ilayout.M - 1) + ", inclusive.");
            } else if (!board.isBlank(move)) {
                IO.println("\nInvalid move.");
                IO.println("\nThe selected index must be blank.");
            } else {
                return move;
            }
        }
    }

    private void printWinner () {
        Ilayout.ID winner = board.getWinner();

        IO.println("\n" + board + "\n");

        if (winner == Ilayout.ID.Blank) {
            IO.println("It's a draw.");
        } else {
            IO.println("Player " + winner.toString() + " wins!");
        }
    }


    /**
     * Jogador aleatório: escolhe uma casa vazia ao acaso
     *
     * @return o índice de uma casa vazia
     * @pre !board.isGameOver()
     */
    private int getRandomMove() {
        List<Integer> livres = new ArrayList<>(board.getAvailableMoves());
        return livres.get(rand.nextInt(livres.size()));
    }

    /**
     * Joga vários jogos seguidos e mostra o tempo médio por jogo
     * e o número de vitórias de cada jogador e de empates
     */
    void main()  {
        final int repetitions = 1;
        long times = 0;
        int vitoriasX = 0, empates = 0, vitoriasO = 0;

        for (int i = 0; i < repetitions; i++) {
            Console game = new Console();
            long startTime = System.currentTimeMillis();

            Ilayout.ID winner = game.play();

            long totalTime = System.currentTimeMillis() - startTime;
            times += totalTime;

            if (winner == Ilayout.ID.X) {
                vitoriasX++;
            } else if (winner == Ilayout.ID.O) {
                vitoriasO++;
            } else {
                empates++;
            }
        }

        IO.println("Av Time: " + times * 1.0f / repetitions + " milisecs");
        printResultados(vitoriasX, empates, vitoriasO);
    }

}