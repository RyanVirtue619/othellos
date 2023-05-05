/*
 * Othello class project competition
 * Copyright 2017 Roger Jaffe
 * All rights reserved
 */
package com.mrjaffesclass.othello;

/**
 * Othello class project competition
 * @author Roger Jaffe
 * @version 1.0
 */
public class Othello {

	public static void simulation(int repeatNum, Player player1, Player player2) throws InterruptedException {
		int repeats = repeatNum;
		int[] results = new int[repeats];
		int p1TotalTime = 0;
		int p2TotalTime = 0;
		int p1AvgGameTime;
		int p2AvgGameTime;
		String p1Name = "";
		String p2Name = "";
		for(int i = 0; i < repeats; i++) {
			System.out.println("Game: " + (i+1));
			ControllerNoPrint c = new ControllerNoPrint( 
			//Controller c = new Controller(
			//ControllerRandomTesting c = new ControllerRandomTesting(
				player1, player2
			);
			c.displayMatchup();
			int result = c.run();
			results[i] = result;
			p1TotalTime += c.p1Time();
			p2TotalTime += c.p2Time();
			p1Name=c.p1GetName();
			p2Name=c.p2GetName();
			c.popPlayerTimes();
		}
		p1AvgGameTime = p1TotalTime/repeats;
		p2AvgGameTime = p2TotalTime / repeats;
		int blackWins = 0;
		int whiteWins = 0;
		int ties = 0;
		for(int i : results) {
			if(i==1) blackWins++;
			if(i==2) whiteWins++;
			if(i==3) ties++;
		}
		System.out.printf("In %d games, %s won: %d, %s won: %d, and %d were ties\n", repeats,p1Name, blackWins,p2Name, whiteWins, ties);
		System.out.printf("%s Times: Total Time = %dms, Avg Time Per Game = %dms\n", p1Name,p1TotalTime, p1AvgGameTime);
		System.out.printf("%s Times: Total Time = %dms, Avg Time Per Game = %dms\n", p2Name,p2TotalTime, p2AvgGameTime);
	}
	public static void main(String[] args) throws InterruptedException {
		int repetitions = 5;
		simulation(repetitions,new TestPlayer(Constants.BLACK), new RyanPlayerEvolved(Constants.WHITE));
		//simulation(repetitions, new RyanPlayerEvolved(Constants.BLACK), new TestPlayer(Constants.WHITE));	
		System.exit(0);
	}
  
}
