package gettysburg.engine.unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import gettysburg.common.Coordinate;
import gettysburg.common.Direction;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgUnit;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.CoordinateImpl;

public class ValidMovementTest 
{
	private GbgGame game;

	@Before
	public void setUp() throws Exception 
	{
		game = GettysburgFactory.makeGame();
	}
	
	@Test
	public void coordinate_to_string()
	{
		Assert.assertEquals("(1, 1)", CoordinateImpl.makeCoordinate(1, 1).toString());
	}
	
	@Test(expected = GbgInvalidCoordinateException.class)
	public void coordinate_outside_the_board_is_invalid()
	{
		CoordinateImpl.makeCoordinate(30, 30);
	}
	
	@Test
	public void test_the_distance_between_two_coordinates()
	{
		Coordinate point1 = CoordinateImpl.makeCoordinate(5, 10);
		Coordinate point2 = CoordinateImpl.makeCoordinate(5, 5);
		Assert.assertEquals(5,  point1.distanceTo(point2));

		Coordinate point3 = CoordinateImpl.makeCoordinate(15, 5);
		Coordinate point4 = CoordinateImpl.makeCoordinate(15, 10);
		Assert.assertEquals(5,  point3.distanceTo(point4));

		Coordinate point5 = CoordinateImpl.makeCoordinate(1, 1);
		Coordinate point6 = CoordinateImpl.makeCoordinate(2, 3);
		Assert.assertEquals(2,  point5.distanceTo(point6));
	}
	
	@Test
	public void test_the_direction_between_two_coordinates()
	{
		Coordinate main = CoordinateImpl.makeCoordinate(5, 5);
		Coordinate north = CoordinateImpl.makeCoordinate(5, 4);
		Coordinate northeast = CoordinateImpl.makeCoordinate(6, 4);
		Coordinate east = CoordinateImpl.makeCoordinate(6, 5);
		Coordinate southeast = CoordinateImpl.makeCoordinate(6, 6);
		Coordinate south = CoordinateImpl.makeCoordinate(5, 6);
		Coordinate southwest = CoordinateImpl.makeCoordinate(4, 6);
		Coordinate west = CoordinateImpl.makeCoordinate(4, 5);
		Coordinate northwest = CoordinateImpl.makeCoordinate(4, 4);
		Coordinate none1 = CoordinateImpl.makeCoordinate(7, 6);
		Coordinate none2 = CoordinateImpl.makeCoordinate(1, 6);
		Coordinate none3 = CoordinateImpl.makeCoordinate(6, 8);
		Coordinate none4 = CoordinateImpl.makeCoordinate(1, 6);

		Assert.assertEquals(Direction.NORTH,  main.directionTo(north));
		Assert.assertEquals(Direction.NORTHEAST,  main.directionTo(northeast));
		Assert.assertEquals(Direction.EAST,  main.directionTo(east));
		Assert.assertEquals(Direction.SOUTHEAST,  main.directionTo(southeast));
		Assert.assertEquals(Direction.SOUTH,  main.directionTo(south));
		Assert.assertEquals(Direction.SOUTHWEST,  main.directionTo(southwest));
		Assert.assertEquals(Direction.WEST,  main.directionTo(west));
		Assert.assertEquals(Direction.NORTHWEST,  main.directionTo(northwest));
		Assert.assertEquals(Direction.NONE,  main.directionTo(none1));
		Assert.assertEquals(Direction.NONE,  main.directionTo(none2));
		Assert.assertEquals(Direction.NONE,  main.directionTo(none3));
		Assert.assertEquals(Direction.NONE,  main.directionTo(none4));
	}
	
	@Test(expected = GbgInvalidMoveException.class)
	public void movement_is_invalid_if_unit_is_not_located_at_from_coordinate()
	{
		GbgUnit hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();
		game.moveUnit(hethUnit, CoordinateImpl.makeCoordinate(9, 9), CoordinateImpl.makeCoordinate(11, 11));
	}

	@Test(expected = GbgInvalidMoveException.class)
	public void movement_is_invalid_if_the_distance_is_longer_than_movement_factor()
	{
		GbgUnit hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();
		game.moveUnit(hethUnit, CoordinateImpl.makeCoordinate(8, 8), CoordinateImpl.makeCoordinate(15, 15));
	}

	@Test(expected = GbgInvalidMoveException.class)
	public void movement_is_invalid_if_the_to_coordinate_contains_another_unit()
	{
		GbgUnit hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();
		game.moveUnit(hethUnit, CoordinateImpl.makeCoordinate(8, 8), CoordinateImpl.makeCoordinate(11, 11));
	}

	@Test(expected = GbgInvalidMoveException.class)
	public void unit_cannot_move_twice_in_a_turn()
	{
		GbgUnit gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();

		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 11), CoordinateImpl.makeCoordinate(11, 12));
		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 12), CoordinateImpl.makeCoordinate(11, 10));
	}
}
