package gettysburg.engine.unittest;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.EAST;
import static gettysburg.common.Direction.NORTH;
import static gettysburg.common.Direction.NORTHEAST;
import static gettysburg.common.Direction.SOUTH;
import static gettysburg.common.Direction.SOUTHWEST;
import static gettysburg.common.GbgGameStep.CMOVE;
import static gettysburg.common.UnitSize.BATTALION;
import static gettysburg.common.UnitSize.BRIGADE;
import static gettysburg.common.UnitSize.DIVISION;
import static gettysburg.common.UnitType.ARTILLERY;
import static gettysburg.common.UnitType.CAVALRY;
import static gettysburg.common.UnitType.INFANTRY;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgUnit;
import gettysburg.common.TestGbgGame;
import student.gettysburg.engine.common.GbgUnitImpl;
import student.gettysburg.engine.common.GettysburgEngine;

public class Version3Tests 
{
	private GbgGame game;
	private TestGbgGame testGame;
	private GbgUnit gamble, rowley, schurz, devin, heth, rodes, dance, hampton;

	@Before
	public void setup()
	{
		game = testGame = makeTestGame();
		gamble = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
		devin = game.getUnitsAt(makeCoordinate(13, 9)).iterator().next();
		heth = game.getUnitsAt(makeCoordinate(8, 8)).iterator().next();
		// These work if the student kept that GbgUnitImpl.makeUnit method
		rowley = GbgUnitImpl.makeUnit(UNION, 3, NORTHEAST, "Rowley", 2, DIVISION, INFANTRY);
		schurz = GbgUnitImpl.makeUnit(UNION, 2, NORTH, "Schurz", 2, DIVISION, INFANTRY);
		rodes = GbgUnitImpl.makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2, DIVISION, INFANTRY);
		dance = GbgUnitImpl.makeUnit(CONFEDERATE, 2, EAST, "Dance", 4, BATTALION, ARTILLERY);
		hampton = GbgUnitImpl.makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4, BRIGADE, CAVALRY);
		// If the previous statements fail, comment them out and try these
//		rowley = TestUnit.makeUnit(UNION, 3, NORTHEAST, "Rowley", 2);
//		schurz = TestUnit.makeUnit(UNION,  2,  NORTH, "Shurz", 2);
//		rodes = TestUnit.makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2);
//		dance = TestUnit.makeUnit(CONFEDERATE, 2, EAST, "Dance", 4);
//		hampton = TestUnit.makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4);
//		devin.setFacing(SOUTH);
//		gamble.setFacing(WEST);
//		heth.setFacing(EAST);
	}
	
	@Test
	public void unit_can_move_back_to_safe_square()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 2, 1, SOUTH);
		testGame.putUnitAt(devin, 1, 2, NORTH);
		testGame.putUnitAt(gamble, 3, 2, NORTH);
		List<GbgUnit> enemies = new ArrayList<GbgUnit>();
		enemies.add(devin);
		enemies.add(gamble);
		
		// Still have (2,2) to move back
		assertTrue(((GettysburgEngine) game).getBoard().moveBack(heth, enemies));
	}

	@Test
	public void unit_has_no_save_square_to_move_back()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 1, 1, SOUTH);
		testGame.putUnitAt(devin, 1, 2, NORTH);
		testGame.putUnitAt(gamble, 2, 2, NORTH);
		List<GbgUnit> enemies = new ArrayList<GbgUnit>();
		enemies.add(devin);
		enemies.add(gamble);
		
		assertFalse(((GettysburgEngine) game).getBoard().moveBack(heth, enemies));
	}
}
