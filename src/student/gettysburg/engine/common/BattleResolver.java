package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.BattleResolutionConfigure;

/**
 * Class contains useful methods for resolve the battle
 * 
 * @author huytran172
 */
public class BattleResolver {
	private GettysburgBoard board;
	private GettysburgEngine game;
	private BattleResolutionImpl battleResolution;
	private BattleResult result;
	private List<GbgUnit> activeConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> activeUnionUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedConfederateUnit = new ArrayList<GbgUnit>();
	private List<GbgUnit> eliminatedUnionUnit = new ArrayList<GbgUnit>();

	public BattleResolver(GettysburgEngine game, BattleResolutionImpl battleResolution) {
		this.game = game;
		this.board = game.getBoard();
		this.battleResolution = battleResolution;
		setBattleResult();
		resolveEliminateStatus();
		resolveUnitBackStatus();
		resolveExchangeStatus();
	}

	/**
	 * Getter
	 */
	public List<GbgUnit> getActiveConfederateUnit() {
		return activeConfederateUnit;
	}

	/**
	 * Getter
	 */
	public List<GbgUnit> getActiveUnionUnit() {
		return activeUnionUnit;
	}

	/**
	 * Getter
	 */
	public List<GbgUnit> getEliminatedConfederateUnit() {
		return eliminatedConfederateUnit;
	}

	/**
	 * Getter
	 */
	public List<GbgUnit> getEliminatedUnionUnit() {
		return eliminatedUnionUnit;
	}

	public BattleResult getBattleResult() 
	{
		return result;
	}

	private void setBattleResult() {
		int attackingFactor = getAttackingFactor();
		int defendingFactor = getDefendingFactor();
		// if (abs(a - b) < 1e-12) {
		//
		// }
		double br = defendingFactor != 0 ? attackingFactor * 1.0 / defendingFactor : 10;

		int n = game.getRandomNumber();

		BattleResult[][] battleResult = BattleResolutionConfigure.battleResolution;
		
		if (br >= 6.0) {
			result = battleResult[0][n];
		} else if (br < 6.0 && br >= 5.0) {
			result = battleResult[1][n];
		} else if (br < 5.0 && br >= 4.0) {
			result = battleResult[2][n];
		} else if (br < 4.0 && br >= 3.0) {
			result = battleResult[3][n];
		} else if (br < 3.0 && br >= 2.0) {
			result = battleResult[4][n];
		} else if (br < 2.0 && br >= 1.0) {
			result = battleResult[5][n];
		} else if (br < 1.0 && br >= 0.5) {
			result = battleResult[6][n];
		} else if (br < 0.5 && br >= 0.333) {
			result = battleResult[7][n];
		} else if (br < 0.333 && br >= 0.25) {
			result = battleResult[8][n];
		} else if (br < 0.25 && br >= 0.20) {
			result = battleResult[9][n];
		} else if (br < 0.20 && br >= 0.167) {
			result = battleResult[10][n];
		} else {
			result = battleResult[11][n];
		}

		if (game.getResults() != null) {
			result = game.getResults().get(0);
		}
	}

	/**
	 * Resolve the unit in the board in an board
	 */
	private void resolveExchangeStatus() 
	{
		if (result.equals(BattleResult.EXCHANGE)) {
			int attackingFactor = getAttackingFactor();
			int defendingFactor = getDefendingFactor();
			if (attackingFactor > defendingFactor) {
				for (GbgUnit unit : battleResolution.getBattle().getDefenders()) {
					addEliminated(unit);
				}
				addAndRemoveExchangeUnit(defendingFactor, true);

			} else {
				for (GbgUnit unit : battleResolution.getBattle().getAttackers()) {
					addEliminated(unit);
				}

				addAndRemoveExchangeUnit(attackingFactor, false);
			}
		}
	}

