/*******************************************************************************
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

import java.util.*;
import java.util.Map.Entry;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.GettysburgFactory;

/**
 * This is the game engine master class that provides the interface to the game
 * implementation. DO NOT change the name of this file and do not change the
 * name of the methods that are defined here since they must be defined to implement the
 * GbgGame interface.
 * 
 * @version Jun 9, 2017
 */
public class GettysburgEngine implements GbgGame
{
	protected int turnNumber = 1;
	protected GbgGameStep currentStep = GbgGameStep.UMOVE;

	private static int HOURS_IN_ONE_BATTLE_DAY = 13;
	protected GettysburgBoard board;

	private Collection<BattleDescriptor> battles = new ArrayList<BattleDescriptor>();
//	boolean isBattleResolved = false;


	public GettysburgBoard getBoard() {
		return board;
	}

	public GettysburgEngine(GettysburgBoard board) 
	{
		this.board = board;
	}

	/*
	 * deprecated
	 * @see gettysburg.common.GbgGame#endBattleStep()
	 */
	@Override
	public void endBattleStep()
	{
		if (currentStep == GbgGameStep.UBATTLE) {
			currentStep = GbgGameStep.CMOVE;
		} else if (currentStep == GbgGameStep.CBATTLE) {
			currentStep = GbgGameStep.GAME_OVER;
		} else {
			throw new GbgInvalidActionException("Invalid action");
		}
	}

	/*
	 * deprecated
	 * @see gettysburg.common.GbgGame#endMoveStep()
	 */
	@Override
	public void endMoveStep()
	{
		if (currentStep == GbgGameStep.UMOVE) {
			currentStep = GbgGameStep.UBATTLE;
		} else if (currentStep == GbgGameStep.CMOVE) {
			currentStep = GbgGameStep.CBATTLE;
		} else {
			throw new GbgInvalidActionException("Invalid action");
		}
	}

	/*
	 * @see gettysburg.common.GbgGame#endStep()
	 */
	@Override
	public GbgGameStep endStep()
	{
		switch (currentStep) {
		case UMOVE:
			board.clearStackedUnits();
			currentStep = GbgGameStep.UBATTLE;
			return currentStep;

		case UBATTLE:
//			if (! isBattleResolved) {
//				throw new GbgInvalidActionException("Must resolve battle");
//			}

			currentStep = GbgGameStep.CMOVE;
			board.initializeUnits(turnNumber, currentStep);
			return currentStep;

		case CMOVE:
			currentStep = GbgGameStep.CBATTLE;
			return currentStep;

		case CBATTLE:
//			if (! isBattleResolved) {
//				throw new GbgInvalidActionException("Must resolve battle");
//			}

			currentStep = GbgGameStep.UMOVE;
			turnNumber += 1;
			board.resetStatus();
			board.initializeUnits(turnNumber, currentStep);
			return currentStep;
		default:
			return GbgGameStep.GAME_OVER;
		}
	}

	/*
	 * @see gettysburg.common.GbgGame#getBattlesToResolve()
	 */
	@Override
	public Collection<BattleDescriptor> getBattlesToResolve()
	{
//		isBattleResolved = true;
		
		if (currentStep != GbgGameStep.CBATTLE && currentStep != GbgGameStep.UBATTLE) {
			throw new GbgInvalidActionException("Invalid action");
		}
		
		
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = board.getMap().entrySet().iterator();
		Set<GbgUnit> attackers = new HashSet<GbgUnit>();
		Set<GbgUnit> defenders = new HashSet<GbgUnit>();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue() == null) continue;
			GbgUnit unit = pair.getValue().iterator().next();
			
