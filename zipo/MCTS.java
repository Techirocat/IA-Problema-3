/**
 * Algoritmo do Monte Carlo Tree Search (MCTS)
 * Repete as fases de seleção, expansão, simulação e retropropagação
 * e devolve a jogada com mais simulações
 */
public class MCTS {

    private final int interacoes;
    private final double C;

    /**
     * Cria um Algoritmo MCTS
     *
     * @param iteracoes número de iterações a executar por pesquisa
     *
     * @pre iteracoes > 0
     */
    public MCTS(int iteracoes, double C) {
        this.interacoes = iteracoes;
        this.C = C;
    }

    /**
     * Executa o MCTS a partir de um estado e devolve a melhor jogada
     *
     * @param state estado atual do jogo
     *
     * @return o índice da jogada cujo nó teve mais simulações
     * @pre state != null e !state.isGameOver()
     * @post state não é alterado; o valor devolvido pertence a state.getAvailableMoves()
     */
    public int search(Ilayout state){
        if(state.isGameOver()){
            throw new IllegalStateException("O jogo já terminou: não há jogadas possíveis.");
        }

        MCTSNode root = new MCTSNode(state, null, -1, state.getTurn(), C);

        for(int i = 0; i < this.interacoes; i++){
            MCTSNode node = select(root);

            if(!node.is_terminal() && !node.is_fully_expanded()){
                node = node.expand();
            }

            Ilayout.ID winner = node.rollout();
            node.backPropagate(winner);
        }

        return mostVisited(root).getMove();
    }

    /**
     * Fase de seleção: a partir de um nó, desce na árvore escolhendo
     * até chegar a um nó terminal ou a um nó com jogadas ainda por expandir
     *
     * @param node nó onde começa a descida
     * @return o nó selecionado
     */
    private MCTSNode select(MCTSNode node){
        while(!node.is_terminal() && node.is_fully_expanded()){
            node = node.best_child();
        }

        return node;
    }

    /**
     * Devolve o filho da raiz com mais simulações
     *
     * @param root raiz da árvore de pesquisa
     * @return o filho da raiz mais visitado
     */
    private MCTSNode mostVisited(MCTSNode root){
        MCTSNode best = root.getChildren().getFirst();
        for(MCTSNode child : root.getChildren()){
            if(child.getVisits() > best.getVisits()){
                best = child;
            }
        }

        return best;
    }
}
