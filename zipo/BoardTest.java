import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários da classe Board.
 * Verificam a deteção de vitória (em linha, coluna e diagonal, para cada
 * jogador) e a deteção de empate.
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
}