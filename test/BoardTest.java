import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    // Faz as jogadas pela ordem dada (começa sempre o X)
    private Board jogar(int... jogadas) {
        Board b = new Board();
        for (int j : jogadas) {
            b.move(j);
        }
        return b;
    }

    @Test
    public void testXWinnerLinha() {
        // X: 0,1,2  |  O: 3,4
        Board b = jogar(0, 3, 1, 4, 2);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    @Test
    public void testXWinnerColuna() {
        // X: 0,3,6  |  O: 1,4
        Board b = jogar(0, 1, 3, 4, 6);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    @Test
    public void testXWinnerDiagonal() {
        // X: 0,4,8  |  O: 1,2
        Board b = jogar(0, 1, 4, 2, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.X, b.getWinner());
    }

    @Test
    public void testOWinnerLinha() {
        // X: 0,1,8  |  O: 3,4,5
        Board b = jogar(0, 3, 1, 4, 8, 5);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

    @Test
    public void testOWinnerColuna() {
        // X: 0,3,8  |  O: 1,4,7
        Board b = jogar(0, 1, 3, 4, 8, 7);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

    @Test
    public void testOWinnerDiagonal() {
        // X: 1,2,3  |  O: 0,4,8
        Board b = jogar(1, 0, 2, 4, 3, 8);
        assertTrue(b.isGameOver());
        assertEquals(Ilayout.ID.O, b.getWinner());
    }

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