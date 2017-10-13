package gettysburg.engine.unittest;

import static gettysburg.common.ArmyID.*;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStep.*;
import static gettysburg.common.UnitSize.*;
import static gettysburg.common.UnitType.*;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gettysburg.common.*;
import gettysburg.engine.common.TestGettysburgEngine;
import student.gettysburg.engine.common.BattleResolutionImpl;
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
		List<BattleResult> bs = new ArrayList<BattleResult>();
		bs.add(BattleResult.ABACK);
		testGame.setBattleResults(bs);
		testGame.setGameStep(GbgGameStep.CMOVE);
		game.endStep();		
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		BattleResolution result = game.resolveBattle(battle);
		assertEquals(BattleResult.ABACK, result.getBattleResult());
		assertEquals(1, result.getEliminatedConfederateUnits().size());
	}
	
	@Test
	public void test_EXCHANGE_attack_factor_is_smaller_than_defend_factor()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 1, 1, SOUTH);
		testGame.putUnitAt(rowley, 1, 2, NORTH);
		testGame.putUnitAt(schurz, 2, 2, NORTH);
		List<BattleResult> bs = new ArrayList<BattleResult>();
		bs.add(BattleResult.EXCHANGE);
		testGame.setBattleResults(bs);
		testGame.setGameStep(GbgGameStep.CMOVE);
		game.endStep();		// CBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		BattleResolution result = game.resolveBattle(battle);
		assertEquals(BattleResult.EXCHANGE, result.getBattleResult());
		assertEquals(0, result.getActiveConfederateUnits().size());
		assertEquals(0, result.getActiveUnionUnits().size());
		assertEquals(1, result.getEliminatedConfederateUnits().size());
		assertEquals(2, result.getEliminatedUnionUnits().size());
	}
	
	@Test
	public void test_EXCHANGE_attack_factor_is_bigger_than_defend_factor()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 1, 1, SOUTH);
		testGame.putUnitAt(rowley, 1, 2, NORTH);
		testGame.putUnitAt(schurz, 2, 2, NORTH);
		List<BattleResult> bs = new ArrayList<BattleResult>();
		bs.add(BattleResult.EXCHANGE);
		testGame.setBattleResults(bs);
		testGame.setGameStep(GbgGameStep.UMOVE);
		game.endStep();		// UBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		BattleResolution result = game.resolveBattle(battle);
		assertEquals(BattleResult.EXCHANGE, result.getBattleResult());
		assertEquals(0, result.getActiveConfederateUnits().size());
		assertEquals(0, result.getActiveUnionUnits().size());
		assertEquals(1, result.getEliminatedConfederateUnits().size());
		assertEquals(2, result.getEliminatedUnionUnits().size());
	}

	@Test
	public void test_DBACK_status()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.clearBoard();
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		assertEquals(makeCoordinate(5, 5), testGame.whereIsUnit(heth));
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		assertEquals(makeCoordinate(5, 7), testGame.whereIsUnit(devin));
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		assertEquals(makeCoordinate(5, 6), testGame.whereIsUnit(heth));
		game.endStep();		// CBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		((TestGettysburgEngine) testGame).setRandomNum(3);
		BattleResolution bs = game.resolveBattle(battle);
		
		// Combat factor: 4/1 random 3 -> DBACK

		assertEquals(BattleResult.DBACK, bs.getBattleResult());
		assertNotEquals(makeCoordinate(5, 7), testGame.whereIsUnit(devin));

		assertEquals(heth, bs.getActiveConfederateUnits().iterator().next());
		assertEquals(devin, bs.getActiveUnionUnits().iterator().next());
	}

	@Test
	public void test_ABACK_status()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.clearBoard();
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		assertEquals(makeCoordinate(5, 5), testGame.whereIsUnit(heth));
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		assertEquals(makeCoordinate(5, 7), testGame.whereIsUnit(devin));
		testGame.setGameStep(UMOVE);
		game.moveUnit(devin, makeCoordinate(5,7), makeCoordinate(5, 6));
		assertEquals(makeCoordinate(5, 6), testGame.whereIsUnit(devin));
		game.endStep();		// CBATTLE
		((TestGettysburgEngine) testGame).setRandomNum(3);
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		BattleResolution bs = game.resolveBattle(battle);
		
		// Combat factor: 1/4 random 3 -> ABACK

		assertEquals(BattleResult.ABACK, bs.getBattleResult());
		assertNotEquals(makeCoordinate(5, 7), testGame.whereIsUnit(devin));

		assertEquals(heth, bs.getActiveConfederateUnits().iterator().next());
		assertEquals(devin, bs.getActiveUnionUnits().iterator().next());
	}
	
	@Test
	public void test_AELIM_status()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.clearBoard();
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 6, NORTH);
		testGame.putUnitAt(gamble, 4, 5, EAST);
		testGame.setGameStep(UMOVE);
		game.endStep();		// UBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		((TestGettysburgEngine) testGame).setRandomNum(5);
		BattleResolution result = game.resolveBattle(battle);
		assertEquals(BattleResult.AELIM, result.getBattleResult());
		assertEquals(1, result.getActiveConfederateUnits().size());
		assertEquals(0, result.getActiveUnionUnits().size());
		assertEquals(0, result.getEliminatedConfederateUnits().size());
		assertEquals(2, result.getEliminatedUnionUnits().size());
	}
}
