/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mrjaffesclass.othello;

import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author student
 */
public class RyanPlayerEvolved extends Player {
    public static final int[][] DIRECTIONS = {{0, 1},{1, 1},{1, 0},{1, -1},{0 ,-1},{-1, -1},{-1, 0},{-1 ,1}};
    public static final int BOARDSIZE = 8;
    int[][] board;
    private int cc = 1000;
	private int mm = 1;
	private int gg = -50;
	private int dd = -75;
	private int ee = 25;
	private int xx = -15;
	private int uu = 15;
	private int yy = 50;
    private int[][] boardInfluences = {
		{cc, gg, yy, ee, ee, yy, gg, cc},
		{gg, dd, xx, xx, xx, xx, dd, gg},
		{yy, xx, uu, mm, mm, uu, xx, yy},
		{ee, xx, mm, mm, mm, mm, xx, ee},
		{ee, xx, mm, mm, mm, mm, xx, ee},
		{yy, xx, uu, mm, mm, uu, xx, yy},
		{gg, dd, xx, xx, xx, xx, dd, gg},
		{cc, gg, yy, ee, ee, yy, gg, cc}};
    
    public RyanPlayerEvolved(int color) 
    {
        super(color);
    }
    
    @Override
	public String getName() {
		return "Ryan Evolved";
	}
        
