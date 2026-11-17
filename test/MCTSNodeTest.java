import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe MCTSNode.
 * Cobrem cada uma das quatro fases do MCTS isoladamente (seleção, expansão,
 * simulação e retropropagação), bem como a distinção entre nós MAX e MIN.
 * Em todos os testes o X é o jogador MAX.
 */
class MCTSNodeTest {

    /**
     * Cria um tabuleiro novo e aplica as jogadas pela ordem dada,
     * começando sempre pelo X e alternando entre os jogadores
     *
     * @param jogadas índices das casas a jogar, de 0 a 8
     * @return o tabuleiro resultante
     * @pre as jogadas são válidas: casas vazias e jogo ainda não terminado
     */
    private Board jogar(int... jogadas) {
        Board b = new Board();
        for (int j : jogadas) {
            b.move(j);
        }
        return b;
    }

    /**
     * Cria a raiz de uma árvore MCTS: sem pai, sem jogada (-1)
     * e com o X como jogador MAX
     *
     * @param b estado do jogo a guardar na raiz
     * @return o nó raiz criado
     */
    private MCTSNode raiz(Board b) {
        return new MCTSNode(b, null, -1, Ilayout.ID.X, 1.4);
    }

    // ---------- Raiz ----------

    /**
     * Uma raiz acabada de criar não tem visitas, vitórias nem filhos
     */
    @Test
    public void testRaizComecaVazia() {
        MCTSNode root = raiz(new Board());
        assertEquals(0, root.getVisits());
        assertEquals(0.0, root.getWins());
        assertTrue(root.getChildren().isEmpty());
    }

    // ---------- Terminal / Expandido ----------

    /**
     * is_terminal() é falso num tabuleiro vazio e verdadeiro num tabuleiro em que o X já ganhou
     */
    @Test
    public void testIsTerminal() {
        assertFalse(raiz(new Board()).is_terminal());
        assertTrue(raiz(jogar(0, 3, 1, 4, 2)).is_terminal());   // X ganhou
    }

    /**
     * Num tabuleiro vazio, a raiz só fica totalmente expandida
     * depois de criados os 9 filhos, um por cada casa vazia
     */
    @Test
    public void testIsFullyExpanded() {
        MCTSNode root = raiz(new Board());
        for (int i = 0; i < 8; i++) {
            root.expand();
            assertFalse(root.is_fully_expanded());
        }
        root.expand();
        assertTrue(root.is_fully_expanded());
    }

    // ---------- Expansão ----------

    /**
     * expand() acrescenta um filho à raiz, em que a casa jogada fica ocupada
     * e a vez passa para o O, sem alterar o tabuleiro da raiz
     */
    @Test
    public void testExpandCriaFilhoValido() {
        Board b = new Board();
        MCTSNode root = raiz(b);
        MCTSNode child = root.expand();

        assertEquals(1, root.getChildren().size());
        assertEquals(Ilayout.ID.O, child.getState().getTurn());        // agora joga o O
        assertFalse(child.getState().getAvailableMoves().contains(child.getMove()));
        assertEquals(9, b.getAvailableMoves().size());                 // original intacto
    }

