package gettysburg.engine.unittest;

import org.junit.Test;

import gettysburg.common.ArmyID;
import gettysburg.common.Direction;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgGameStep;
import gettysburg.common.GbgUnit;
import gettysburg.common.UnitSize;
import gettysburg.common.UnitType;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GbgUnitImpl;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;

public class InitializeGbgGameTest
{
	private GbgGame game;

	@Before
	public void setUp() throws Exception 
	{
		game = GettysburgFactory.makeGame();
	}
	
	@Test
	public void gettysburg_factory_can_make_an_instance_of_GbgGame()
	{
		Assert.assertThat(game, instanceOf(GbgGame.class));
	}
	
	@Test
	public void initial_game_has_the_current_turn_number_value_of_1()
	{
		Assert.assertEquals(1, game.getTurnNumber());
	}
	
	@Test
	public void initial_game_has_the_step_at_union_movement_step()
	{
		Assert.assertEquals(GbgGameStep.UMOVE, game.getCurrentStep());
	}
	

	@Test
	public void get_unit_at_returns_null_when_coordiate_has_no_unit()
	{
		Assert.assertNull((ArrayList<GbgUnit>) game.getUnitsAt(CoordinateImpl.makeCoordinate(1, 1)));
	}

	@Test
	public void where_is_unit_return_null_when_coordiate_has_no_unit()
	{
		Assert.assertNull(game.whereIsUnit(new GbgUnitImpl()));
	}

	@Test
	public void test_where_is_unit_with_leader_and_army()
	{
		Assert.assertEquals(11, game.whereIsUnit("Gamble", ArmyID.UNION).getX());
		Assert.assertEquals(11, game.whereIsUnit("Gamble", ArmyID.UNION).getY());
		Assert.assertEquals(13, game.whereIsUnit("Devin", ArmyID.UNION).getX());
		Assert.assertEquals(9, game.whereIsUnit("Devin", ArmyID.UNION).getY());
		Assert.assertEquals(8, game.whereIsUnit("Heth", ArmyID.CONFEDERATE).getX());
		Assert.assertEquals(8, game.whereIsUnit("Heth", ArmyID.CONFEDERATE).getY());
	}
	
	@Test
	public void no_coordinate_for_unit_that_does_not_exist()
	{
		Assert.assertNull(game.whereIsUnit("Huy Tran", ArmyID.CONFEDERATE));
	}
}
