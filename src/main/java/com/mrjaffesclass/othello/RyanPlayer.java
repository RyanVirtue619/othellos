package com.mrjaffesclass.othello;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RyanPlayer extends Player 
{


	
	public RyanPlayer(int color) 
	{
		super(color);
	}
	@Override
	public String getName() {
		return "No-Pruning";
	}

	class MiniMax  {
		final boolean isMaxPlayer;
		private Integer eval = 0;
		private int depth = 0;
		private final int color;
		static int maxDepth = 5;
		private List<MiniMax> children = new ArrayList<>();
		private final Board boardToEdit;
		private Position move;
		boolean[][] stableSquares = new boolean[Constants.SIZE][Constants.SIZE];
		
		MiniMax(Board board, int depth, boolean isMaxPlayer, int color, Position moveMade) {
			this.move = moveMade;
			this.boardToEdit=board;
			this.color = color;
			this.depth = depth;
			this.isMaxPlayer = isMaxPlayer;			
			/*System.out.println("===============================================");
			System.out.println(this.boardToEdit.toString());
			System.out.println("Depth: " + this.depth);
			System.out.println("==============================================="); */
			if(depth == maxDepth) {
				eval = evaluate(boardToEdit);
			} else {
				generateMoves();
				setEval(this.isMaxPlayer);
				if(depth == 0) {
					rootMove();
				}
			}
		}
		
		private void rootMove() {
			for(MiniMax child : children) {
				if(child.getEval() >= this.eval) {
					this.move = child.getMove();
				}
			}
		}
		
		public Position getMove() {
			return this.move;
		}
		
		static Square[][] getBoard(Board board){
			Square[][] squares = new Square[Constants.SIZE][Constants.SIZE];
			for(int i = 0; i < Constants.SIZE; i++) {
				for( int j = 0; j < Constants.SIZE; j++) {
					squares[i][j] = board.getSquare(new Position(i, j));
				}
			}
			return squares;
		}

		static void setBoard(Board copyBoard, Board board) {
			Square[][] original = getBoard(copyBoard);
			for(int i = 0; i < Constants.SIZE; i++) {
				for(int j = 0; j < Constants.SIZE; j++) {
					board.setSquare(new Player(original[i][j].getStatus()), new Position(i, j));
				}
			}
		}

		int getEval() {
			return this.eval;
		}

		public int compareTo(MiniMax node) {
			return eval.compareTo(node.getEval());
		}

		private void setEval(boolean isMaxPlayer) {
			if(children.isEmpty()) {
				eval = evaluate(boardToEdit);
			} else if(isMaxPlayer) {
				eval = children.stream().max(Comparator.comparing(v -> v.getEval())).get().getEval();
				//makes it evaluate # of options after move
			} else {
				eval = children.stream().min(Comparator.comparing(v -> v.getEval())).get().getEval();
				//makes it evaluate # of options after move
			}
		}
		
		private void generateMoves() {
			Player testPlayer = (this.isMaxPlayer? (new Player(this.color)) : (new Player(this.color*-1)));
			List<Position> legalMoves = getLegalMoves(boardToEdit, testPlayer);
			for(Position pos : legalMoves) {
				Board childBoard = new Board();
				setBoard(this.boardToEdit, childBoard);
				childBoard.makeMove(testPlayer, pos);
				this.children.add(new MiniMax(childBoard, this.depth+1, !this.isMaxPlayer, this.color, pos));
			}
		}

		private int evaluate(Board board) {
			int newEval = 0;
			for(int i = 0; i < Constants.SIZE; i++) {
				for(int j = 0; j < Constants.SIZE; j++) {
					Square square = board.getSquare(new Position(i, j));
					if(square.getStatus() != 0) {
						if (isCorner(i, j)) {
							newEval += 100 * square.getStatus();
						} else if(isStable(board, i, j)) {
							newEval += 100 * square.getStatus();
						} 
					}
				}
			}
			newEval = newEval  * color;
			//System.out.println("Score: " + newEval);
			return newEval;
		}

		private boolean isCorner(int x, int y) {
			if(x == 0 && y == 0) return true;
			if(x == Constants.SIZE -1 && y == 0) return true;
			if(x == 0 && y == Constants.SIZE - 1) return true;
			return x == Constants.SIZE - 1 && y == Constants.SIZE - 1;
		}

		private boolean isStable(Board board, int x, int y) {
			boolean nStatic = false, neStatic = false, nwStatic = false, sStatic = false, 
			seStatic = false, swStatic = false, wStatic = false, eStatic = false;
			String[] directions = Directions.getDirections();

			for(String i: directions) {
				if(i.equals("N")) nStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("NE")) neStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("E"))  eStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("SE")) seStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("S"))  sStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("SW")) swStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("W"))  wStatic = checkDirectionStable(board, new Position(x, y), i);
				if(i.equals("NW")) nwStatic = checkDirectionStable(board, new Position(x, y), i);
			}
			stableSquares[x][y] = (nStatic || sStatic) && (neStatic || swStatic) && (nwStatic || seStatic) && (eStatic || wStatic);
			return (nStatic || sStatic) && (neStatic || swStatic) && (nwStatic || seStatic) && (eStatic || wStatic);
		}
		
		

		private boolean checkDirectionStable(Board board, Position position, String direction) {
			Position vector = Directions.getVector(direction);
			Position antiVector = new Position(vector.getRow()*-1, vector.getCol()*-1);
			Position newPosition = position.translate(vector);
			Square originalSquare = board.getSquare(position);
			while(true) {
				if(newPosition.isOffBoard()) return true;
				if(stableSquares[newPosition.getRow()][newPosition.getCol()] && squaresEqual(originalSquare, board.getSquare(newPosition))) return true;
				if(board.getSquare(newPosition).getStatus() == 0) return false;
				if(board.getSquare(newPosition).getStatus() == originalSquare.getStatus()*-1) {
					return isDirFilled(board, position, vector, antiVector);
				}
				newPosition = newPosition.translate(vector);
			}
		}
		
		private boolean isDirFilled(Board board, Position start, Position vector, Position antiVector) {
			Position newPos = start.translate(vector);
			while(!newPos.isOffBoard()) {
				if(board.getSquare(newPos).getStatus() == 0) return false;
				newPos = newPos.translate(vector);
			}
			newPos = start.translate(antiVector);
			while(!newPos.isOffBoard()) {
				if(board.getSquare(newPos).getStatus() == 0) return false;
				newPos = newPos.translate(antiVector);
			}
			return true;
		}
		
		private boolean squaresEqual(Square square1, Square square2) {
			return square1.getStatus() == square2.getStatus();
		}
	}

	@Override
	public Position getNextMove(Board board) {
		long startTime = System.nanoTime();
		MiniMax mini = new MiniMax(board, 0 , true, this.getColor(), null);
		long endTime = System.nanoTime();
		moveTimes.add((endTime-startTime)/1000000);
		return mini.getMove();
	}

	public ArrayList<Position> getLegalMoves(Board board, Player playerToCheck) {
	ArrayList list = new ArrayList<>();
	for (int row = 0; row < Constants.SIZE; row++) {
		for (int col = 0; col < Constants.SIZE; col++) {
			Position testPosition = new Position(row, col);
			if (board.isLegalMove(playerToCheck, testPosition)) {
				list.add(testPosition);
			}        
		}
	}
	return list;
	}
}
