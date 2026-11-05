import java.util.*;

/**
 * Representa o tabuleiro do Jogo do Galo.
 * Guarda o conteúdo de cada casa, o jogador que tem a vex, as jogadas
 * disponíveis e o estado do jogo.
 * As casas são indexadas de 0 a N*M-1, da esquerda para a direita e de cima para baixo.
 */
public class Board implements Ilayout,Cloneable{
    private ID[][] board;
    private ID playersTurn;
    private ID winner;
    private HashSet<Integer> movesAvailable;

    private int moveCount;
    private boolean gameOver;

    /**
     * Cria um tabuleiro vazio, com o jogador X a começar
     */
    Board() {
        board = new ID[N][M];
        movesAvailable = new HashSet<>();
        reset();
    }

    /**
     * Coloca todas as casas a vazio e carrega as jogadas disponíveis
     */
    private void initialize () {
        for (int row = 0; row < N; row++)
            for (int col = 0; col < M; col++) {
                board[row][col] = ID.Blank;
            }
        movesAvailable.clear();

        for (int i = 0; i < N*M; i++) {
            movesAvailable.add(i);
        }
    }

    /**
     * Reinicia o jogo com um tabuleiro vazio, com o jogador X a começar
     */
    public void reset() {
        moveCount = 0;
        gameOver = false;
        playersTurn = ID.X;
        winner = ID.Blank;
        initialize();
    }

    /**
     * Verifica se o jogador que acabou de jogar completou uma linha,
     * coluna ou diagonal. Em caso afirmativo, define-o como vencedor
     * e marca o jogo como terminado
     */
    private void checkWinner() {
        ID player = playersTurn;

        // Verificar as linhas e as colunas
        for (int i = 0; i < N; i++) {
            // Verificar Linhas
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player ) {
                winner = player;
                gameOver = true;
                return;
            }

            // Verificar Coluna
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player ) {
                winner = player;
                gameOver = true;
                return;
            }
        }

        // Verificar Diagonal Principal
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player ) {
            winner = player;
            gameOver = true;
            return;
        }

        // Verificar Diagonal Secundária
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player ) {
            winner = player;
            gameOver = true;
        }
    }


    /**
     * Coloca um X ou um O na casa indicada, consoante o jogador que tem a vez
     *
     * @param index índice da casa, de 0 a 9
     * @return true se a jogada foi efetuada, false se a casa já estava ocupada
     * @throws IllegalStateException se o jogo já tiver terminado
     */
    public boolean move (int index) {
        return move(index% M, index/M);
    }

    /**
     * Coloca um X ou um O na posição indicada, consoante o jogador que tem a vez
     * Depois da jogada, verifica se houve vitória ou empate e passa a vez
     * ao outro jogador
     *
     * @param x coluna da casa
     * @param y linha da casa
     * @return true se a jogada foi efetuada, false se a casa já estava ocupada
     * @throws IllegalStateException se o jogo já tiver terminado
     */
   public boolean move (int x, int y) {
        if (gameOver) {
            throw new IllegalStateException("Game over. No more moves can be played.");
        }

        if (board[y][x] == ID.Blank) {
            board[y][x] = playersTurn;
        } else {
            return false;
        }

        moveCount++;
        movesAvailable.remove(y * N + x);

        // The game is a draw.
        if (moveCount == N * M) {
            winner = ID.Blank;
            gameOver = true;
        }

        // Check for a winner.
        checkWinner();

       playersTurn = (playersTurn == ID.X) ? ID.O : ID.X;
        return true;
    }

    /**
     * Verifica se o jogo terminou
     *
     * @return true se o jogo terminou, false cas contrário
     */
    public boolean isGameOver () {
        return gameOver;
    }

    /**
     * Indica o jogador que tem a ver de jogar
     *
     * @return o jogador que tem a vez
     */
    public ID getTurn () {
        return playersTurn;
    }

    /**
     * Indica o vencedor do jogo
     *
     * @return o jogador vencedor ou "Blank" em caso de empate
     * @throws IllegalStateException se o jogo ainda não tiver terminado
     */
    public ID getWinner () {
        if (!gameOver) {
            throw new IllegalStateException("Not over yet!");
        }
        return winner;
    }

    /**
     * Devolve os índices de todas as casas vazias
     *
     * @return conjunto com os índices das casas vazias
     */
    public HashSet<Integer> getAvailableMoves () {
        return new HashSet<>(movesAvailable);
    }

    /**
     * Cria uma cópia profunda do tabuleiro
     *
     * @return uma cópia independente deste tabuleiro
     */
    public Object clone () {
    	try {
	        Board b = (Board) super.clone();

	        b.board = new ID[N][M];
	        for (int i=0; i<N; i++)
	        	for (int j=0; j<M; j++)
	        		b.board[i][j] = this.board[i][j];

	        b.playersTurn = this.playersTurn;
	        b.winner = this.winner;
	        b.movesAvailable = new HashSet<Integer>();
	        b.movesAvailable.addAll(this.movesAvailable);
	        b.moveCount = this.moveCount;
	        b.gameOver = this.gameOver;
	        return b;
    	}
    	catch (Exception e) {
    		throw new InternalError();
    	}
    }

    /**
     * Devolve uma representação textual do tabuleiro, uma linha por fila
     * As casas vazias aparecem como "_"
     *
     * @return o tabuleiro em formato de texto
     */
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < N; y++) {
            for (int x = 0; x < M; x++) {
                if (board[y][x] == ID.Blank) {
                    sb.append("-");
                } else {
                    sb.append(board[y][x].name());
                }
                sb.append(" ");
            }
            if (y != N -1) {
                sb.append("\n");
            }
        }
        return new String(sb);
    }

    /**
     * Verifica se uma casa está vazia
     *
     * @param index índice da casa
     * @return true se a casa estiver vazia
     */
    public boolean isBlank (int index) {
        int x=index/M;
        int y=index%M;
        return (board[x][y] == ID.Blank);
    }


    /**
     * Gera todos os estados que resultam de uma jogada válida
     * a parir deste tabuleiro
     *
     * @return lista com um tabuleiro por cada casa vazia
     */
    public List<Ilayout> children() {
        List<Ilayout> list = new ArrayList<>();

        // YOUR CODE HERE
        for (int i=0; i<N * M; i++){
            if(isBlank(i)){
                Board child = (Board) this.clone();
                child.move(i);
                list.add(child);
            }
        }

         return list;
    }


    /**
     * Compara este tabuleiro com outro objeto. Dois tabuleiros são iguais
     * se tiverem o mesmo conteúdo em todas as casas
     *
     * @param other objeto a comparar
     * @return true se os tabuleiros tiverem o mesmo conteúdo, false caso contrário
     */
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (other == null) return false;
		if (getClass() != other.getClass()) return false;
		Board that = (Board) other;

		for (int x=0; x<N; x++)
            for (int y=0; y<M; y++)
            	if (board[x][y] != that.board[x][y]) return false;
        return true;
    }

    /**
     * Calcula o código de hash com base no conteúdo das casas
     *
     * @return o código de hash do tabuleiro
     */
	@Override
	public int hashCode() {
		return Arrays.deepHashCode(board);
	}
}
