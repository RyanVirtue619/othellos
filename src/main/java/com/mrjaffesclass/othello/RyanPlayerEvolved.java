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
		long endTime = System.nanoTime();
		moveTimes.add((endTime-startTime)/1000000);
                boolean[][] legals = getLegalMoves(board);
                printLegals(legals);
                int[] thing = getRand(legals);
                return new Position(thing[0],thing[1]);
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
                for(int j = 0; j < BOARDSIZE; j++) {
                    System.out.print((legals[i][j] ? ("A") : ("0")) + " "); 
                }
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
            for(int i = 0; i < BOARDSIZE; i++) {
                for(int j = 0; j < BOARDSIZE; j++) {
                    legals[i][j] = isLegalMove(board, i, j); 
                }
            }
            return legals;
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
