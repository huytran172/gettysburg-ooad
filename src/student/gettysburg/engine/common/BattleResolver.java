package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.BattleResolutionConfigure;

/**
 * Class contains useful methods for resolve the battle
 * 
 * @author huytran172
 */
public class BattleResolver 
{
	private GettysburgBoard board;
	private BattleResolutionImpl bsi;
	private BattleResult result = null;
	private List<GbgUnit> activeConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> activeUnionUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedUnionUnit = new ArrayList<GbgUnit>();
	

	public BattleResolver(GettysburgBoard board, BattleResolutionImpl bsi) {
		this.board = board;
		this.bsi = bsi;
	}

	public List<GbgUnit> getActiveConfederateUnit() {
		return activeConfederateUnit;
	}

	public List<GbgUnit> getActiveUnionUnit() {
		return activeUnionUnit;
	}

	public List<GbgUnit> getEliminatedConfederateUnit() {
		return eliminatedConfederateUnit;
	}

	public List<GbgUnit> getEliminatedUnionUnit() {
		return eliminatedUnionUnit;
	}

	public BattleResult getBattleResult() {
		int attackingFactor = getAttackingFactor();
		int defendingFactor = getDefendingFactor();

		double br = defendingFactor != 0 ? 
				attackingFactor * 1.0 / defendingFactor : 10;

//		Random rand = new Random();
	
		int n = 0;
		BattleResult[][] battleResult = BattleResolutionConfigure.battleResolution;
		
		if (br >= 6.0) {
			result = battleResult[0][n];
		} 
		else if (br < 6.0 && br >= 5.0) {
			result = battleResult[1][n];
		}
		else if (br < 5.0 && br >= 4.0) {
			result = battleResult[2][n];
		}
		else if (br < 4.0 && br >= 3.0) {
			result = battleResult[3][n];
		}
		else if (br < 3.0 && br >= 2.0) {
			result = battleResult[4][n];
		}
		else if (br < 2.0 && br >= 1.0) {
			result = battleResult[5][n];
		}
		else if (br < 1.0 && br >= 0.5) {
			result = battleResult[6][n];
		}
		else if (br < 0.5 && br >= 0.333) {
			result = battleResult[7][n];
		}
		else if (br < 0.333 && br >= 0.25) {
			result = battleResult[8][n];
		}
		else if (br < 0.25 && br >= 0.20) {
			result = battleResult[9][n];
		}
		else if (br < 0.20 && br >= 0.167) {
			result = battleResult[10][n];
		}
		else {
			result = battleResult[11][n];
		}

		resolveEliminateStatus();
		resolveUnitBackStatus();
		resolveExchangeStatus();

		return result;
	}

	/**
	 * Resolve the unit in the board in an board
	 */
	public void resolveExchangeStatus() 
	{
		int attackingFactor = getAttackingFactor();
		int defendingFactor = getDefendingFactor();
	
		if (result.equals(BattleResult.EXCHANGE)) {
			if (attackingFactor > defendingFactor) {
				for (GbgUnit unit: bsi.getBattle().getDefenders()) {
					addEliminated(unit);
				}
				
				addAndRemoveExchangeUnit(defendingFactor, true);
				
				
			} else {
				for (GbgUnit unit: bsi.getBattle().getAttackers()) {
					addEliminated(unit);
				}
	
				addAndRemoveExchangeUnit(attackingFactor, false);
			}
		}
	}

	/**
	 * Resolve BACK battle status
	 * 
	 * Move the unit back
	 * If there is no available square, remove the unit from the board
	 * and add them to the eliminated list
	 */
	public void resolveUnitBackStatus()
	{
		if (result.equals(BattleResult.ABACK)) {
			for (GbgUnit unit: bsi.getBattle().getAttackers()) {
				boolean isSuccess = board.moveBack(unit, bsi.getBattle().getDefenders());	
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
				
			}
		} else if (result.equals(BattleResult.DBACK)) {
			for (GbgUnit unit: bsi.getBattle().getDefenders()) {
				boolean isSuccess = board.moveBack(unit, bsi.getBattle().getAttackers());	
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
			}
		}
	}