	/**
	 * Resolve BACK battle status
	 * 
	 * Move the unit back If there is no available square, remove the unit from the
	 * board and add them to the eliminated list
	 */
	private void resolveUnitBackStatus() {
		if (result.equals(BattleResult.ABACK)) {
			for (GbgUnit unit : battleResolution.getBattle().getAttackers()) {
				boolean isSuccess = board.moveBack(unit, battleResolution.getBattle().getDefenders());
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
			}
			
			for (GbgUnit unit: battleResolution.getBattle().getDefenders()) {
				addActive(unit);
			}
		} else if (result.equals(BattleResult.DBACK)) {
			for (GbgUnit unit : battleResolution.getBattle().getDefenders()) {
				boolean isSuccess = board.moveBack(unit, battleResolution.getBattle().getAttackers());
				addMoveBackActive(unit, isSuccess);
				addMoveBackEliminated(unit, isSuccess);
			}

			for (GbgUnit unit: battleResolution.getBattle().getAttackers()) {
				addActive(unit);
			}
		}
	}

	/**
	 * Resolve the units when the battleResolution.getBattle() has ELIM status
	 * 
	 * Add eliminated units to eliminated list and remove them from the board Add
	 * active units to active list
	 */
	private void resolveEliminateStatus() {
		if (result.equals(BattleResult.AELIM)) {
			for (GbgUnit unit : battleResolution.getBattle().getAttackers()) {
				addEliminated(unit);
			}

			for (GbgUnit unit : battleResolution.getBattle().getDefenders()) {
				addActive(unit);
			}
		} else if (result.equals(BattleResult.DELIM)) {
			for (GbgUnit unit : battleResolution.getBattle().getDefenders()) {
				addEliminated(unit);
			}

			for (GbgUnit unit : battleResolution.getBattle().getAttackers()) {
				addActive(unit);
			}
		}
	}

	/**
	 * Get the attacking factor of all attacking units
	 * 
	 * @return int
	 */
	private int getAttackingFactor() {
		int attackingFactor = 0;

		for (GbgUnit u : battleResolution.getBattle().getAttackers()) {
			attackingFactor += u.getCombatFactor();
		}

		return attackingFactor;
	}

	/**
	 * Get defending factor of all defending units
	 * 
	 * @return int
	 */
	private int getDefendingFactor() {
		int defendingFactor = 0;

		for (GbgUnit u : battleResolution.getBattle().getDefenders()) {
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
	private void addActive(GbgUnit unit) {
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
		if (!isSuccess && unit.getArmy().equals(ArmyID.UNION)) {
			eliminatedUnionUnit.add(unit);
			board.removeUnit(unit);
		} else if (!isSuccess && unit.getArmy().equals(ArmyID.CONFEDERATE)) {
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
	 * removes a number of units whose combined combat factors total at least that
	 * of the units removed by the opponent then, add other units to active list
	 * 
	 * @param enemyFactor
	 * @param isAttack
	 */
	private void addAndRemoveExchangeUnit(int enemyFactor, boolean isAttack) {
		// smallest factor bigger than enemy factor
		int smallest = 0;
		List<GbgUnit> unitsToBeRemoved = new ArrayList<GbgUnit>();

		ArrayList<GbgUnit> units = isAttack ? (ArrayList<GbgUnit>) battleResolution.getBattle().getAttackers()
				: (ArrayList<GbgUnit>) battleResolution.getBattle().getDefenders();

		int n = units.size();

		for (int i = 0; i < n; i++) {
			smallest += units.get(i).getCombatFactor();
		}

		smallest += 1;

		for (int i = 0; i < (1 << n); i++) {
			int current = 0;
			List<GbgUnit> currentUnits = new ArrayList<GbgUnit>();
			for (int j = 0; j < n; j++) {
				if ((i & (1 << j)) > 0) {
					current += units.get(j).getCombatFactor();
					currentUnits.add(units.get(j));
				}
			}

			if (current < smallest && current > enemyFactor) {
				smallest = current;
				unitsToBeRemoved = new ArrayList<GbgUnit>(currentUnits);
			}
		}

		for (GbgUnit unit : units) {
			if (unitsToBeRemoved.contains(unit)) {
				addEliminated(unit);
			} else {
				addActive(unit);
			}
		}
	}
}
