package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.BattleResolutionConfigure;

public class BattleResolutionImpl implements BattleResolution 
{
	private BattleDescriptor battle;
	private GettysburgBoard board;
	private BattleResult result = null;
	private List<GbgUnit> activeConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> activeUnionUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedUnionUnit = new ArrayList<GbgUnit>();

	public BattleResolutionImpl(BattleDescriptor battle) {
		this.battle = battle;
	}

	public void setBoard(GettysburgBoard context) {
		this.board = context;
	}
	
	@Override
	public Collection<GbgUnit> getActiveConfederateUnits() {
		return activeConfederateUnit;
	}

	@Override
	public Collection<GbgUnit> getActiveUnionUnits() {
		return activeUnionUnit;
	}

	@Override
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
//		resolveUnitBackStatus();

		return result;
	}

	@Override
	public Collection<GbgUnit> getEliminatedConfederateUnits() {
		return eliminatedConfederateUnit;
	}

	@Override
	public Collection<GbgUnit> getEliminatedUnionUnits() {
		return eliminatedUnionUnit;
	}
	

	/**
	 * Get the attacking factor of all attacking units
	 * 
	 * @return int
	 */
	private int getAttackingFactor() 
	{
		int attackingFactor = 0;

		for (GbgUnit u: battle.getAttackers()) {
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

		for (GbgUnit u: battle.getAttackers()) {
			defendingFactor += u.getCombatFactor();
		}
		
		return defendingFactor;
	}
	
	private void resolveEliminateStatus() 
	{
		if (result.equals(BattleResult.AELIM)) {
			for (GbgUnit unit: battle.getAttackers()) {
				addEliminated(unit);
			}
			
			for (GbgUnit unit: battle.getDefenders()) {
				addActive(unit);
			}
		} else if (result.equals(BattleResult.DELIM)) {
			for (GbgUnit unit: battle.getDefenders()) {
				addEliminated(unit);
			}

			for (GbgUnit unit: battle.getAttackers()) {
				addActive(unit);
			}
		}
	}

	private void addActive(GbgUnit unit) 
	{
		if (unit.getArmy().equals(ArmyID.UNION)) {
			activeUnionUnit.add(unit);
		} else {
			activeConfederateUnit.add(unit);
		}
	}

	private void addEliminated(GbgUnit unit) {
		if (unit.getArmy().equals(ArmyID.UNION)) {
			eliminatedUnionUnit.add(unit);
		} else {
			eliminatedConfederateUnit.add(unit);
		}
	}

	private void resolveUnitBackStatus()
	{
		if (result.equals(BattleResult.ABACK)) {
			for (GbgUnit unit: battle.getAttackers()) {
				boolean isSuccess = board.moveBack(unit, battle.getDefenders());	
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
				
			}
		} else if (result.equals(BattleResult.DBACK)) {
			for (GbgUnit unit: battle.getDefenders()) {
				boolean isSuccess = board.moveBack(unit, battle.getAttackers());	
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
			}
		}
	}

	private void addMoveBackEliminated(GbgUnit unit, boolean isSuccess) {
		if (! isSuccess && unit.getArmy().equals(ArmyID.UNION)) {
			eliminatedUnionUnit.add(unit);
			board.removeUnit(unit);
		} else if (! isSuccess && unit.getArmy().equals(ArmyID.CONFEDERATE)) {
			eliminatedConfederateUnit.add(unit);
			board.removeUnit(unit);
		}
	}

	private void addMoveBackActive(GbgUnit unit, boolean isSuccess) {
		if (isSuccess && unit.getArmy().equals(ArmyID.UNION)) {
			activeUnionUnit.add(unit);
		} else if (isSuccess && unit.getArmy().equals(ArmyID.CONFEDERATE)) {
			activeConfederateUnit.add(unit);
		}
	}
}
