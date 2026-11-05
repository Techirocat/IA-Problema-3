import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MCTSNodeTest {

    // Faz as jogadas pela ordem dada (começa sempre o X)
    private Board jogar(int... jogadas) {
        Board b = new Board();
        for (int j : jogadas) {
            b.move(j);
        }
        return b;
    }

    // Cria uma raiz (sem pai e sem jogada)
    private MCTSNode raiz(Board b) {
        return new MCTSNode(b, null, -1, Ilayout.ID.X);
    }

    // ---------- Raiz ----------

    @Test
    public void testRaizComecaVazia() {
        MCTSNode root = raiz(new Board());
        assertEquals(0, root.getVisits());
        assertEquals(0.0, root.getWins());
        assertTrue(root.getChildren().isEmpty());
    }

    // ---------- Terminal / Expandido ----------

    @Test
    public void testIsTerminal() {
        assertFalse(raiz(new Board()).is_terminal());
        assertTrue(raiz(jogar(0, 3, 1, 4, 2)).is_terminal());   // X ganhou
    }

    @Test
    public void testIsFullyExpanded() {
        MCTSNode root = raiz(new Board());
        for (int i = 0; i < 8; i++) {
            root.expand();
            assertFalse(root.is_fully_expanded());
        }
        root.expand();                                          // 9.º filho
        assertTrue(root.is_fully_expanded());
    }

    // ---------- Expansão ----------

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

    @Test
    public void testBestChildPrefereNaoVisitado() {
        MCTSNode root = raiz(new Board());
        MCTSNode a = root.expand();
        MCTSNode b = root.expand();

        a.backPropagate(Ilayout.ID.X);                                 // só o 'a' foi visitado
        assertSame(b, root.best_child());
    }

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

    @Test
    public void testRolloutJogoJaAcabado() {
        MCTSNode node = raiz(jogar(0, 3, 1, 4, 2));                    // X já ganhou
        assertEquals(Ilayout.ID.X, node.rollout());
    }

    @Test
    public void testRolloutUltimaJogadaDaEmpate() {
        Board b = jogar(0, 1, 2, 4, 3, 5, 7, 6);                       // só falta a casa 8
        MCTSNode node = raiz(b);

        assertEquals(Ilayout.ID.Blank, node.rollout());
        assertEquals(1, b.getAvailableMoves().size());                 // original não mudou
    }

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

    @Test
    public void testBackPropagateEmpate() {
        MCTSNode root = raiz(new Board());
        MCTSNode child = root.expand();

        child.backPropagate(Ilayout.ID.Blank);
        assertEquals(1, child.getVisits());
        assertEquals(0.5, child.getWins());
    }

    @Test
    public void testBackPropagateDerrota() {
        MCTSNode root = raiz(new Board());
        MCTSNode child = root.expand();                                // jogada do X

        child.backPropagate(Ilayout.ID.O);
        assertEquals(1, child.getVisits());
        assertEquals(0.0, child.getWins());
    }

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



    @Test
    public void testIsMaxPlayer() {
        MCTSNode root = raiz(new Board());                             // joga o X (MAX)
        MCTSNode filho = root.expand();                                // joga o O (MIN)

        assertTrue(root.isMaxPlayer());
        assertFalse(filho.isMaxPlayer());
    }

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
}