    @Override
    public Position getNextMove(Board original) {
        board = getSquares(original);
        long startTime = System.nanoTime();
        
        boolean[][] legals = getLegalMoves(board);
        //printLegals(legals);
        double topEval = -100000000;
        int[] topMove = new int[2];
        boolean anyMoves = false;
        for (boolean[] row : legals) {
            for (boolean item : row) {
                if (item) {
                    anyMoves = true;
                }
            }
        }
        if (!anyMoves) {
            return null;
        }
        for (int i = 0; i < legals.length; i++) {
            for (int j = 0; j < legals[0].length; j++) {
                if (legals[i][j]) {
                    MiniMax find = new MiniMax(board, 1, true, this.getColor(), new int[] { i, j });
                    if (find.getEval() > topEval) {
                        topEval = find.getEval();
                        topMove = find.getMove();
                        find.printBoard();
                        System.out.println(find.evaluate());
                        System.out.println(find.getEval());
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        moveTimes.add((endTime - startTime) / 1000000);
        return new Position(topMove[0], topMove[1]);
    }

    class MiniMax
    {
        private int[][] board;
        private int depth;
        private boolean isMaxPlayer;
        private int maxDepth = 5;
        private double eval;
        private ArrayList<int[]> legals = new ArrayList<>();
        private ArrayList<MiniMax> children = new ArrayList<>();
        private int player;
        private int[] move;

        MiniMax(int[][] board, int depth, boolean isMaxPlayer, int player, int[] move) {
            this.board = board;
            this.depth = depth;
            this.isMaxPlayer = isMaxPlayer;
            this.player = player;
            this.move = move;
            if (depth == maxDepth) {
                eval = evaluate();
            } else {
                generateMoves();
                nextLayer();
                getTopEval();
            }
        }

        public void getTopEval() {
            if (legals.size() == 0) {
                eval = gameOver(board);
            } else {
                double top = children.get(0).getEval();
                for (int i = 1; i < children.size(); i++) {
                    double temp = children.get(i).getEval();
                    if (temp > top) {
                        top = temp;
                    }
                }
                eval = top;
            }
        }

        public void printBoard() {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    String getStr = "";
                    if (board[i][j] == -1)
                        getStr = "W";
                    if (board[i][j] == 1)
                        getStr = "B";
                    if (board[i][j] == 0)
                        getStr = " ";
                    System.out.print("|" + getStr);
                }
                System.out.print("|");
                System.out.println();
            }
        }

        public int[] getMove() {
            return this.move;
        }

        public double getEval() {
            return this.eval;
        }

        public double evaluate() {
            double inf = influence(board);
            return inf;
        }

        public double evaluate(int[][] boardToEval) {
            double inf = influence(boardToEval);
            return inf;
        }
        private double influence(int[][] board) {
			double maxPlayerInfluence = 0;
			double minPlayerInfluence = 0;
			for (int row = 0; row < Constants.SIZE; row++) {
				for (int col = 0; col < Constants.SIZE; col++) {
					if (board[row][col] == player) {
						if(boardInfluences[row][col] < 0) {
							minPlayerInfluence -= boardInfluences[row][col];
						} else {
							maxPlayerInfluence += boardInfluences[row][col];
						}
					} else if (board[row][col] == player*-1) {
						if(boardInfluences[row][col] < 0) {
							maxPlayerInfluence -= boardInfluences[row][col];
						} else {
							minPlayerInfluence += boardInfluences[row][col];
						}
					}
				}
			}
			double x = (maxPlayerInfluence - minPlayerInfluence);
			double y =  (maxPlayerInfluence + minPlayerInfluence);
			double div = (x / y);
			return div;
        }

        private void nextLayer() {
            for (int[] pos : legals) {
                int[][] newBoard = new int[8][8];
                for (int i = 0; i < newBoard.length; i++) {
                    for (int j = 0; j < newBoard[0].length; j++) {
                        newBoard[i][j] = board[i][j];
                    }
                }
                makeMove(newBoard, pos, isMaxPlayer ? (player) : (player * -1));
                children.add(new MiniMax(newBoard, depth + 1, !isMaxPlayer, player, pos));
            }
        }
        
        private void makeMove(int[][] boardToMove, int[] move, int player) {
            boardToMove[move[0]][move[1]] = player;
            updateBoard(boardToMove, move);
        }
        public void printLegals(boolean[][] legals) {
            for(int i = 0; i < BOARDSIZE; i++) {
                for (int j = 0; j < BOARDSIZE; j++) {
                    System.out.print(("|" + (legals[i][j] ? ("A") : ("0"))));
                }
                System.out.print("|");
                System.out.println();
            }
        }
        
        private void updateBoard(int[][] boardToMove, int[] move) {
            HashMap<Integer, int[]> passed = new HashMap<>();
            int square = getBoardItem(boardToMove, move);
            for (int[] dir : DIRECTIONS) {
                int[] pos = new int[2];
                pos[0] = move[0];
                pos[1] = move[1];
                passed = new HashMap<>();
                addVector(dir, pos);
                while (inBounds(pos) && getBoardItem(boardToMove, pos) == square * -1) {
                    passed.put(getBoardItem(boardToMove, move), new int[] { pos[0], pos[1] });
                    addVector(dir, pos);
                }
                if (inBounds(pos) && getBoardItem(boardToMove, move) != 0) {
                    updateSquares(boardToMove, pos, new int[] {move[0], move[1]}, dir);
                }
            }
        }

        private void updateSquares(int[][] boardToMove, int[] to, int[] from, int[] dir) {
            addVector(dir, from);
            while (from[0] != to[0] || from[1] != to[1]) {
                boardToMove[from[0]][from[1]] = boardToMove[from[0]][from[1]] * -1;
                addVector(dir, from);
            }
        }

        private int getBoardItem(int[][] board, int[] move) {
            return board[move[0]][move[1]];
        }
        
        private void generateMoves() {
            boolean[][] legalArr = getLegalMoves(board, isMaxPlayer ? (player) : (player * -1));
            //System.out.println("board to test, player: " + (isMaxPlayer ? ("W") : ("B")));
            for (int i = 0; i < legalArr.length; i++) {
                for (int j = 0; j < legalArr.length; j++) {
                    if (legalArr[i][j]) {
                        System.out.print("X" + " ");
                    } else {
                        System.out.print("O" + " ");
                    }
                }
                System.out.println();
            }
            for (int i = 0; i < legalArr.length; i++) {
                for (int j = 0; j < legalArr.length; j++) {
                    if (legalArr[i][j]) {
                        int[] move = new int[2];
                        move[0] = i;
                        move[1] = j;
                        legals.add(move);
                    }
                    
                }
            }
            if (legals.size() == 0) {
                //System.out.println("SwapPlr");
                swapPlayer();
                legalArr = getLegalMoves(board, isMaxPlayer ? (player) : (player * -1));
                for (int i = 0; i < legalArr.length; i++) {
                    for (int j = 0; j < legalArr.length; j++) {
                        if (legalArr[i][j]) {
                            int[] move = new int[2];
                            move[0] = i;
                            move[1] = j;
                            legals.add(move);
                        }
                    }
                }
            }
            /*System.out.println("board to test, player: " + (isMaxPlayer ? ("W") : ("B")));
            printBoard();
            System.out.println("legals");
            printLegals(legalArr);*/
        }

        private double gameOver(int[][] boardToEval) {
            int playerCount = 0;
            int enemyCount = 0;
            for (int[] row : boardToEval) {
                for (int square : row) {
                    if (square == player) {
                        playerCount++;
                    } else if (square == player * -1) {
                        enemyCount++;
                    }
                }
            }
            if (playerCount > enemyCount) {
                return 10000;
            } else if (playerCount < enemyCount) {
                return -10000;
            } else {
                return 0;
            }
        }

        private void swapPlayer() {
            isMaxPlayer ^= true;
        }
    }
    
        
    public int[] getRand(boolean[][] legals) {
        ArrayList<int[]> positions = new ArrayList<>();
        for(int i = 0; i < BOARDSIZE; i++) {
            for(int j = 0; j < BOARDSIZE; j++) {
                if(legals[i][j]) {
                    int[] newPos = new int[2];
                    newPos[0] = i;
                    newPos[1] = j;
                    positions.add(newPos);
                }
            }
        }
        int index = (int) (Math.random() * positions.size());
        return positions.get(index);
    }
        
    public void printLegals(boolean[][] legals) {
        for(int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                System.out.print(("|" + (legals[i][j] ? ("A") : ("0"))));
            }
            System.out.print("|");
            System.out.println();
        }
    }
        
    public int[][] getSquares(Board board) {
        int[][] data = new int[BOARDSIZE][BOARDSIZE];
        for(int i = 0; i < BOARDSIZE; i++) {
            for(int j = 0; j < BOARDSIZE; j++) {
                data[i][j] = board.getSquare(new Position(i, j)).getStatus();
            }
        }
        return data;
    }
    
    public boolean[][] getLegalMoves(int[][] board) {
        boolean[][] legals = new boolean[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                legals[i][j] = isLegalMove(board, i, j);
            }
        }
        return legals;
    }
    
