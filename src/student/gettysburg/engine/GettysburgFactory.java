/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package student.gettysburg.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;
import gettysburg.common.exceptions.GbgNotImplementedException;
import gettysburg.engine.common.TestGettysburgEngine;
import student.gettysburg.engine.common.BattleDescriptorImpl;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GbgUnitImpl;
import student.gettysburg.engine.common.GettysburgBoard;
import student.gettysburg.engine.common.GettysburgEngine;
import student.gettysburg.engine.utility.configure.BattleOrder;
import student.gettysburg.engine.utility.configure.UnitInitializer;

/**
 * A factory class that contains creation methods for all of the components of
 * the Gettysburg game, including the game itself.
 * @version Jul 30, 2017
 */
public final class GettysburgFactory
{
	/**
	 * Creation method for a new Gettysburg game instance.
	 * @return a new Gettysburg game
	 */
	public static GbgGame makeGame()
	{
		GettysburgBoard board = new GettysburgBoard();
		
		board.initializeUnits(0, GbgGameStep.CMOVE);
		board.initializeUnits(0, GbgGameStep.UMOVE);

		GbgGame game = new GettysburgEngine(board);
		
		return game;
	}
	
	
	/**
	 * Creation for a new Gettysburg game instance. In this case the
	 * client can specify a version, such as "alpha", or some other
	 * string that indicates that a version of the game with limited
	 * or extended capabilities should be produced. The valid version
	 * strings are defined by the instructor for the course. If the
	 * string is invalid, throw a GbgNotImplementedException.
	 * 
	 * @param version the version to create
	 * @return an instance of the specific version of the Gettysburg game
	 */
	public static GbgGame makeGame(String version)
	{
		throw new GbgNotImplementedException("makeGame()");
	}
	/**
	 * Creation method for a test Gettysburg game
	 * @return the TestGbgGame instance
	 */
	public static TestGbgGame makeTestGame()
	{
		GettysburgBoard board = new GettysburgBoard();
		
		board.initializeUnits(0, GbgGameStep.CMOVE);
		board.initializeUnits(0, GbgGameStep.UMOVE);

		TestGettysburgEngine testGame = new TestGettysburgEngine(board);
		
		return testGame;
	}
	
	
	/**
	 * Creation for a new Gettysburg  test game instance. In this case the
	 * client can specify a version, such as "alpha", or some other
	 * string that indicates that a version of the game with limited
	 * or extended capabilities should be produced. The valid version
	 * strings are defined by the instructor for the course. If the
	 * string is invalid, throw a GbgNotImplementedException.
	 * 
	 * @param version the version to create
	 * @return an instance of the specific version of the Gettysburg game
	 */
	public static GbgGame makeTestGame(String version)
	{
		throw new GbgNotImplementedException("makeGame()");
	}
	
	/**
	 * Factory method for creating Coordinates. This method makes a Coordinate
	 * instance that is used by the GettysburgEngine implementation and internal
	 * to the engine's classes. There is no requirement by the client that objects
	 * of this type are used to implement the Coordinate interface.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the Coordinate object as implemented by the Gettysburg engine.
	 */
	public static Coordinate makeCoordinate(int x, int y)
	{
		try {
			return CoordinateImpl.makeCoordinate(x, y);
		} catch (GbgInvalidCoordinateException e) {
			return null;
		}
	}
	
	/**
	 * Factory method for creating a BattleDescriptor.
	 * @param attackers a collection of attacking units
	 * @param defenders a collection of defending units
	 * @return the BattleDescriptor that is created by this method
	 */
	public static BattleDescriptor makeBattleDescriptor(
			Collection<GbgUnit> attackers, Collection<GbgUnit> defenders)
	{
		BattleDescriptorImpl battle = new BattleDescriptorImpl();
		
		Iterator<GbgUnit> it1 = attackers.iterator();
		
		while (it1.hasNext()) {
			battle.addAttacker(it1.next());
		}

		Iterator<GbgUnit> it2 = attackers.iterator();
		
		while (it2.hasNext()) {
			battle.addAttacker(it2.next());
		}

		return battle;
	}
	
	/**
	 * Factory method for making a square descriptor. Ideally, the implementation
	 * will use some sort of lazy evaluation in order to avoid duplication of
	 * objects that are simply data structures with no active behavior. If not,
	 * then it is necessary to ensure that equals() and hashcode() methods are
	 * implemented.
	 * @param elevation the elevation of the square
	 * @param terrain the terrain of the square
	 * @return the SquareDescriptor
	 */
	public static GbgSquareDescriptor makeSquareDescriptor(int elevation, Terrain terrain)
	{
		throw new GbgNotImplementedException("makeSquareDescriptor()");
	}
	
	private static GettysburgBoard makeBoard() 
	{
		GettysburgBoard board = new GettysburgBoard();
		
		board.initializeUnits(0, GbgGameStep.CMOVE);
		board.initializeUnits(0, GbgGameStep.UMOVE);

		return board;
	}
}
