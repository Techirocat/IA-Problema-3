import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da classe Board.
 * Verificam a deteção de vitória (em linha, coluna e diagonal, para cada
 * jogador), a deteção de empate, a alternância de jogadores, as jogadas
 * inválidas, a cópia do tabuleiro e a geração de sucessores.
 * Em todos os testes o X começa e os jogadores alternam.
 */
class BoardTest {

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

    // ---------- Vitórias ----------

    /**
     * O X completa a primeira linha (casas 0, 1 e 2):
     * o jogo termina e o vencedor é o X
     */
    @Test
    public void testXWinnerLinha() {
        // X: 0,1,2  |  O: 3,4
        Board b = jogar(0, 3, 1, 4, 2);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    /**
     * O X completa a primeira coluna (casas 0, 3 e 6):
     * o jogo termina e o vencedor é o X
     */
    @Test
    public void testXWinnerColuna() {
        // X: 0,3,6  |  O: 1,4
        Board b = jogar(0, 1, 3, 4, 6);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    /**
     * O X completa a diagonal principal (casas 0, 4 e 8):
     * o jogo termina e o vencedor é o X
     */
    @Test
    public void testXWinnerDiagonal() {
        // X: 0,4,8  |  O: 1,2
        Board b = jogar(0, 1, 4, 2, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    /**
     * O X completa a diagonal secundária (casas 2, 4 e 6):
     * o jogo termina e o vencedor é o X
     */
    @Test
    public void testXWinnerDiagonalSecundaria() {
        // X: 2,4,6  |  O: 0,1
        Board b = jogar(2, 0, 4, 1, 6);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    /**
     * O O completa a segunda linha (casas 3, 4 e 5):
     * o jogo termina e o vencedor é o O
     */
    @Test
    public void testOWinnerLinha() {
        // X: 0,1,8  |  O: 3,4,5
        Board b = jogar(0, 3, 1, 4, 8, 5);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

    /**
     * O O completa a coluna do meio (casas 1, 4 e 7):
     * o jogo termina e o vencedor é o O
     */
    @Test
    public void testOWinnerColuna() {
        // X: 0,3,8  |  O: 1,4,7
        Board b = jogar(0, 1, 3, 4, 8, 7);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

    /**
     * O O completa a diagonal principal (casas 0, 4 e 8):
     * o jogo termina e o vencedor é o O
     */
    @Test
    public void testOWinnerDiagonal() {
        // X: 1,2,3  |  O: 0,4,8
        Board b = jogar(1, 0, 2, 4, 3, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

    /**
     * O X ganha ao ocupar a última casa livre: o resultado tem de ser
     * vitória do X e não empate, apesar de o tabuleiro ficar cheio
     */
    @Test
    public void testVitoriaNaUltimaJogada() {
        // Tabuleiro final:
        // O O X
        // O O X
        // X X X   (o X completa a casa 8: coluna 2,5,8 e linha 6,7,8)
        Board b = jogar(2, 0, 5, 1, 6, 3, 7, 4, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    // ---------- Empate ----------

    /**
     * O tabuleiro fica cheio sem nenhuma linha completa:
     * o jogo termina empatado e getWinner() devolve Blank
     */
    @Test
    public void testEmpate() {
        // Tabuleiro final:
        // X O X
        // X O O
        // O X X
        Board b = jogar(0, 1, 2, 4, 3, 5, 7, 6, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.Blank, b.getWinner());
    }

    // ---------- Estado inicial e vez ----------

    /**
     * Um tabuleiro novo está vazio, com a vez do X, o jogo por acabar
     * e as 9 casas disponíveis
     */
    @Test
    public void testTabuleiroInicial() {
        Board b = new Board();
        assertEquals(Ilayout.ID.X, b.getTurn());
        assertFalse(b.isGameOver());
        assertEquals(9, b.getAvailableMoves().size());
        for (int i = 0; i < 9; i++) {
            assertTrue(b.isBlank(i));
        }
    }

    /**
     * reset() devolve um tabuleiro já jogado ao estado inicial
     */
    @Test
    public void testReset() {
        Board b = jogar(0, 3, 1, 4, 2);                                // jogo terminado
        b.reset();
        assertEquals(new Board(), b);
        assertEquals(Ilayout.ID.X, b.getTurn());
        assertFalse(b.isGameOver());
        assertEquals(9, b.getAvailableMoves().size());
    }

    /**
     * A vez alterna entre X e O a cada jogada válida
     */
    @Test
    public void testVezAlterna() {
        Board b = new Board();
        b.move(0);
        assertEquals(Ilayout.ID.O, b.getTurn());
        b.move(4);
        assertEquals(Ilayout.ID.X, b.getTurn());
    }

    // ---------- Jogadas ----------

    /**
     * Uma jogada válida ocupa a casa e retira-a das jogadas disponíveis
     */
    @Test
    public void testJogadaOcupaCasa() {
        Board b = new Board();
        assertTrue(b.move(4));
        assertFalse(b.isBlank(4));
        assertFalse(b.getAvailableMoves().contains(4));
        assertEquals(8, b.getAvailableMoves().size());
    }

    /**
     * Jogar numa casa ocupada devolve false e não altera o tabuleiro,
     * a vez nem as jogadas disponíveis
     */
    @Test
    public void testJogadaEmCasaOcupada() {
        Board b = jogar(4);                                            // X na casa 4, vez do O
        Board antes = (Board) b.clone();

        assertFalse(b.move(4));
        assertEquals(antes, b);
        assertEquals(Ilayout.ID.O, b.getTurn());
        assertEquals(antes.getAvailableMoves(), b.getAvailableMoves());
    }

    /**
     * Jogar depois de o jogo terminar lança IllegalStateException
     */
    @Test
    public void testJogadaDepoisDoFim() {
        Board b = jogar(0, 3, 1, 4, 2);                                // X ganhou
        assertThrows(IllegalStateException.class, () -> b.move(8));
    }

    /**
     * Pedir o vencedor antes do fim do jogo lança IllegalStateException
     */
    @Test
    public void testGetWinnerAntesDoFim() {
        Board b = jogar(0, 4);
        assertThrows(IllegalStateException.class, b::getWinner);
    }

    /**
     * getAvailableMoves() devolve uma cópia: alterá-la não altera o tabuleiro
     */
    @Test
    public void testGetAvailableMovesDevolveCopia() {
        Board b = new Board();
        b.getAvailableMoves().clear();
        assertEquals(9, b.getAvailableMoves().size());
    }

    // ---------- Cópia ----------

    /**
     * clone() devolve um tabuleiro igual mas independente:
     * jogar no clone não altera o original
     */
    @Test
    public void testCloneIndependente() {
        Board original = jogar(0, 4);
        Board copia = (Board) original.clone();

        assertNotSame(original, copia);
        assertEquals(original, copia);
        assertEquals(original.getTurn(), copia.getTurn());

        copia.move(8);
        assertTrue(original.isBlank(8));
        assertEquals(7, original.getAvailableMoves().size());
        assertEquals(Ilayout.ID.X, original.getTurn());
    }

    // ---------- Sucessores ----------

    /**
     * children() gera um sucessor por cada casa vazia, todos diferentes,
     * com a vez do outro jogador, sem alterar o tabuleiro original
     */
    @Test
    public void testChildren() {
        Board b = jogar(0, 4);                                         // 7 casas vazias, vez do X
        Board antes = (Board) b.clone();

        List<Ilayout> filhos = b.children();

        assertEquals(7, filhos.size());
        Set<Ilayout> distintos = new HashSet<>(filhos);
        assertEquals(7, distintos.size());                             // todos diferentes
        for (Ilayout f : filhos) {
            assertEquals(Ilayout.ID.O, f.getTurn());
            assertEquals(6, f.getAvailableMoves().size());
        }
        assertEquals(antes, b);                                        // original intacto
        assertEquals(7, b.getAvailableMoves().size());
    }
}