    /**
     * Nove expansões seguidas da raiz geram nove jogadas diferentes,
     * ou seja, nenhuma jogada é expandida duas vezes
     */
    @Test
    public void testExpandNaoRepeteJogadas() {
        MCTSNode root = raiz(new Board());
        Set<Integer> jogadas = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            jogadas.add(root.expand().getMove());
        }
        assertEquals(9, jogadas.size());                               // 9 jogadas diferentes
    }

    // ---------- Seleção ----------

    /**
     * best_child() escolhe primeiro um filho nunca visitado,
     * antes de calcular a fórmula UCB1
     */
    @Test
    public void testBestChildPrefereNaoVisitado() {
        MCTSNode root = raiz(new Board());
        MCTSNode a = root.expand();
        MCTSNode b = root.expand();

        a.backPropagate(Ilayout.ID.X);                                 // só o 'a' foi visitado
        assertSame(b, root.best_child());
    }

    /**
     * Num nó MAX, com o mesmo número de visitas, best_child() escolhe
     * o filho com maior valor de w/n + c√(ln t / n), isto é, o que ganha sempre
     */
    @Test
    public void testBestChildEscolheMelhorUCB() {
        MCTSNode root = raiz(new Board());
        MCTSNode a = root.expand();
        MCTSNode b = root.expand();

        for (int i = 0; i < 3; i++) {
            a.backPropagate(Ilayout.ID.X);                             // 'a' ganha sempre
            b.backPropagate(Ilayout.ID.O);                             // 'b' perde sempre
        }
        assertSame(a, root.best_child());
    }

    // ---------- Simulação ----------


    /**
     * rollout() num estado já terminal devolve o vencedor sem jogar mais
     */
    @Test
    public void testRolloutJogoJaAcabado() {
        MCTSNode node = raiz(jogar(0, 3, 1, 4, 2));                    // X já ganhou
        assertEquals(Ilayout.ID.X, node.rollout());
    }

    /**
     * Quando só falta uma casa e essa jogada dá empate, rollout() devolve
     * Blank e não altera o tabuleiro do nó
     */
    @Test
    public void testRolloutUltimaJogadaDaEmpate() {
        Board b = jogar(0, 1, 2, 4, 3, 5, 7, 6);                       // só falta a casa 8
        MCTSNode node = raiz(b);

        assertEquals(Ilayout.ID.Blank, node.rollout());
        assertEquals(1, b.getAvailableMoves().size());                 // original não mudou
    }

    /**
     * Cem simulações a partir do tabuleiro vazio terminam todas com um
     * resultado e nenhuma altera o tabuleiro do nó
     */
    @Test
    public void testRolloutTerminaSempre() {
        Board b = new Board();
        MCTSNode node = raiz(b);
        for (int i = 0; i < 100; i++) {
            assertNotNull(node.rollout());
        }
        assertEquals(9, b.getAvailableMoves().size());                 // original não mudou
    }

    // ---------- Retropropagação ----------

    /**
     * Uma vitória do MAX soma 1 visita e 1 vitória ao nó e à raiz
     */
    @Test
    public void testBackPropagateVitoria() {
        MCTSNode root = raiz(new Board());
        MCTSNode child = root.expand();

        child.backPropagate(Ilayout.ID.X);                             // MAX ganhou
        assertEquals(1, child.getVisits());
        assertEquals(1.0, child.getWins());
        assertEquals(1, root.getVisits());
        assertEquals(1.0, root.getWins());                             // a raiz também conta para o MAX
    }

    /**
     * Um empate soma 1 visita e 0.5 vitórias ao nó
     */
    @Test
    public void testBackPropagateEmpate() {
        MCTSNode root = raiz(new Board());
        MCTSNode child = root.expand();

        child.backPropagate(Ilayout.ID.Blank);
        assertEquals(1, child.getVisits());
        assertEquals(0.5, child.getWins());
    }

    /**
     * Uma derrota do MAX soma 1 visita ao nó e não altera as vitórias
     */
    @Test
    public void testBackPropagateDerrota() {
        MCTSNode root = raiz(new Board());
        MCTSNode child = root.expand();                                // jogada do X

        child.backPropagate(Ilayout.ID.O);
        assertEquals(1, child.getVisits());
        assertEquals(0.0, child.getWins());
    }

    /**
     * A retropropagação atualiza todos os nós do caminho até à raiz,
     * com as vitórias contadas sempre do ponto de vista do MAX:
     * uma vitória do MIN não soma vitórias em nenhum nível
     */
    @Test
    public void testBackPropagateSobeAteARaiz() {
        MCTSNode root = raiz(new Board());
        MCTSNode filho = root.expand();                                // jogou o X
        MCTSNode neto = filho.expand();                                // jogou o O

        neto.backPropagate(Ilayout.ID.O);                              // MIN ganhou
        assertEquals(1, neto.getVisits());
        assertEquals(0.0, neto.getWins());                             // vitórias contadas para o MAX
        assertEquals(1, filho.getVisits());
        assertEquals(0.0, filho.getWins());
        assertEquals(1, root.getVisits());
        assertEquals(0.0, root.getWins());
    }


    // ---------- MAX / MIN ----------

    /**
     * A raiz, onde joga o X, é um nó MAX; o filho, onde joga o O, é um nó MIN
     */
    @Test
    public void testIsMaxPlayer() {
        MCTSNode root = raiz(new Board());                             // joga o X (MAX)
        MCTSNode filho = root.expand();                                // joga o O (MIN)

        assertTrue(root.isMaxPlayer());
        assertFalse(filho.isMaxPlayer());
    }

    /**
     * Num nó MIN, best_child() escolhe o filho com o menor valor de
     * w/n − c√(ln t / n), ou seja, o que é melhor para o MIN
     */
    @Test
    public void testBestChildNoMinEscolheMinimo() {
        MCTSNode root = raiz(new Board());
        MCTSNode noMin = root.expand();                                // nó MIN: joga o O
        MCTSNode a = noMin.expand();
        MCTSNode b = noMin.expand();

        for (int i = 0; i < 3; i++) {
            a.backPropagate(Ilayout.ID.X);                             // bom para o MAX
            b.backPropagate(Ilayout.ID.O);                             // bom para o MIN
        }
        assertSame(b, noMin.best_child());                             // MIN minimiza w/n - c√(ln t / n)
    }

    // ---------- Fig. 2 do enunciado ----------

    /**
     * Constrói a árvore da Fig. 2 do enunciado, com os mesmos valores
     * de n e w em cada nó, usando o valor de c indicado.
     * Cada nó recebe uma simulação própria (a da sua criação) e as restantes
     * vêm dos descendentes, tal como acontece no algoritmo.
     *
     * @param c parâmetro de exploração
     * @return os nós da figura: [raiz, A, B, C, D, E, D1, D2], onde
     *         A, B e C são os filhos da raiz (n=4/w=2, n=1/w=0, n=5/w=3),
     *         D e E os filhos de C (n=3/w=1, n=1/w=1) e
     *         D1 e D2 os filhos de D (n=1/w=1, n=1/w=0)
     */
    private MCTSNode[] arvoreFig2(double c) {
        final Ilayout.ID GANHA = Ilayout.ID.X;                         // vitória do MAX: w += 1
        final Ilayout.ID PERDE = Ilayout.ID.O;                         // derrota do MAX: w += 0

        MCTSNode raiz = new MCTSNode(new Board(), null, -1, Ilayout.ID.X, c);
        raiz.backPropagate(PERDE);                                     // raiz: n=1, w=0

        MCTSNode a = raiz.expand();                                    // nó MIN
        a.backPropagate(GANHA);
        a.expand().backPropagate(GANHA);
        a.expand().backPropagate(PERDE);
        a.expand().backPropagate(PERDE);                               // A: n=4, w=2

        MCTSNode b = raiz.expand();
        b.backPropagate(PERDE);                                        // B: n=1, w=0

        MCTSNode cNo = raiz.expand();                                  // nó MIN
        cNo.backPropagate(GANHA);
        MCTSNode d = cNo.expand();                                     // nó MAX
        d.backPropagate(PERDE);
        MCTSNode d1 = d.expand();
        d1.backPropagate(GANHA);                                       // D1: n=1, w=1
        MCTSNode d2 = d.expand();
        d2.backPropagate(PERDE);                                       // D2: n=1, w=0
        MCTSNode e = cNo.expand();
        e.backPropagate(GANHA);                                        // E: n=1, w=1

        return new MCTSNode[]{raiz, a, b, cNo, d, e, d1, d2};
    }

    /**
     * A árvore construída tem os valores de n e w da Fig. 2
     */
    @Test
    public void testArvoreFig2Contagens() {
        MCTSNode[] t = arvoreFig2(0.2);
        int[]    n = {11, 4, 1, 5, 3, 1, 1, 1};
        double[] w = { 5, 2, 0, 3, 1, 1, 1, 0};
        for (int i = 0; i < t.length; i++) {
            assertEquals(n[i], t[i].getVisits());
            assertEquals(w[i], t[i].getWins());
        }
    }

    /**
     * Com c = 0.2, a política de seleção segue o caminho assinalado na Fig. 2:
     * na raiz (MAX) escolhe C (0.74 contra 0.65 e 0.31), em C (MIN) escolhe D
     * (0.19 contra 0.75) e em D (MAX) escolhe D1 (1.21 contra 0.21)
     */
    @Test
    public void testPoliticaSegueFig2() {
        MCTSNode[] t = arvoreFig2(0.2);
        MCTSNode raiz = t[0], c = t[3], d = t[4], d1 = t[6];

        assertTrue(raiz.isMaxPlayer());
        assertFalse(c.isMaxPlayer());
        assertTrue(d.isMaxPlayer());

        assertSame(c,  raiz.best_child());
        assertSame(d,  c.best_child());
        assertSame(d1, d.best_child());
    }

    /**
     * Na mesma árvore, um c maior (1.4) dá mais peso à exploração:
     * a raiz passa a escolher B, o filho menos visitado (n=1),
     * em vez de C
     */
    @Test
    public void testCMaiorFavoreceExploracao() {
        MCTSNode[] t = arvoreFig2(1.4);
        assertSame(t[2], t[0].best_child());
    }

    // ---------- Simulação com resultado forçado ----------

    /**
     * Quando só resta uma casa e essa jogada dá a vitória ao X,
     * todas as simulações devolvem X
     */
    @Test
    public void testRolloutVitoriaForcada() {
        // O O X
        // O O X
        // X X -     falta a casa 8; o X ganha ao jogá-la
        MCTSNode node = raiz(jogar(2, 0, 5, 1, 6, 3, 7, 4));
        for (int i = 0; i < 20; i++) {
            assertEquals(Ilayout.ID.X, node.rollout());
        }
    }
}