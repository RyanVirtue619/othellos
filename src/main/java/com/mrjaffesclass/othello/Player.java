package com.mrjaffesclass.othello;

import java.util.ArrayList;
import java.util.List;

/**
 * Player object. Students will extend this class
 * and override the getNextMove method
 * 
 * @author Mr. Jaffe
 * @version 1.0
 */
public class Player
{
  private final int color;
  	public final List<Long> moveTimes;
  /**
   * Player constructor
   * @param color   One of Constants.WHITE or Constants.BLACK
   */
  public Player(int color) {
	  
this.moveTimes = new ArrayList<>();
	this.color = color;
  }
  
  public void popTime() {
	  for(int i = 0; i < moveTimes.size(); i++) {
		  moveTimes.remove(i);
	  }
  }
  
  public Long averageMoveTime() {
		Long size = Long.valueOf(moveTimes.size());
		Long sum = Long.valueOf(0);
		for(int i = 0; i < size; i++) {
			sum += moveTimes.get(i);
		}
		return sum/size;
	}

	public Long totalTime() {
		Long size = Long.valueOf(moveTimes.size());
		Long sum = Long.valueOf(0);
		for(int i = 0; i < size; i++) {
			sum += moveTimes.get(i);
		}
		return sum;
	}
  
  /**
   * Gets the player color
   * @return        Player color
   */
  public int getColor() {
    return this.color;
  }
  
  /**
   * Gets the player name
   * @return        Player name
   */
  public String getName() {
    return this.getClass().getSimpleName();
  }

  /**
   * The player must override getNextMove
   * @param board Game board
   * @return A position coordinate pair of his/her next move. Returns null
   *          if no move is available
   */
  Position getNextMove(Board board) {
    return null;
  };
  
  /**
   * Are this player and the passed-in player the same?
   * @param p Passed-in player
   * @return true if the players are the same
   */
  boolean isThisPlayer(Player p) {
    return p.getColor() == this.getColor();
  }
  
  /**
   * Are this color and the passed-in color the same?
   * @param p Passed-in color (represented as an integer)
   * @return true if the colors (integers) are the same
   */
  boolean isThisPlayer(int p) {
    return p == this.getColor();
  }

  @Override
  public String toString() {
    switch (this.color) {
      case Constants.BLACK:
        return this.getName()+" (BLACK)";
      case Constants.WHITE:
        return this.getName()+" (WHITE)";
      default:
        return this.getName()+" (?????)";
    }
  }
  
}
