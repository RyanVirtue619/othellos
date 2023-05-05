package com.mrjaffesclass.othello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RyanPlayer7 extends Player 
{

	private int cc = 1000;
	private int mm = 1;
	private int gg = -500;
	private int dd = -750;
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
	public RyanPlayer7(int color) 
	{
		super(color);
	}

	@Override
	public String getName() {
		return "Alpha-Beta-Pruning-Demon-Mode";
	}

	class MiniMax  {
		boolean isMaxPlayer;
		private double eval = 0;
		private int depth = 0;
		private final int color;
		private int maxDepth = 5;
		private List<MiniMax> children = new ArrayList<>();
		private final Board boardToEdit;
		private Position move;
		private double alpha;
		private double beta;
		private HashMap<Position, HashMap<String, Integer>> pieces = new HashMap<>();
		private boolean[][] stableSquares = new boolean[8][8];

		MiniMax(Board board, int depth, boolean isMaxPlayer, int color, Position moveMade, double alpha, double beta) {
			this.move = moveMade;
			this.boardToEdit=board;
			this.color = color;
			this.depth = depth;
			this.isMaxPlayer = isMaxPlayer;		
			this.alpha = alpha;
			this.beta = beta;
			if(depth == maxDepth) {
					eval = evaluate(boardToEdit);
			} else {
					generateMoves();
					//setEval(this.isMaxPlayer);
					if(depth == 0) {
							rootMove();
					}
			}
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder("  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |\n");
			sb.append                           ("--+---+---+---+---+---+---+---+---+\n");
			for (int row = 0; row < Constants.SIZE; row++) {
				sb.append(row).append(" |");
				for (int col = 0; col < Constants.SIZE; col++) {
					boolean bool = stableSquares[row][col];
					sb.append (bool ? (" X ") : (" O ")).append("|");
				}
				sb.append                           ("\n--+---+---+---+---+---+---+---+---+\n");
			}
			return sb.toString();
		}

		private void rootMove() {
			for(MiniMax child : children) {
				if(child.getEval() >= this.eval) {
					this.move = child.getMove();
					/*System.out.println(this.eval);
					System.out.println("-----------------------------------------------------------------------");
					System.out.println(stability(boardToEdit));
					System.out.println(toString());
					System.out.println(boardToEdit.toString());
					System.out.println("-----------------------------------------------------------------------");*/
					break;
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

		double getEval() {
			return this.eval;
		}

		private void generateMoves() {
			eval = (isMaxPlayer? (-1000000) : (1000000));
			Player testPlayer = (this.isMaxPlayer? (new Player(color)) : (new Player(color*-1)));
			List<Position> legalMoves = getLegalMoves(boardToEdit, testPlayer);
			/*if(legalMoves.isEmpty()) {
				eval  *= -1;
			}*/
			for(int i = 0; i < legalMoves.size(); i++) {
				Position pos = legalMoves.get(i);
				Board childBoard = new Board();
				setBoard(this.boardToEdit, childBoard);
				childBoard.makeMove(testPlayer, pos);
				this.children.add(new MiniMax(childBoard, this.depth+1, !this.isMaxPlayer, this.color, pos, this.alpha, this.beta));
				double childVal = children.get(i).getEval();
				eval = (isMaxPlayer? (Math.max(eval, childVal)) : (Math.min(eval, childVal)));
				if(eval > beta) {
					break;
				}
				if(isMaxPlayer) {
					alpha = Math.max(eval, alpha);
				} else {
					beta = Math.min(eval,beta);
				}

			}
		}

		private double evaluate(Board board) {
			/*if(board.countSquares(0) == 0) {
				if(board.countSquares(color) > board.countSquares(color*-1)) {
					return 1000000;
				} else if (board.countSquares(color) < board.countSquares(color*-1)) {
					return -1000000;
				} else {
					return 0;
				}
			}*/
			double corners = corners(board);
			double stability = stability(board);
			double influence = influence(board);
			//double giveCorners = giveCorners(board);
			double mobility = mobility(board);
			//double pieceParity = pieceParity(board);
			return corners +  stability + influence + mobility;
		}
		
		private double influence(Board board) {
			double weight = board.countSquares(0) / 4;
			double maxPlayerInfluence = 0;
			double minPlayerInfluence = 0;
			for (int row = 0; row < Constants.SIZE; row++) {
				for (int col = 0; col < Constants.SIZE; col++) {
					if (board.getSquare(new Position(row, col)).getStatus() == color) {
						if(boardInfluences[row][col] < 0) {
							minPlayerInfluence -= boardInfluences[row][col];
						} else {
							maxPlayerInfluence += boardInfluences[row][col];
						}
					} else if (board.getSquare(new Position(row, col)).getStatus() == color*-1) {
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
			double end = weight * div;
			return end;
                }
		
		private double mobility(Board board) {
			double weight = board.countSquares(0) / 4;
			String[] directions = Directions.getDirections();
			double maxPlayerMobility = 0;
			double minPlayerMobility = 0;
			List<Position> maxLegalMoves = getLegalMoves(board, new Player(color));
			maxPlayerMobility += maxLegalMoves.size();
			List<Position> minLegalMoves = getLegalMoves(board, new Player(color * -1));
			minPlayerMobility += minLegalMoves.size();
			for(int i = 0; i < Constants.SIZE; i++) {
				for(int j = 0; j < Constants.SIZE; j++) {
					Position pos = new Position(i , j);
					if(board.getSquare(pos).getStatus() == 0) {
						for(String vector : directions) {
							if(!pos.translate(Directions.getVector(vector)).isOffBoard()) {
								int status = board.getSquare(pos.translate(Directions.getVector(vector))).getStatus();
								if(status == color) {
									minPlayerMobility++;
								} else if(status == color*-1) {
									maxPlayerMobility++;
								}
							}
						}
					}
				}
			}
			if(maxPlayerMobility == 0 && minPlayerMobility == 0) {
				return 0;
			}
			double x = (maxPlayerMobility - minPlayerMobility);
			double y =  (maxPlayerMobility + minPlayerMobility);
			double div = (x / y);
			double end = weight * div;
			return end;
		}
		
		private Position addPassedPiece(Position index, String direction, int i) {
			if(i == (direction.equals("vertical") ? (index.getRow()) : (index.getCol()))) return null;
			Position newPos = switch (direction) {
				case "horizontal" -> new Position(index.getRow(), i);
				case "vertical" -> new Position(i, index.getCol());
				case "downDiagonal" ->new Position(index.getRow() + (i - index.getCol()), i);
				case "upDiagonal" -> new Position(index.getRow() + ((i - index.getCol()) * -1), i);
				default -> null;
			};
			//System.out.printf("Passed (%d, %d)", newPos.getRow(), newPos.getCol());
			return newPos;
		}
		
		private int interpretLine(int[] line, Position[] passedPieces, Position index, String direction, int color) {
			boolean reachedOpp1 = false;
			boolean reachedOpp2 = false;
			boolean reachedEnd = false;
			for(int i = direction.equals("vertical") ? (index.getRow()) : (index.getCol()); i < Constants.SIZE; i++) {
				if(line[i] == 0) {
					break;
				} else if(Math.abs(line[i]) == 2) {
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedEnd = true;
					break;
				}else if(line[i] == color*-1){
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedOpp1 = true;
					break;
				} else if(i == 7) {
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedEnd = true;
					break;
				} else {
					passedPieces[i] = addPassedPiece(index, direction, i);
				}
			}
			for(int i = direction.equals("vertical") ? (index.getRow()) : (index.getCol()) ; i >= 0; i--) {
				if(line[i] == 0) {
					break;
				} else if(Math.abs(line[i]) == 2) {
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedEnd = true;
					break;
				} else if(line[i] == color*-1){
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedOpp2 = true;
					break;
				} else if(i == 0) {
					passedPieces[i] = addPassedPiece(index, direction, i);
					reachedEnd = true;
					break;
				} else {
					passedPieces[i] = addPassedPiece(index, direction, i);
				}
			}
			if(reachedEnd) {
				return 1;
			} else if (reachedOpp1 && reachedOpp2){
				return 1;
			} else {
				return -1;
			}			
		}

		private void computeProtection(Board board, Position index) {
			//System.out.printf("Enter Compute Protection Index: (%d, %d)\n", index.getRow(), index.getCol());
			pieces.get(index).forEach((key, value) -> {
				//System.out.printf("Checking direction %s\n", key);
				if(value == null) {
					int[] line = new int[8];
					//make sure passed pieces are added
					Position[] passedPieces = new Position[8];
					int stab;
					switch(key) {
						case "horizontal" -> {
							for(int i = 0; i < Constants.SIZE; i++) {
								line[i] = board.getSquare(new Position(index.getRow(), i)).getStatus();
							}
							stab = interpretLine(line, passedPieces, index, key, board.getSquare(index).getStatus());
							pieces.get(index).put(key, stab);
							//System.out.println("");
							//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", index.getRow(), index.getCol(), key, stab);
							for(Position pos : passedPieces) {
								if(pos != null) {
									if(board.getSquare(pos).getStatus() == color) {
										pieces.get(pos).put(key, stab);
										//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", pos.getRow(), pos.getCol(), key, stab);
									}
								}
							}
							for(Position pos : passedPieces) {
								if(pos != null) {
									computeProtection(board, pos);
								}
							}
						}
						case "vertical" -> {
							for(int i = 0; i < Constants.SIZE; i++) {
								line[i] = board.getSquare(new Position(i, index.getCol())).getStatus();
							}
							stab = interpretLine(line, passedPieces, index, key, board.getSquare(index).getStatus());
							pieces.get(index).put(key, stab);
							//System.out.println("");
							//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", index.getRow(), index.getCol(), key, stab);
							for(Position pos : passedPieces) {
								if(pos != null) {
									if(board.getSquare(pos).getStatus() == color) {
										pieces.get(pos).put(key, stab);
										//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", pos.getRow(), pos.getCol(), key, stab);
									}
								}
							}
							for(Position pos : passedPieces) {
								if(pos != null) {
									computeProtection(board, pos);
								}
							}
						}
						case "downDiagonal" -> {
							for(int x = index.getCol(), y = index.getRow(); x >= 0 && y >= 0; x--, y--) {
								if((x == 7 && y < 7) || (y == 7 && x < 7)) {
									line[x] = board.getSquare(new Position(y, x)).getStatus() * 2;
								} else {
									line[x] = board.getSquare(new Position(y, x)).getStatus();
								}
							}
							for(int x = index.getCol(), y = index.getRow(); x < Constants.SIZE && y < Constants.SIZE; x++, y++) {
								if((x == 7 && y < 7) || (y == 7 && x < 7)) {
									line[x] = board.getSquare(new Position(y, x)).getStatus() * 2;
								} else {
									line[x] = board.getSquare(new Position(y, x)).getStatus();
								}
							}
							stab = interpretLine(line, passedPieces, index, key, board.getSquare(index).getStatus());
							pieces.get(index).put(key, stab);
							//System.out.println("");
							//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", index.getRow(), index.getCol(), key, stab);
							for(Position pos : passedPieces) {
								if(pos != null) {
									if(board.getSquare(pos).getStatus() == color) {
										pieces.get(pos).put(key, stab);
										//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", pos.getRow(), pos.getCol(), key, stab);
									}
								}
							}
							for(Position pos : passedPieces) {
								if(pos != null) {
									computeProtection(board, pos);
								}
							}
						}
						case "upDiagonal" -> {
							for(int x = index.getCol(), y = index.getRow(); x >= 0 && y < Constants.SIZE; x--, y++) {
								if((x == 7 && y < 7) || (y == 7 && x < 7)) {
									line[x] = board.getSquare(new Position(y, x)).getStatus() * 2;
								} else {
									line[x] = board.getSquare(new Position(y, x)).getStatus();
								}
							}
							for(int x = index.getCol(), y = index.getRow(); x < Constants.SIZE && y >= 0; x++, y--) {
								if((x == 7 && y < 7) || (y == 7 && x < 7)) {
									line[x] = board.getSquare(new Position(y, x)).getStatus() * 2;
								} else {
									line[x] = board.getSquare(new Position(y, x)).getStatus();
								}
							}
							stab = interpretLine(line, passedPieces, index, key, board.getSquare(index).getStatus());
							pieces.get(index).put(key, stab);
							//System.out.println("");
							//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", index.getRow(), index.getCol(), key, stab);
							for(Position pos : passedPieces) {
								if(pos != null) {
									if(board.getSquare(pos).getStatus() == color) {
										pieces.get(pos).put(key, stab);
										//System.out.printf("Update position: (%d, %d) at direction %s to %d\n", pos.getRow(), pos.getCol(), key, stab);
									}
								}
							}
							for(Position pos : passedPieces) {
								if(pos != null) {
									computeProtection(board, pos);
								}
							}
						}
					}
				}
			});
			//System.out.printf("Exit Compute Protection Index: (%d, %d)\n", index.getRow(), index.getCol());
		}

		// worked off of https://stackoverflow.com/users/11483646/andrei 's idea to send stability information so adjacent pieces in this thread https://stackoverflow.com/questions/41455456/determining-stable-discs-in-othello#:~:text=stable%20%2D%20cannot%20be%20flipped%20(assign,next%20move%20(assign%200%20score)
		private double stability(Board board) {
			if(board.getSquare(new Position(0, 0)).getStatus() == 0
			&& board.getSquare(new Position(7, 0)).getStatus() == 0 
			&& board.getSquare(new Position(0, 7)).getStatus() == 0 
			&& board.getSquare(new Position(7, 7)).getStatus() == 0) return 0;
			int weight = 50;
			int maxPlayerStability = 0;
			int minPlayerStability = 0;
			for(int i = 0; i < Constants.SIZE; i++) {
				for(int j = 0; j < Constants.SIZE; j++) {
					Position pos = new Position(i, j);
					if(board.getSquare(pos).getStatus() != 0) {
						HashMap<String, Integer> discMap;
						discMap = new HashMap<>();
						discMap.put("vertical", null);
						discMap.put("horizontal", null);
						discMap.put("downDiagonal", null);
						discMap.put("upDiagonal", null);
						pieces.put(pos, discMap);
					}
				}
			}
			//System.out.println("------------------------------------------------------------------");
			//System.out.println(board.toString());
			var entry = pieces.entrySet().iterator().next();
			computeProtection(board,  entry.getKey());
			
			for(Map.Entry<Position, HashMap<String, Integer>> var : pieces.entrySet()) {
				Position pos = var.getKey();
				if(board.getSquare(pos).getStatus() == color) {
					maxPlayerStability += (var.getValue().containsValue(-1)) ? (0) : (1);
					if((!var.getValue().containsValue(null)) && (!var.getValue().containsValue(-1))) {
						stableSquares[pos.getRow()][pos.getCol()] = true;
					}
				} else {
					minPlayerStability += (var.getValue().containsValue(-1)) ? (0) : (1);
					if((!var.getValue().containsValue(null) && (!var.getValue().containsValue(-1)))) {
						stableSquares[pos.getRow()][pos.getCol()] = true;
					}
				}
			}
			//System.out.println(toString());
			//System.out.println("------------------------------------------------------------------");
			if(maxPlayerStability == 0 && minPlayerStability == 0) return 0;
			double x = (maxPlayerStability - minPlayerStability);
			double y =  (maxPlayerStability + minPlayerStability);
			double div = (x / y);
			double end = weight * div;
			return end;
		}

		private double corners(Board board) {
			int weight = 50;
			int maxPlayerCorners = 0;
			int minPlayerCorners = 0;
			Square[] corners = new Square[4];
			corners[0] = board.getSquare(new Position(0, 0));
			corners[1] = board.getSquare(new Position(Constants.SIZE-1, 0));
			corners[2] = board.getSquare(new Position(0, Constants.SIZE-1));
			corners[3] = board.getSquare(new Position(Constants.SIZE-1, Constants.SIZE-1));
			for(Square square : corners) {
					if(square.getStatus() == color) {
						maxPlayerCorners++;
					} else if (square.getStatus() == (color * -1)) {
						minPlayerCorners++;
					}
			}
			if(maxPlayerCorners == 0 && minPlayerCorners == 0) return 0;
			double x = (maxPlayerCorners - minPlayerCorners);
			double y =  (maxPlayerCorners + minPlayerCorners);
			double div = (x / y);
			double end = weight * div;
			return end;
		}

		/*private double pieceParity(Board board) {
			int weight = 1;
			int maxPlayerParity = 0;
			int minPlayerParity = 0;
			for(int i = 0; i < Constants.SIZE; i++) {
				for(int j = 0; j < Constants.SIZE; j++) {
					Square square = board.getSquare(new Position(i, j));
					if(square.getStatus() == color) {
						maxPlayerParity++;
					} else if (square.getStatus() == (color * -1)) {
						minPlayerParity++;
					}
				}
			}
			double x = (maxPlayerParity - minPlayerParity);
			double y =  (maxPlayerParity+ minPlayerParity);
			double div = (x / y);
			double end = weight * div;
			return end;
		}*/
		
		/*private double giveCorners(Board board) {
			int weight = 60;
			int maxPlayerGive = 0;
			int minPlayerGive = 0;
			
			Square[][] corners = new Square[4][4];
			Square[] corner1 = new Square[4];
			Square[] corner2 = new Square[4];
			Square[] corner3 = new Square[4];
			Square[] corner4 = new Square[4];

			corner1[0] = board.getSquare(new Position(0, 0));
			corner2[0] = board.getSquare(new Position(Constants.SIZE-1, 0));
			corner3[0] = board.getSquare(new Position(0, Constants.SIZE-1));
			corner4[0] = board.getSquare(new Position(Constants.SIZE-1, Constants.SIZE-1));

			if(corner1[0].getStatus() == 0) {
				corner1[1] = board.getSquare(new Position(1, 1));
				corner1[2] = board.getSquare(new Position(0, 1));
				corner1[3] = board.getSquare(new Position(1, 0));
			}
			
			if(corner2[0].getStatus() == 0) {
				corner2[1] = board.getSquare(new Position(6, 1));
				corner2[2] = board.getSquare(new Position(6, 0));
				corner2[3] = board.getSquare(new Position(7, 1));
			}
			
			if(corner3[0].getStatus() == 0) {
				corner3[1] = board.getSquare(new Position(1, 6));
				corner3[2] = board.getSquare(new Position(0, 6));
				corner3[3] = board.getSquare(new Position(1, 7));
			}
			
			if(corner4[0].getStatus() == 0) {
				corner4[1] = board.getSquare(new Position(6, 6));
				corner4[2] = board.getSquare(new Position(7, 6));
				corner4[3] = board.getSquare(new Position(6, 7));
			}
			
			corners[0] = corner1;
			corners[1] = corner2;
			corners[2] = corner3;
			corners[3] = corner4;
			
			for(Square[] corner : corners) {
				if(corner[0].getStatus() == 0) {
					for(int i = 1; i < 4; i++) { 
						if(corner[i].getStatus() == color) {
							if(i == 1) minPlayerGive += 4;
							minPlayerGive++;
						} else if (corner[i].getStatus() == color * -1) {
							if(i == 1) maxPlayerGive += 4;
							maxPlayerGive++;
						}
					}

				}
			}
			
			if(maxPlayerGive == 0 && minPlayerGive == 0) return 0;
			double x = (minPlayerGive);
			double y =  (12);
			double div = (x / y);
			double end = weight * div;
 			return end;
		}*/
	}


	@Override
	public Position getNextMove(Board board) {
		long startTime = System.nanoTime();
		MiniMax mini = new MiniMax(board, 0 , true, this.getColor(), null, -1000000, 1000000);
		long endTime = System.nanoTime();
		moveTimes.add((endTime-startTime)/1000000);
		return mini.getMove();
	}

	public ArrayList<Position> getLegalMoves(Board board, Player playerToCheck) {
			ArrayList<Position> list = new ArrayList<>();
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

