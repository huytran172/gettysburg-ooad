package gettysburg.engine.unittest;

import org.junit.Test;

import gettysburg.common.Direction;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgGameStep;
import gettysburg.common.GbgUnit;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.CoordinateImpl;

import org.junit.Assert;
import org.junit.Before;

public class PlaySequenceTest
{
	private GbgGame game;

	@Before
	public void setUp() throws Exception 
	{
		game = GettysburgFactory.makeGame();
	}
	
	@Test
	public void union_player_can_move_zero_or_more_units_at_their_turn()
	{
		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();
		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 11), CoordinateImpl.makeCoordinate(11, 10));
//		game.setUnitFacing(gambleUnit, Direction.NORTHEAST);
//		Assert.assertEquals(Direction.NORTHEAST, gambleUnit.getFacing());
		Assert.assertEquals(11, game.whereIsUnit(gambleUnit).getX());
		Assert.assertEquals(10, game.whereIsUnit(gambleUnit).getY());
		
		
		GbgUnit devinUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(13, 9)).iterator().next();
		game.moveUnit(devinUnit, CoordinateImpl.makeCoordinate(13, 9), CoordinateImpl.makeCoordinate(14, 8));
		game.setUnitFacing(devinUnit, Direction.WEST);
		Assert.assertEquals(Direction.WEST, devinUnit.getFacing());
		Assert.assertEquals(14, game.whereIsUnit(devinUnit).getX());
		Assert.assertEquals(8, game.whereIsUnit(devinUnit).getY());
	}
	
	@Test(expected = GbgInvalidMoveException.class)
	public void union_can_make_invalid_first_move()
	{
		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();
		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 11), CoordinateImpl.makeCoordinate(11, 18));
	}
	
	@Test(expected = GbgInvalidMoveException.class)
	public void union_and_confederate_player_cannot_do_any_action_during_combat_phases()
	{
		game.endStep();
		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();

		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 11), CoordinateImpl.makeCoordinate(11, 10));
	}
	
	@Test
	public void confederate_player_can_move_zero_or_more_units_at_their_turn()
	{
		game.endMoveStep();
		game.endBattleStep();
		
		GbgUnit hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();
		game.moveUnit(hethUnit, CoordinateImpl.makeCoordinate(8, 8), CoordinateImpl.makeCoordinate(9, 10));
		game.setUnitFacing(hethUnit, Direction.NORTHEAST);
		Assert.assertEquals(Direction.NORTHEAST, hethUnit.getFacing());
		Assert.assertEquals(9, game.whereIsUnit(hethUnit).getX());
		Assert.assertEquals(10, game.whereIsUnit(hethUnit).getY());
	}
	
	@Test
	public void test_end_move_and_battle_step()
	{
		game.endMoveStep();
		Assert.assertEquals(GbgGameStep.UBATTLE, game.getCurrentStep());
		game.endBattleStep();
		Assert.assertEquals(GbgGameStep.CMOVE, game.getCurrentStep());
		game.endMoveStep();
		Assert.assertEquals(GbgGameStep.CBATTLE, game.getCurrentStep());
		game.endBattleStep();
		Assert.assertEquals(GbgGameStep.GAME_OVER, game.getCurrentStep());
	}
	
	@Test
	public void test_end_step()
	{
		
		Assert.assertEquals(GbgGameStep.UBATTLE, game.endStep());
		Assert.assertEquals(GbgGameStep.CMOVE, game.endStep());
		Assert.assertEquals(GbgGameStep.CBATTLE, game.endStep());
	}
}
