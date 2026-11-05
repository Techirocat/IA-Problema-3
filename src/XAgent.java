/**
 * Agente que joga Jogo do Galo usando Monte Carlo Tree Search (MCTS)
 */
public class XAgent {

    private static final int interacoes = 5000;

	/**
     * Escolhe a melhor jogada para o estado atual do tabuleiro usando MCTS
     *
     * @param board o tabuleiro onde jogar
     * @return o índice da casa onde jogar
     */
	static int play(Ilayout board) {
        return new MCTS(interacoes).search(board);
    }
}