    public boolean[][] getLegalMoves(int[][] board, int player) {
        boolean[][] legals = new boolean[BOARDSIZE][BOARDSIZE];
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                legals[i][j] = isLegalMove(board, i, j);
            }
        }
        return legals;
    }
    
    public boolean isLegalMove(int[][] board, int row, int col, int color) {
        int[] pos = new int[2];
        HashMap<Integer, int[]> passed = new HashMap<>();
        if(board[row][col] != 0) return false;
        for(int[] dir : DIRECTIONS) {
            pos[0] = row;
            pos[1] = col;
            passed = new HashMap<>();
            addVector(dir, pos);
            while(inBounds(pos) && getItem(pos, board) == color * -1) {
                passed.put(getItem(pos, board), dir);
                addVector(dir, pos);
            }
            if(inBounds(pos)) {
                passed.put(getItem(pos, board), dir);
            }
            if(passed.containsKey(color) && passed.containsKey(color * -1)) return true;
        }
        return false;
    }

    public boolean isLegalMove(int[][] board, int row, int col) {
        int[] pos = new int[2];
        HashMap<Integer, int[]> passed = new HashMap<>();
        if(board[row][col] != 0) return false;
        for(int[] dir : DIRECTIONS) {
            pos[0] = row;
            pos[1] = col;
            passed = new HashMap<>();
            addVector(dir, pos);
            while(inBounds(pos) && getItem(pos, board) == this.getColor() * -1) {
                passed.put(getItem(pos, board), dir);
                addVector(dir, pos);
            }
            if(inBounds(pos)) {
                passed.put(getItem(pos, board), dir);
            }
            if(passed.containsKey(this.getColor()) && passed.containsKey(this.getColor() * -1)) return true;
        }
        return false;
    }

    private int getItem(int[] pos, int[][] board) {
        return board[pos[0]][pos[1]];
    }

    private boolean inBounds(int[] pos) {
        return (pos[0] >= 0 && pos[0] < BOARDSIZE) && (pos[1] >= 0 && pos[1] < BOARDSIZE);
    }


    private void addVector(int[] vector, int[] pos) {
        pos[0] += vector[0];
        pos[1] += vector[1];
    }
	
}
