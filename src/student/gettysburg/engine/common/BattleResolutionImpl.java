package student.gettysburg.engine.common;

import java.util.Collection;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;

public class BattleResolutionImpl implements BattleResolution 
{
	private BattleDescriptor battle;

	public BattleResolutionImpl(BattleDescriptor battle) {
		this.battle = battle;
	}

	@Override
	public Collection<GbgUnit> getActiveConfederateUnits() {
		if ((getBattleResult() == BattleResult.AELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeAttackers() == ArmyID.UNION)
		) {
			return battle.getDefenders();
		} else if ((getBattleResult() == BattleResult.DELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeDefenders() == ArmyID.UNION)
		) {
			return battle.getAttackers();
		}
		
		return null;
	}

	@Override
	public Collection<GbgUnit> getActiveUnionUnits() {
		if ((getBattleResult() == BattleResult.AELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeAttackers() == ArmyID.CONFEDERATE)
		) {
			return battle.getDefenders();
		} else if ((getBattleResult() == BattleResult.DELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeDefenders() == ArmyID.CONFEDERATE)
		) {
			return battle.getAttackers();
		}
		
		return null;
	}

	@Override
	public BattleResult getBattleResult() {
		int attackingFactor = 0;
		int defendingFactor = 0;

		for (GbgUnit u: battle.getAttackers()) {
			attackingFactor += u.getCombatFactor();
		}
		
		for (GbgUnit u: battle.getDefenders()) {
			defendingFactor += u.getCombatFactor();
		}
		
		double odds = attackingFactor / defendingFactor;
		
		if (odds >= 2.0) {
			return BattleResult.DELIM;
		} else if (odds < 2.0 && odds > 0.5) {
			return BattleResult.EXCHANGE;
		} else {
			return BattleResult.AELIM;
		}
	}

	@Override
	public Collection<GbgUnit> getEliminatedConfederateUnits() {
		if ((getBattleResult() == BattleResult.AELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeAttackers() == ArmyID.CONFEDERATE)
		) {
			return battle.getAttackers();
		} else if ((getBattleResult() == BattleResult.DELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeDefenders() == ArmyID.CONFEDERATE)
		) {
			return battle.getDefenders();
		}
		
		return null;
	}

	@Override
	public Collection<GbgUnit> getEliminatedUnionUnits() {
		if ((getBattleResult() == BattleResult.AELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeAttackers() == ArmyID.UNION)
		) {
			return battle.getAttackers();
		} else if ((getBattleResult() == BattleResult.DELIM && 
			((BattleDescriptorImpl) battle).getArmyTypeDefenders() == ArmyID.UNION)
		) {
			return battle.getDefenders();
		}
		
		return null;
	}

}
