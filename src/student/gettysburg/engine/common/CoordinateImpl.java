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
package student.gettysburg.engine.common;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;
import student.gettysburg.engine.GettysburgFactory;

/**
 * Implementation of the gettysburg.common.Coordinate interface. Additional methods
 * used in this implementation are added to this class. Clients should <em>ONLY</em>
 * use the public Coordinate interface. Additional methods
 * are only for engine internal use.
 * 
 * @version Jun 9, 2017
 */
public class CoordinateImpl implements Coordinate
{
	private final int x, y;
	
	/**
	 * Private constructor that is called by the factory method.
	 * @param x
	 * @param y
	 */
	private CoordinateImpl(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Needed for JSON processing.
	 */
	public CoordinateImpl()
	{
		x = y = 0;
	}
	
	/**
	 * Factory method for creating Coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public static CoordinateImpl makeCoordinate(int x, int y)
	{
		if (x < 1 || x > GbgBoard.COLUMNS || y < 1 || y > GbgBoard.ROWS) {
			throw new GbgInvalidCoordinateException(
					"Coordinates for (" + x + ", " + y + ") are out of bounds.");
		}
		return new CoordinateImpl(x, y);
	}
	
	/*
	 * @see gettysburg.common.Coordinate#directionTo(gettysburg.common.Coordinate)
	 */
	@Override
	public Direction directionTo(Coordinate coordinate)
	{
		if (coordinate.getX() < x && coordinate.getY() == y) return Direction.WEST;
		if (coordinate.getX() > x && coordinate.getY() == y) return Direction.EAST;
		if (coordinate.getY() < y && coordinate.getX() == x) return Direction.NORTH;
		if (coordinate.getY() > y && coordinate.getX() == x) return Direction.SOUTH;

		if (coordinate.getX() < x && coordinate.getY() < y &&
			Math.abs(coordinate.getX() - x) == Math.abs(coordinate.getY() - y)
		) {
			return Direction.NORTHWEST;
		}

		if (coordinate.getX() > x && coordinate.getY() < y &&
			Math.abs(coordinate.getX() - x) == Math.abs(coordinate.getY() - y)
		) {
			return Direction.NORTHEAST;
		}

		if (coordinate.getY() > y && coordinate.getX() > x &&
			Math.abs(coordinate.getX() - x) == Math.abs(coordinate.getY() - y)
		) {
			return Direction.SOUTHEAST;
		}

		if (coordinate.getY() > y && coordinate.getX() < x && 
			Math.abs(coordinate.getX() - x) == Math.abs(coordinate.getY() - y)
		) {
			return Direction.SOUTHWEST;
		}
		
		return Direction.NONE;
	}

	/*
	 * @see gettysburg.common.Coordinate#distanceTo(gettysburg.common.Coordinate)
	 */
	@Override
	public int distanceTo(Coordinate coordinate)
	{
		return (int) Math.max(
				Math.abs(x - coordinate.getX()),
				Math.abs(y - coordinate.getY())
			);
	}
	
	public Coordinate[] getNeighbors() 
	{
		CoordinateImpl[] adj = new CoordinateImpl[8];// = new
		
		final int[][] adjDiff = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 } };
		
		for (int i = 0; i < 8; i++) {
			if (x + adjDiff[i][0] > 0 && y + adjDiff[i][1] > 0 && x + adjDiff[i][0] < 23 && y + adjDiff[i][1] < 29) {
				adj[i] = (CoordinateImpl) GettysburgFactory.makeCoordinate(x + adjDiff[i][0], y + adjDiff[i][1]);
			}
		}

		return adj;
	}

	/*
	 * @see gettysburg.common.Coordinate#getX()
	 */
	@Override
	public int getX()
	{
		return x;
	}

	/*
	 * @see gettysburg.common.Coordinate#getY()
	 */
	@Override
	public int getY()
	{
		return y;
	}
	
	// Change the equals and hashCode if you need to.
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/*
	 * We do not compare a CoordinateImpl to any object that just implements
	 * the Coordinate interface.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CoordinateImpl)) {
			return false;
		}
		CoordinateImpl other = (CoordinateImpl) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return"(" + x + ", " + y + ")";
	}
}