	/**
	 * Resolve the units when the bsi.getBattle() has ELIM status
	 * 
	 * Add eliminated units to eliminated list and remove them from the board
	 * Add active units to active list
	 */
	public void resolveEliminateStatus() 
	{
		if (result.equals(BattleResult.AELIM)) {
			for (GbgUnit unit: bsi.getBattle().getAttackers()) {
				addEliminated(unit);
			}
			
			for (GbgUnit unit: bsi.getBattle().getDefenders()) {
				addActive(unit);
			}
		} else if (result.equals(BattleResult.DELIM)) {
			for (GbgUnit unit: bsi.getBattle().getDefenders()) {
				addEliminated(unit);
			}
	
			for (GbgUnit unit: bsi.getBattle().getAttackers()) {
				addActive(unit);
			}
		}
	}

	/**
	 * Get the attacking factor of all attacking units
	 * 
	 * @return int
	 */
	private int getAttackingFactor() 
	{
		int attackingFactor = 0;
	
		for (GbgUnit u: bsi.getBattle().getAttackers()) {
			attackingFactor += u.getCombatFactor();
		}
		
		return attackingFactor;
	}

	/**
	 * Get defending factor of all defending units
	 * 
	 * @return int
	 */
	private int getDefendingFactor() 
	{
		int defendingFactor = 0;
	
		for (GbgUnit u: bsi.getBattle().getAttackers()) {
			defendingFactor += u.getCombatFactor();
		}
		
		return defendingFactor;
	}

	/**
	 * HELPER METHOD
	 * 
	 * Add unit to active list based on their army
	 * 
	 * @param unit
	 */
	private void addActive(GbgUnit unit) 
	{
		if (unit.getArmy().equals(ArmyID.UNION)) {
			activeUnionUnit.add(unit);
		} else {
			activeConfederateUnit.add(unit);
		}
	}

	/**
	 * HELPER METHOD
	 * 
	 * Add unit to eliminated list based on their army
	 * 
	 * @param unit
	 */
	private void addEliminated(GbgUnit unit) {
		if (unit.getArmy().equals(ArmyID.UNION)) {
			eliminatedUnionUnit.add(unit);
			board.removeUnit(unit);
		} else {
			eliminatedConfederateUnit.add(unit);
			board.removeUnit(unit);
		}
	}

	/**
	 * HELPER METHOD
	 * 
	 * Remove unsuccessful backing unit
	 * 
	 * @param unit
	 * @param isSuccess
	 */
	private void addMoveBackEliminated(GbgUnit unit, boolean isSuccess) {
		if (! isSuccess && unit.getArmy().equals(ArmyID.UNION)) {
			eliminatedUnionUnit.add(unit);
			board.removeUnit(unit);
		} else if (! isSuccess && unit.getArmy().equals(ArmyID.CONFEDERATE)) {
			eliminatedConfederateUnit.add(unit);
			board.removeUnit(unit);
		}
	}

	/**
	 * HELPER METHOD
	 * 
	 * Add unit to active list those unit move back successfully
	 * 
	 * @param unit
	 * @param isSuccess
	 */
	private void addMoveBackActive(GbgUnit unit, boolean isSuccess) {
		if (isSuccess && unit.getArmy().equals(ArmyID.UNION)) {
			activeUnionUnit.add(unit);
		} else if (isSuccess && unit.getArmy().equals(ArmyID.CONFEDERATE)) {
			activeConfederateUnit.add(unit);
		}
	}

	/**
	 * removes a number of units whose combined combat factors 
	 * total at least that of the units removed by the opponent
	 * then, add other units to active list
	 * 
	 * @param enemyFactor 
	 * @param isAttack
	 */
	private void addAndRemoveExchangeUnit(int enemyFactor, boolean isAttack) {
		int i = 0;
	
		Iterator<GbgUnit> it = isAttack ? bsi.getBattle().getAttackers().iterator() : bsi.getBattle().getAttackers().iterator();
	
		while (it.hasNext()) {
			GbgUnit unit = it.next();
	
			if (i < enemyFactor) {
				i += unit.getCombatFactor();
				addEliminated(unit);
			} else {
				addActive(unit);
			}
		}
	}
	
}
