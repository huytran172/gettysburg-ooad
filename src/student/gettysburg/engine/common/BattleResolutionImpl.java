package student.gettysburg.engine.common;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;

public class BattleResolutionImpl implements BattleResolution 
{
	private BattleDescriptor battle;
	private BattleResolver resolver;

	public BattleResolutionImpl(BattleDescriptor battle, GettysburgBoard board) {
		this.battle = battle;
		resolver = new BattleResolver(board, this);
	}

	@Override
	public Collection<GbgUnit> getActiveConfederateUnits() {
		return resolver.getActiveConfederateUnit();
	}

	@Override
	public Collection<GbgUnit> getActiveUnionUnits() {
		return resolver.getActiveUnionUnit();
	}

	@Override
	public BattleResult getBattleResult() {
		return resolver.getBattleResult();
	}

	@Override
	public Collection<GbgUnit> getEliminatedConfederateUnits() {
		return resolver.getEliminatedConfederateUnit();
	}

	@Override
	public Collection<GbgUnit> getEliminatedUnionUnits() {
		return resolver.getEliminatedUnionUnit();
	}

	public List<GbgUnit> getActiveConfederateUnit() {
		return resolver.getActiveConfederateUnit();
	}

	public List<GbgUnit> getActiveUnionUnit() {
		return resolver.getActiveUnionUnit();
	}

	public List<GbgUnit> getEliminatedConfederateUnit() {
		return resolver.getEliminatedConfederateUnit();
	}

	public List<GbgUnit> getEliminatedUnionUnit() {
		return resolver.getEliminatedUnionUnit();
	}

	public BattleDescriptor getBattle() {
		return battle;
	}
}