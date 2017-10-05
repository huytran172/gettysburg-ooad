package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import gettysburg.common.ArmyID;
import gettysburg.common.Coordinate;
import gettysburg.common.GbgGameStep;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.utility.configure.BattleOrder;
import student.gettysburg.engine.utility.configure.UnitInitializer;

public class GettysburgBoard 
{
	private Map<CoordinateImpl, Collection<GbgUnit>> map = new HashMap<CoordinateImpl, Collection<GbgUnit>>();
	private Map<GbgUnit, Boolean> facingChangeStatus = new HashMap<GbgUnit, Boolean>();
	private Map<GbgUnit, Boolean> movedStatus = new HashMap<GbgUnit, Boolean>();
	
	/**
	 * Helper method to use in engine
	 * Move the unit from src coordinate to dest coordinate
	 * @param unit
	 * @param from
	 * @param to
	 */
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to)
	{
		map.remove(from);
		Collection<GbgUnit> units = new ArrayList<GbgUnit>();
		units.add(unit);
		map.remove(from);
		map.put((CoordinateImpl) to, units);
	}
	
	/**
	 * Getter
	 */
	public Map<GbgUnit, Boolean> getFacingChangeStatus() 
	{
		return facingChangeStatus;
	}

	/**
	 * Getter
	 */
	public Map<GbgUnit, Boolean> getMovedStatus() 
	{
		return movedStatus;
	}

	/**
	 * Getter
	 */
	public Map<CoordinateImpl, Collection<GbgUnit>> getMap() 
	{
		return map;
	}

	/**
	 * Get the coordinate of unit
	 * @param leader
	 * @param army
	 * @return Coordinate
	 */
	public Coordinate whereIsUnit(GbgUnit unit)
	{
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue().contains(unit)) {
				return pair.getKey();
			}
		}
		
		return null;
	}
	
	/**
	 * Get the coordinate of unit
	 * @param leader
	 * @param army
	 * @return Coordinate
	 */
	public Coordinate whereIsUnit(String leader, ArmyID army)
	{

		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			for (GbgUnit unit: pair.getValue()) {
				if (unit.getArmy().equals(army) && unit.getLeader().equals(leader)) {
					return pair.getKey();
				}
			}
		}
		
		return null;
	}

	/**
	 * Initialize the reinforcement to the map
	 * @param turn current turn
	 * @param step current step
	 */
	public void initializeUnits(int turn, GbgGameStep step)
	{
		Set<Coordinate> occupied = getAllOccupiedSquare();
		if (step == GbgGameStep.CMOVE) {
			for (UnitInitializer ui: BattleOrder.getConfederateBattleOrder()) {
				if (ui.turn == turn && ! occupied.contains(ui.where)) {
					putUnitsToBoard(ui);
				}
			}
		} else if (step == GbgGameStep.UMOVE) {
			for (UnitInitializer ui: BattleOrder.getUnionBattleOrder()) {
				if (ui.turn == turn && ! occupied.contains(ui.where)) {
					putUnitsToBoard(ui);
				}
			}
		}
		
	}
	
	/**
	 * Clear stacked unit if they do not move before the move step ends
	 */
	public void clearStackedUnits()
	{
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue().size() > 1) {
				map.replace(pair.getKey(), null);
			}
		}
		
		it.remove();
	}
	
	/**
	 * Reset the facing and moving status of unit
	 */
	public void resetStatus() {
		Iterator<Entry<GbgUnit, Boolean>> it1 = movedStatus.entrySet().iterator();
		
		while (it1.hasNext()) {
			Map.Entry<GbgUnit, Boolean> pair = (Map.Entry<GbgUnit, Boolean>) it1.next();
			pair.setValue(false);
		}

		Iterator<Entry<GbgUnit, Boolean>> it2 = facingChangeStatus.entrySet().iterator();
		
		while (it2.hasNext()) {
			Map.Entry<GbgUnit, Boolean> pair = (Map.Entry<GbgUnit, Boolean>) it2.next();
			pair.setValue(false);
		}
	}

	/**
 	 * Shortest distance of a unit from the source coordinate to destination coordinate
	 * @param unit
	 * @param from
	 * @param to
	 * @return distance || infinite distance
	 */
	public int shortestPathDistance(GbgUnit unit, Coordinate from, Coordinate to)
	{
		return shortestPath(unit, from, to) != null ? shortestPath(unit, from, to).size() - 1 : 999;
	}

	/**
	 * Get enemy controlled zone for a unit
	 * @param unit
	 * @return Set of coordinate
	 */
	public Set<Coordinate> getAllEnemiesControlledZoneFor(GbgUnit unit) 
	{
		HashSet<Coordinate> controlledCoordinates = new HashSet<Coordinate>();
		ArmyID unitArmy = unit.getArmy();
		ArmyID enemyArmy = unitArmy == ArmyID.UNION ? ArmyID.CONFEDERATE : ArmyID.UNION;
		
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue() != null) {
				for (GbgUnit u: pair.getValue()) {
					if (u.getArmy() == enemyArmy) {
						for (Coordinate c: ((GbgUnitImpl) u).getCurrentZoneControl(pair.getKey())) {
							controlledCoordinates.add(c);
						}
					}
				}
			}
		}
		
		return controlledCoordinates;
	}
	
	/**
	 * Get all occupied square by units on the board
	 * @return
	 */
	private Set<Coordinate> getAllOccupiedSquare()
	{
		HashSet<Coordinate> occupied = new HashSet<Coordinate>();
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue() != null) {
				occupied.add(pair.getKey());
			}
		}	
		
		return occupied;
	}
	
	/**
	 * Shortest path of a unit from the source coordinate to destination coordinate
	 * @param unit
	 * @param from
	 * @param to
	 * @return LinkedList path to destination
	 */
	private List<Coordinate> shortestPath(GbgUnit unit, Coordinate from, Coordinate to) 
	{
		from = GettysburgFactory.makeCoordinate(from.getX(), from.getY());
		to = GettysburgFactory.makeCoordinate(to.getX(), to.getY());

		Set<Coordinate> enemiesControlledZone = getAllEnemiesControlledZoneFor(unit);
		
		Map<Coordinate, Boolean> visited = new HashMap<Coordinate, Boolean>();
		Map<Coordinate, Coordinate> prev = new HashMap<Coordinate, Coordinate>();
	    List<Coordinate> directions = new LinkedList<Coordinate>();
	    Queue<Coordinate> queue = new LinkedList<Coordinate>();
	    
	    Coordinate current = from;
	    queue.add(current);
	    visited.put(current, true);
	    
	    while (! queue.isEmpty()) {
	    		current = queue.remove();
	    		
	    		if (current.equals(to)) { break; }
	    		else {
	    			for (Coordinate c: ((CoordinateImpl) current).getNeighbors()) {
	    				if (! visited.containsKey(c) && 
	    					! enemiesControlledZone.contains(c) &&
	    					c != null
	    				) {
	    					queue.add(c);
	    					visited.put(c, true);
	    					prev.put(c, current);
	    				} else if (! visited.containsKey(c) &&
	    						enemiesControlledZone.contains(c) &&
	    						to.equals(c)
	    				) {
	    					queue.add(c);
	    					visited.put(c, true);
	    					prev.put(c, current);
	    				}
	    			}
	    		}
	    }
	    
	    if (! current.equals(to)) {
	    		return null;
	    }
	    
	    for (Coordinate c = to; c != null; c = prev.get(c)) {
	    		directions.add(c);
	    }
	    
	    return directions;
	}
	
	/**
	 * Helper method to put unit in unit intializer to the board
	 * @param ui
	 */
	private void putUnitsToBoard(UnitInitializer ui) {
		if (getMap().containsKey(ui.where)) {
			getMap().get(ui.where).add(ui.unit);
		} else {
			Collection<GbgUnit> units = new ArrayList<GbgUnit>();
			units.add(ui.unit);
			getMap().put((CoordinateImpl) GettysburgFactory.makeCoordinate(ui.where.getX(),  ui.where.getY()), units);
		}
		getMovedStatus().put(ui.unit, false);
		getFacingChangeStatus().put(ui.unit, false);
	}
	
}