package gettysburg.engine.unittest;

import org.junit.Test;

import gettysburg.common.Direction;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgUnit;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.CoordinateImpl;

import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;

public class FacingTest
{
	private GbgGame game;

	@Before
	public void setUp() throws Exception 
	{
		game = GettysburgFactory.makeGame();
	}
	
	@Test
	public void it_returns_the_corresponding_time_for_the_turn_number_1()
	{
		Assert.assertEquals(new GregorianCalendar(1863, 7, 1, 6, 0), game.getGameDate());
	}
	
//	@Test
//	public void union_can_change_facing_direction_at_their_turn()
//	{
//		// Default UMove
//		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();
//		game.setUnitFacing(gambleUnit, Direction.NORTHEAST);
//		Assert.assertEquals(Direction.NORTHEAST, gambleUnit.getFacing());
//	}
	
//	@Test(expected = GbgInvalidMoveException.class)
//	public void unit_cannot_change_facing_twice_in_their_turn()
//	{
//		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();
//		game.setUnitFacing(gambleUnit, Direction.NORTHEAST);
//		game.setUnitFacing(gambleUnit, Direction.EAST);
//	}
	
	@Test
	public void confederate_cannot_change_face_during_union_move()
	{
		GbgUnit hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();
		try {
			game.setUnitFacing(hethUnit, Direction.NORTHEAST);
			Assert.fail("invalid move here");
		} catch (GbgInvalidMoveException e) {}

		Assert.assertEquals(Direction.EAST, hethUnit.getFacing());
	}
}
