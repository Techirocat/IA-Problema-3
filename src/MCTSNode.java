/**
 * Ná da árvore de pesquisa do algoritmo Monte Carlo Tree Search (MCTS)
 *
 * Cada nó representa um estado do jogo e guarda as estatísticas recolhidas
 * nas simulações (número de visitas e vitórias acumuladas), usadas para
 * orientar a pesquisa através da fórmula UCB1
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MCTSNode {
    private final Ilayout state;
    private final MCTSNode parent;
    private ArrayList<MCTSNode> children;
    private int visits;
    private double wins;
    private int move;
    private Ilayout.ID maxPlayer;

    private double C = 1.4;
    private static final Random rand = new Random();

    /**
     * Cria um nó da árvore MCTS
     *
     * @param state estado do jogo representado pelo nó
     * @param parent nó pai ou null para a raiz
     * @param move jogada que levou do pai a este nó, ou -1 para a raiz
     */
    MCTSNode(Ilayout state, MCTSNode parent, int move, Ilayout.ID maxPlayer) {
        this.state = state;
        this.parent = parent;
        children = new ArrayList<>();
        visits = 0;
        wins = 0.0;
        this.move = move;
        this.maxPlayer = maxPlayer;
    }

    /**
     * Verifica se o estado deste nó é terminal (alguem ganhou ou é um empate)
     *
     * @return true se o jogo terminou, false caso contrário
     */
    public boolean is_terminal(){
        return state.isGameOver();
    }

    /**
     * Verifica se todas as jogadas possíveis a partir deste estado
     * já foram expandidas como filhos
     *
     * @return true se não existirem jogadas por expandir, false caso contrário
     */
    public boolean is_fully_expanded(){
        return children.size() == state.getAvailableMoves().size();
    }

    /**
     * Indica se este nó é um nó MAX, ou seja, se é o MAX que tem a vez
     *
     * @return true se for um nó MAX, false se for um nó MIN
     */
    public boolean isMaxPlayer(){
        return state.getTurn() == maxPlayer;
    }

    /**
     * Fase de expansão: cria um filho a partir de uma jogada
     * ainda não explorada e adiciona-o à árvore
     *
     * @return o novo nó filho criado
     */
    public MCTSNode expand(){
        HashSet<Integer> actions = state.getAvailableMoves();
        for(MCTSNode child : children){
            actions.remove(child.move);
        }

        Ilayout newState = (Ilayout) state.clone();
        newState.move(actions.iterator().next());
        MCTSNode child = new MCTSNode(newState, this, actions.iterator().next(), this.maxPlayer);

        children.add(child);
        return child;
    }

    /**
     * Fase de seleção: escolhe o filho mais promissor segundo a fórmula UCB1
     * Filhos nunca visitados têm prioridade
     *
     * @return o filho selecionado
     */
    public MCTSNode best_child(){
        for (MCTSNode child : children){
            if (child.visits == 0){
                return child;
            }
        }

        MCTSNode best = null;
        double bestScore = isMaxPlayer() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (MCTSNode child : children){
            double exploit = child.wins / child.visits;
            double explore = C * Math.sqrt(Math.log(this.visits) / child.visits);

            if (isMaxPlayer()){
                double score = exploit + explore;
                if (score > bestScore){
                    bestScore = score;
                    best = child;
                }
            }else{
                double score = exploit - explore;
                if (score < bestScore){
                    bestScore = score;
                    best = child;
                }
            }
        }

        return best;
    }

    /**
     * Fase de simulação: a partir de uma cópia do estado deste nó
     * joga aleatoriamente até o jogo terminar
     *
     * @return o vencedor da simulação, ou "Blank" em caso de empate
     */
    public Ilayout.ID rollout(){
        Ilayout newState = (Ilayout) state.clone();

        while (!newState.isGameOver()){
            ArrayList<Integer> actions = new ArrayList<>(newState.getAvailableMoves());

            int index = actions.get(rand.nextInt(actions.size()));
            newState.move(index);
        }

        return newState.getWinner();
    }

    /**
     * Fase de retropropagação: atualiza as estatísticas deste nó e de todos os seus
     * antecessores até à raiz.
     * Cada nó soma 1 vitória se ganhar, 0,5 em caso de empate e 0 em caso de derrota
     *
     * @param winner vencedor da simulação, "Blank" se empate
     */
    public void backPropagate(Ilayout.ID winner){
        this.visits++;

        if (winner == this.maxPlayer){
            wins += 1.0;
        } else if (winner == Ilayout.ID.Blank){
            wins += 0.5;
        }

        if(parent != null){
            parent.backPropagate(winner);
        }
    }

    /**
     * @return a lista de filhos já expandidos deste nó
     */
    public ArrayList<MCTSNode> getChildren() {
        return this.children;
    }

    /**
     * @return o estado do jogo representado por este nó
     */
    public Ilayout getState() {
        return this.state;
    }

    /**
     * @return a jogada que levou do pai a este nó, -1 no caso da raiz
     */
    public Integer getMove() {
        return this.move;
    }

    /**
     * @return o número de visitas deste nó
     */
    public int getVisits() {
        return this.visits;
    }

    /**
     * @return a recompensa acumulada deste nó
     */
    public double getWins() {
        return this.wins;
    }
}
