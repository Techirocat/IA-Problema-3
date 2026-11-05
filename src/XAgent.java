/**
 * Agente que joga Jogo do Galo usando Monte Carlo Tree Search (MCTS)
 */
public class XAgent {

	/**
     * Escolhe a melhor jogada para o estado atual do tabuleiro usando MCTS
     *
     * @param board o tabuleiro onde jogar
     * @return o índice da casa onde jogar
     */
	static int play(Ilayout board) {
        return mctsSearch(board, 5000);
    }

    /**
     * Executa o algoritmo MCTS a partir de um estado inicial
     * Em cada iteração são executadas as quatro fases do algoritmo:
     * Seleção, Expansão, Simulação e Retropropagação
     * No fim, é escolhida a jogada do filho da raiz com mais visitas
     *
     * @param rootState estado atual do jogo
     * @param interactions número de iterações a executar
     * @return o índice da melhor jogada encontrada
     */
    static int mctsSearch(Ilayout rootState, int interactions) {
        MCTSNode root = new MCTSNode(rootState, null, -1, Ilayout.ID.X);

        for(int i = 0; i < interactions; i++){
            MCTSNode node = root;

            // Seleção
            while(!node.is_terminal() && node.is_fully_expanded()){
                node = node.best_child();
            }

            // Expansão
            if(!node.is_terminal() && !node.is_fully_expanded()){
                node = node.expand();
            }

            // Simulação
            Ilayout.ID winner = node.rollout();

            // Backpropagation
            node.backPropagate(winner);
        }

        MCTSNode best = root.getChildren().getFirst();
        for(MCTSNode child : root.getChildren()){
            if(child.getVisits() > best.getVisits()){
                best = child;
            }
        }

        return best.getMove();
    }
}
