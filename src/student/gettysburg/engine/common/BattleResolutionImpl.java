package student.gettysburg.engine.common;

import java.util.Collection;
import java.util.List;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;

public class BattleResolutionImpl implements BattleResolution 
{
	private BattleDescriptor battle;
	private BattleResolver resolver;

	public BattleResolutionImpl(BattleDescriptor battle, GettysburgEngine game) {
		this.battle = battle;
		resolver = new BattleResolver(game, this);
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

	public BattleDescriptor getBattle() {
		return battle;
	}
}