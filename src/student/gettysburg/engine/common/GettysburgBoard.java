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
	private static final int INFINITE_DISTANE = 999;
	
	/**
	 * Helper method to use in engine
	 * Move the unit from src coordinate to dest coordinate
	 * @param unit
	 * @param from
	 * @param to
	 */
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to)
	{
		Collection<GbgUnit> fromUnits = new ArrayList<GbgUnit>(map.get(from));
		Collection<GbgUnit> units = new ArrayList<GbgUnit>();

		fromUnits.remove(unit);

		units.add(unit);

		if (fromUnits.size() > 0) {
			map.put((CoordinateImpl) GettysburgFactory.makeCoordinate(from.getX(), from.getY()), fromUnits);
		} else {
			map.remove((CoordinateImpl) GettysburgFactory.makeCoordinate(from.getX(), from.getY()));
		}

		map.put((CoordinateImpl) GettysburgFactory.makeCoordinate(to.getX(), to.getY()), units);
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
			if (pair.getValue() != null && pair.getValue().contains(unit)) {
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
			if (pair.getValue() != null && pair.getValue().size() > 1) {
				map.replace(pair.getKey(), null);
			}
		}
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
		return shortestPath(unit, from, to) != null ? shortestPath(unit, from, to).size() - 1 : INFINITE_DISTANE;
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
	 * Get all the unit that must fight
	 * which is in enemy's ZOC
	 * 
	 * @return Set<GbgUnit>
	 */
	public Set<GbgUnit> getUnitsMustFight() {
		Set<GbgUnit> unitsMustFight = new HashSet<GbgUnit>();
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
	
			if (pair.getValue() != null) {
				for (GbgUnit unit: pair.getValue()) {
					if (getAllEnemiesControlledZoneFor(unit) != null &&
						getAllEnemiesControlledZoneFor(unit).contains(whereIsUnit(unit))
					) {
						unitsMustFight.add(unit);
					}
				}
			}
		}	
	
		return unitsMustFight;
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
		ArmyID enemyArmy = unit.getArmy().equals(ArmyID.CONFEDERATE) ? ArmyID.UNION : ArmyID.CONFEDERATE;

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
	    				if (c != null &&
	    					! visited.containsKey(c) && 
	    					! enemiesControlledZone.contains(c) &&
	    					! isThereUnitIn(((GbgUnitImpl) unit).getCurrentZoneControl(c), enemyArmy)
	    				) {
	    					queue.add(c);
	    					visited.put(c, true);
	    					prev.put(c, current);
	    				} else if (c != null && ! visited.containsKey(c) &&
	    						(enemiesControlledZone.contains(c) ||
	    						isThereUnitIn(((GbgUnitImpl) unit).getCurrentZoneControl(c), enemyArmy)) && 
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
	 * 
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
	
	/**
	 * Check if there is a unit of the army in a coordinate
	 * @param coordinates
	 * @param army
	 * @return
	 */
	private boolean isThereUnitIn(Collection<Coordinate> coordinates, ArmyID army)
	{
		Iterator<Coordinate> it = coordinates.iterator();
		
		while(it.hasNext()) {
			Coordinate c = it.next();

			if (getMap().containsKey(c) && 
				getMap().get(c).size() > 0 &&
				getMap().get(c).iterator().next().getArmy().equals(army)
			) {
				return true;
			}
		}	
		
		return false;
	}
	
	/**
	 * Get all coordinate controller by this collection of units
	 * @param units
	 * @return Collection<Coordinate>
	 */
	public Collection<Coordinate> getZOCOfUnits(Collection<GbgUnit> units)
	{
		Set<Coordinate> coordinates = new HashSet<Coordinate>();
		
		for (GbgUnit unit: units) {
			for (Coordinate c: ((GbgUnitImpl) unit).getCurrentZoneControl(whereIsUnit(unit))) {
				coordinates.add(c);
			}
		}

		return coordinates;
	}
	
	/**
	 * Move the unit back 1 free square which is out of enemy zone
	 * 
	 * @param unit
	 * @return true if move successfully, false otherwise
	 */
	public boolean moveBack(GbgUnit unit, Collection<GbgUnit> enemies)
	{
		Coordinate current = whereIsUnit(unit);
		Collection<Coordinate> enemyCoordinates = getZOCOfUnits(enemies);
		
		for (Coordinate moveableCoordinate: ((CoordinateImpl) current).getNeighbors()) {
			if (moveableCoordinate != null && 
				! enemyCoordinates.contains(moveableCoordinate)
			) {
				moveUnit(unit, current, moveableCoordinate);

				return true;
			}
		}

		return false;
	}
	
	/**
	 * Remove the unit from the board
	 * 
	 * @param unit
	 */
	public void removeUnit(GbgUnit unit)
	{
		Iterator<Entry<CoordinateImpl, Collection<GbgUnit>>> it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<CoordinateImpl, Collection<GbgUnit>> pair = (Map.Entry<CoordinateImpl, Collection<GbgUnit>>) it.next();
			if (pair.getValue() != null && pair.getValue().contains(unit)) {
				pair.getValue().remove(unit);

				return;
			}
		}	
	}
}