			for (Coordinate c: ((GbgUnitImpl) unit).getCurrentZoneControl(pair.getKey())) {
				if (getUnitsAt(c) != null && ! c.equals(whereIsUnit(unit))) {
					GbgUnit unitInZone = getUnitsAt(c).iterator().next();
					if (isBattleTurnOf(unit)) { attackers.add(unit); }
					else { defenders.add(unit); }

					if (isBattleTurnOf(unitInZone)) { attackers.add(unitInZone); }
					else { defenders.add(unitInZone); }
				}
			}
		}
		battles.add(new BattleDescriptorImpl(attackers, defenders));

		return battles;
	}

	/*
	 * @see gettysburg.common.GbgGame#getCurrentStep()
	 */
	@Override
	public GbgGameStep getCurrentStep()
	{
		return currentStep;
	}
	
	/*
	 * @see gettysburg.common.GbgGame#getGameStatus()
	 */
	@Override
	public GbgGameStatus getGameStatus()
	{
		if (currentStep == GbgGameStep.GAME_OVER) {
			return GbgGameStatus.UNION_WINS;
		}
		
		return GbgGameStatus.IN_PROGRESS;
	}
	
	/*
	 * @see gettysburg.common.GbgGame#getGameDate()
	 */
	@Override
	public Calendar getGameDate()
	{
		return new GregorianCalendar(1863, 7, 1 + (turnNumber - 1) / HOURS_IN_ONE_BATTLE_DAY, 6 + (turnNumber - 1) % HOURS_IN_ONE_BATTLE_DAY, 0);
	}

	/*
	 * @see gettysburg.common.GbgGame#getSquareDescriptor(gettysburg.common.Coordinate)
	 */
	@Override
	public GbgSquareDescriptor getSquareDescriptor(Coordinate where)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see gettysburg.common.GbgGame#getTurnNumber()
	 */
	@Override
	public int getTurnNumber()
	{
		return turnNumber;
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitFacing(int)
	 */
	@Override
	public Direction getUnitFacing(GbgUnit unit)
	{
		return unit.getFacing();
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitsAt(gettysburg.common.Coordinate)
	 */
	@Override
	public Collection<GbgUnit> getUnitsAt(Coordinate where)
	{
		return this.board.getMap().get(where);
	}

	/*
	 * @see gettysburg.common.GbgGame#moveUnit(gettysburg.common.GbgUnit, gettysburg.common.Coordinate, gettysburg.common.Coordinate)
	 */
	@Override
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to)
	{
		from = GettysburgFactory.makeCoordinate(from.getX(), from.getY());
		to = GettysburgFactory.makeCoordinate(to.getX(), to.getY());

		if (! whereIsUnit(unit).equals(from) || 
			unit.getMovementFactor() < this.board.shortestPathDistance(unit, from, to) ||
			this.board.getMap().containsKey(to)
		) {
			throw new GbgInvalidMoveException("Invalid movement"); 
		}
		
		if (canUnitMove(unit)) {
			board.moveUnit(unit, from, to);
			board.getMovedStatus().put(unit, true);
		} else {
			throw new GbgInvalidMoveException("Invalid move");
		}
	}

	private boolean canUnitMove(GbgUnit unit) {
		return (
				this.getCurrentStep() == GbgGameStep.UMOVE && 
				unit.getArmy() == ArmyID.UNION &&
				! board.getMovedStatus().get(unit)
			) 
				||
			(
				this.getCurrentStep() == GbgGameStep.CMOVE && 
				unit.getArmy() == ArmyID.CONFEDERATE) &&
				! board.getMovedStatus().get(unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#resolveBattle(int)
	 */
	@Override
	public BattleResolution resolveBattle(BattleDescriptor battle)
	{
		battles.remove(battle);
		return new BattleResolutionImpl(battle);
	}

	/*
	 * @see gettysburg.common.GbgGame#setUnitFacing(gettysburg.common.GbgUnit, gettysburg.common.Direction)
	 */
	@Override
	public void setUnitFacing(GbgUnit unit, Direction direction)
	{
		if ((
				getCurrentStep() == GbgGameStep.UMOVE && 
				unit.getArmy() == ArmyID.UNION &&
				! board.getFacingChangeStatus().get(unit)
			) 
				||
			(
				this.getCurrentStep() == GbgGameStep.CMOVE && 
				unit.getArmy() == ArmyID.CONFEDERATE &&
				! board.getFacingChangeStatus().get(unit)
			))
			{
				unit.setFacing(direction);
				board.getFacingChangeStatus().put(unit, true);
			} else {
				throw new GbgInvalidMoveException("Inappropriate time for this move");
			}
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(gettysburg.common.GbgUnit)
	 */
	@Override
	public Coordinate whereIsUnit(GbgUnit unit)
	{
		return board.whereIsUnit(unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(java.lang.String, gettysburg.common.ArmyID)
	 */
	@Override
	public Coordinate whereIsUnit(String leader, ArmyID army)
	{
		return board.whereIsUnit(leader, army);
	}

	public GbgUnit getUnit(String leader, ArmyID army) 
	{
		Coordinate c = whereIsUnit(leader, army);
		
		if (c == null) {
			return null;
		}
		
		return this.board.getMap().get(c).iterator().next();
		
	}
	
	private boolean isBattleTurnOf(GbgUnit unit) 
	{
		return (this.currentStep == GbgGameStep.CBATTLE && unit.getArmy() == ArmyID.CONFEDERATE) ||
			(this.currentStep == GbgGameStep.UBATTLE && unit.getArmy() == ArmyID.UNION);
	}
	

}
