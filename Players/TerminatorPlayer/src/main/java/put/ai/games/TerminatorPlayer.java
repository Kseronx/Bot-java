package put.ai.games;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class TerminatorPlayer extends Player {
    public static void main(String[] args){}
    private Move maxMove;
    private Move bestMove;
    private int depth = 2;

    @Override
    public String getName() {
        return "Kacper Woźniak 151118";
    }


    @Override
    public Move nextMove(Board board) {
        bestMove = null;
        minmax(depth, board.clone(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return bestMove;
    }

    private int minmax(int depth, Board parentBoard, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || parentBoard.getWinner(getColor()) == getColor()) {
            return getUtility(parentBoard);
        }

        List<Move> possibleMoves = parentBoard.getMovesFor(getColor());

        for (Move move : possibleMoves) {
            Board tempBoard = parentBoard.clone();
            tempBoard.doMove(move);

            int score = maximizingPlayer ?
                    minmax(depth - 1, tempBoard, alpha, beta, false) :
                    minmax(depth - 1, tempBoard, alpha, beta, true);

            tempBoard.undoMove(move);

            if (maximizingPlayer) {
                if (score > alpha) {
                    alpha = score;
                    if (depth == this.depth) {
                        bestMove = move;
                    }
                }
                if (alpha >= beta) {
                    break;
                }
            } else {
                beta = Math.min(beta, score);
                if (alpha >= beta) {
                    break;
                }
            }
        }

        return maximizingPlayer ? alpha : beta;
    }
    private int getUtility(Board board) {
        Color color = getColor();
        int size = board.getSize();
        int utility = 0;

        // Check for a winner
        if (board.getWinner(color) == color) {
            return Integer.MAX_VALUE; // Your player wins
        } else if (board.getWinner(getOpponentColor(color)) == getOpponentColor(color)) {
            return Integer.MIN_VALUE; // Opponent wins
        }

        // Evaluate the board based on the number of pieces in a row
        utility += evaluateRowColumn(board, color);
        utility += evaluateRowColumn(board, getOpponentColor(color));

        // You can add more evaluation criteria based on the specifics of Pentago

        return utility;
    }

    private int evaluateRowColumn(Board board, Color color) {
        int size = board.getSize();
        int streakLength = 0;

        // Check rows
        for (int i = 0; i < size; i++) {
            streakLength += countStreak(board, i, 0, 0, 1, color);
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            streakLength += countStreak(board, 0, j, 1, 0, color);
        }

        return streakLength;
    }

    private int countStreak(Board board, int startX, int startY, int deltaX, int deltaY, Color color) {
        int size = board.getSize();
        int streakLength = 0;

        int x = startX;
        int y = startY;

        while (x >= 0 && x < size && y >= 0 && y < size && board.getState(x, y) == color) {
            streakLength++;
            x += deltaX;
            y += deltaY;
        }

        return streakLength;
    }

    private Color getOpponentColor(Color color) {
        return Player.getOpponent(color);
    }
}

