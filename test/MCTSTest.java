import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe MCTS e do agente XAgent.
 * Verificam que a pesquisa devolve jogadas válidas, não altera o tabuleiro
 * recebido, encontra jogadas táticas (ganhar e bloquear) e rejeita
 * tabuleiros já terminados.
 *
 * Como as simulações são aleatórias, os testes táticos dependem do acaso;
 * com 5000 iterações a jogada correta é encontrada de forma consistente.
 */
class MCTSTest {

    /** Número de iterações usado nos testes, igual ao do XAgent. */
    private static final int ITERACOES = 5000;

    /** Parâmetro de exploração usado nos testes, igual ao do XAgent. */
    private static final double C = 1.4;

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
     * A jogada devolvida é sempre uma casa vazia do tabuleiro
     */
    @Test
    public void testDevolveJogadaValida() {
        MCTS mcts = new MCTS(ITERACOES, C);
        Board b = jogar(4, 0, 8);
        int jogada = mcts.search(b);
        assertTrue(b.getAvailableMoves().contains(jogada));
    }

    /**
     * A pesquisa não altera o tabuleiro recebido:
     * o conteúdo, a vez e as jogadas disponíveis ficam iguais
     */
    @Test
    public void testNaoAlteraTabuleiro() {
        Board b = jogar(0, 4);
        Board antes = (Board) b.clone();

        new MCTS(ITERACOES, C).search(b);

        assertEquals(antes, b);
        assertEquals(antes.getTurn(), b.getTurn());
        assertEquals(antes.getAvailableMoves(), b.getAvailableMoves());
        assertFalse(b.isGameOver());
    }

    /**
     * Com uma única casa livre, a pesquisa devolve essa casa
     */
    @Test
    public void testUnicaJogadaPossivel() {
        Board b = jogar(2, 0, 5, 1, 6, 3, 7, 4);                       // só falta a casa 8
        assertEquals(8, new MCTS(ITERACOES, C).search(b));
    }

    /**
     * Quando o X pode ganhar numa jogada, escolhe essa jogada
     */
    @Test
    public void testGanhaQuandoPode() {
        // X X -
        // O O -
        // - - -     vez do X: ganha na casa 2
        Board b = jogar(0, 3, 1, 4);
        assertEquals(2, new MCTS(ITERACOES, C).search(b));
    }

    /**
     * Quando o adversário ameaça ganhar, o O bloqueia
     */
    @Test
    public void testBloqueiaAdversario() {
        // X X -
        // - O -
        // - - -     vez do O: tem de bloquear na casa 2
        Board b = jogar(0, 4, 1);
        assertEquals(2, new MCTS(ITERACOES, C).search(b));
    }

    /**
     * Pesquisar a partir de um tabuleiro já terminado lança IllegalStateException
     */
    @Test
    public void testJogoTerminadoLancaExcecao() {
        Board b = jogar(0, 3, 1, 4, 2);                                // X ganhou
        assertThrows(IllegalStateException.class, () -> new MCTS(ITERACOES, C).search(b));
    }

    /**
     * O XAgent devolve uma casa vazia e não altera o tabuleiro
     */
    @Test
    public void testXAgentJogaSemAlterarTabuleiro() {
        Board b = jogar(4);
        Board antes = (Board) b.clone();

        int jogada = XAgent.play(b);

        assertTrue(b.getAvailableMoves().contains(jogada));
        assertEquals(antes, b);
        assertEquals(antes.getTurn(), b.getTurn());
    }

    /**
     * Teste de integração: dois agentes MCTS a jogar entre si empatam sempre,
     * como é de esperar no Jogo do Galo quando ambos jogam bem
     */
    @Test
    public void testAutoJogoEmpata() {
        for (int i = 0; i < 20; i++) {
            Board b = new Board();
            while (!b.isGameOver()) {
                b.move(XAgent.play(b));
            }
            assertEquals(Ilayout.ID.Blank, b.getWinner(), "jogo " + i);
        }
    }
}