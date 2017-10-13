/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright Â©2016 Gary F. Pollice
 *******************************************************************************/

package gettysburg.engine.unittest;

import static gettysburg.common.ArmyID.*;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStep.*;
import static student.gettysburg.engine.GettysburgFactory.*;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.*;
import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import student.gettysburg.engine.common.GbgUnitImpl;
import student.gettysburg.engine.common.GettysburgBoard;
import student.gettysburg.engine.common.GettysburgEngine;

/**
 * Test cases for release 2.
 * @version Sep 30, 2017
 */
public class Version2MasterTests
{
	private GbgGame game;
	private TestGbgGame testGame;
	private GbgUnit gamble, devin, heth;

	@Before
	public void setup()
	{
		game = testGame = makeTestGame();
		gamble = game.getUnit("Gamble", UNION);
		devin = game.getUnit("Devin", UNION);
		heth = game.getUnit("Heth", CONFEDERATE);
	}
	
	// Initial setup tests taken as is from Version 1 tests
	@Test
	public void game_turn_is_one_on_initialized_game()
	{
		assertEquals(1, game.getTurnNumber());
	}

	@Test
	public void initial_game_status_is_in_progress()
	{
		assertEquals(IN_PROGRESS, game.getGameStatus());
	}

	@Test
	public void game_step_on_initialized_game_is_UMOVE()
	{
		assertEquals(UMOVE, game.getCurrentStep());
	}

	@Test
	public void correct_square_for_gamble_using_whereIsUnit()
	{
		assertEquals(makeCoordinate(11, 11), game.whereIsUnit("Gamble", UNION));
	}

	@Test
	public void correct_square_for_gamble_using_GetUnitsAt()
	{
		GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
		assertNotNull(unit);
		assertEquals("Gamble", unit.getLeader());
	}

	@Test
	public void devin_faces_south()
	{
		assertEquals(SOUTH, game.getUnitFacing(devin));
	}
	
	// Game step and turn tests
	@Test
	public void union_battle_follows_union_move()
	{
		game.endStep();
		assertEquals(UBATTLE, game.getCurrentStep());
	}

	@Test
	public void confederate_move_follows_union_battle()
	{
		game.endStep();
		game.endStep();
		assertEquals(CMOVE, game.getCurrentStep());
	}

	@Test
	public void confederate_battle_follows_confederate_move()
	{
		game.endStep();
		game.endStep();
		assertEquals(CBATTLE, game.endStep());
	}

	@Test
	public void turn_one_during_confederate_battle()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(1, game.getTurnNumber());
	}

	@Test
	public void go_to_turn_2()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(2, game.getTurnNumber());
	}

	@Test
	public void start_of_turn_2_is_UMOVE_step()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(UMOVE, game.getCurrentStep());
	}

	// Movement tests
	@Test
	public void gamble_moves_north()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(11, 10));
		assertEquals(makeCoordinate(11, 10), game.whereIsUnit(gamble));
		assertNull(">> Documentation says this should be null, not an empty array",
				game.getUnitsAt(makeCoordinate(11, 11)));
	}

	@Test
	public void devin_moves_south()
	{
		game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 11));
		assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
	}

	@Test
	public void heth_moves_east()
	{
		game.endStep();
		game.endStep();
		game.moveUnit(heth, makeCoordinate(8, 8), makeCoordinate(10, 8));
		assertEquals(heth, game.getUnitsAt(makeCoordinate(10, 8)).iterator().next());
	}
	
	@Test
	public void devin_moves_south_using_another_coordinate_()
	{
		game.moveUnit(devin, new TestCoordinate(13, 9), makeCoordinate(13, 11));
		assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
	}
	
	// Tests requiring the test double
	@Test
	public void union_stacked_entry_units_are_at_correct_location()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Geary", UNION));
	    assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Slocum", UNION));
	    assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Williams", UNION));

	    testGame.setGameTurn(20);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();

	    assertNull(game.whereIsUnit("Howe", UNION));
	}
	

	@Test
	public void confederate_stacked_entry_units_are_at_correct_location()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(UBATTLE);
	    assertNull(game.whereIsUnit("Dance", CONFEDERATE));
	    game.endStep();  // step -> CMOVE, turn -> 8
	    assertEquals(makeCoordinate(1, 5), game.whereIsUnit("Dance", CONFEDERATE));
	    assertEquals(makeCoordinate(1, 5), game.whereIsUnit("Nelson", CONFEDERATE));
	    assertEquals(makeCoordinate(1, 5), game.whereIsUnit("Anderson", CONFEDERATE));
	    assertNull(game.whereIsUnit("Jenkins", CONFEDERATE));
	}

	@Test
	public void stacked_entry_units_are_removed_at_the_end_of_move_turn()    // This has been EDITED
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    game.endStep();  // step -> UBATTLE
	    assertNull(game.getUnitsAt(makeCoordinate(22, 22)));
	}
	
	@Test
	public void reinforcement_cannot_be_added_when_there_is_already_a_unit_on_that_square()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 1, 5, EAST);
	    assertEquals(makeCoordinate(1, 5), game.whereIsUnit(devin));
	    testGame.setGameTurn(8);
	    testGame.setGameStep(UBATTLE);
	    assertNull(game.whereIsUnit("Dance", CONFEDERATE));
	    game.endStep();  // step -> CMOVE, turn -> 8
	    assertNull(game.whereIsUnit("Dance", CONFEDERATE));
	    assertEquals(1, game.getUnitsAt(makeCoordinate(1, 5)).size());
	}

	@Test
	public void test_control_zone_of_devin()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 13, 9, SOUTH);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(14,10), makeCoordinate(13, 10), makeCoordinate(12, 10), makeCoordinate(13, 9))), 
			((GbgUnitImpl) devin).getCurrentZoneControl(makeCoordinate(13, 9)));
	}

	@Test
	public void test_control_zone_of_heth()
	{
		testGame.clearBoard();
		testGame.putUnitAt(heth, 8, 8, SOUTH);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(9, 9), makeCoordinate(8, 9), makeCoordinate(7, 9), makeCoordinate(8, 8))), 
			((GbgUnitImpl) heth).getCurrentZoneControl(makeCoordinate(8, 8)));
	}

	@Test
	public void test_control_zone_of_north()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 13, 9, NORTH);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(12, 8), makeCoordinate(13, 8), makeCoordinate(14, 8), makeCoordinate(13, 9))), 
			((GbgUnitImpl) devin).getCurrentZoneControl(makeCoordinate(13, 9)));
	}
	
	@Test
	public void test_control_zone_of_southeast()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 13, 9, SOUTHEAST);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(14, 9), makeCoordinate(14, 10), makeCoordinate(13, 10), makeCoordinate(13, 9))), 
			((GbgUnitImpl) devin).getCurrentZoneControl(makeCoordinate(13, 9)));
	}
	
	@Test
	public void test_control_zone_of_southwest()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 13, 9, SOUTHWEST);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(13, 10), makeCoordinate(12, 10), makeCoordinate(12, 9), makeCoordinate(13, 9))), 
			((GbgUnitImpl) devin).getCurrentZoneControl(makeCoordinate(13, 9)));
	}
	
	@Test
	public void test_control_zone_of_northwest()
	{
		testGame.clearBoard();
		testGame.putUnitAt(devin, 13, 9, NORTHWEST);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(12, 9), makeCoordinate(12, 8), makeCoordinate(13, 8), makeCoordinate(13, 9))), 
			((GbgUnitImpl) devin).getCurrentZoneControl(makeCoordinate(13, 9)));
	}

	@Test
	public void test_control_zone_of_gamble()
	{
		testGame.clearBoard();
		testGame.putUnitAt(gamble, 11, 11, WEST);
		assertEquals(new ArrayList<Coordinate>(Arrays.asList(makeCoordinate(10, 12), makeCoordinate(10, 11), makeCoordinate(10, 10), makeCoordinate(11, 11))), 
			((GbgUnitImpl) gamble).getCurrentZoneControl(makeCoordinate(11, 11)));
	}
	
	@Test
	public void test_enemy_control_zone_of_heth()
	{
		GettysburgBoard board = new GettysburgBoard();
		board.initializeUnits(0, CMOVE);
		board.initializeUnits(0, UMOVE);
		assertEquals(12, board.getAllEnemiesControlledZoneFor(heth).size());
	}

	@Test
	public void test_enemy_control_zone_of_devin()
	{
		GettysburgBoard board = new GettysburgBoard();
		board.initializeUnits(0, CMOVE);
		board.initializeUnits(0, UMOVE);
		assertEquals(4, board.getAllEnemiesControlledZoneFor(devin).size());
	}
	
	@Test
	public void test_shortest_path()
	{
		GettysburgBoard board = new GettysburgBoard();
		board.initializeUnits(0, CMOVE);
		board.initializeUnits(0, UMOVE);
		assertEquals(1, board.shortestPathDistance(gamble, makeCoordinate(11, 11), makeCoordinate(11, 12)));
	}
	
	@Test
	public void it_is_valid_to_stop_on_one_square_in_zone_control_of_enemies()
	{
		GettysburgBoard board = new GettysburgBoard();
		board.initializeUnits(0, CMOVE);
		board.initializeUnits(0, UMOVE);
		assertEquals(4, board.shortestPathDistance(gamble, makeCoordinate(11, 11), makeCoordinate(9, 7)));
	}
	
	@Test
	public void gamble_move_west_to_enemy_control_zone()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(9, 9));
		assertEquals(makeCoordinate(9, 9), game.whereIsUnit(gamble));
	}
	
	@Test
	public void heth_attacks_and_defeats_devin()
	{
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
		BattleResolution bs = game.resolveBattle(battle);
		assertEquals(BattleResult.DELIM, bs.getBattleResult());
		assertEquals(devin, bs.getEliminatedUnionUnits().iterator().next());
		assertEquals(heth, bs.getActiveConfederateUnits().iterator().next());
		assertEquals(0, bs.getActiveUnionUnits().size());
		assertEquals(0, bs.getEliminatedConfederateUnits().size());
	}
	
	@Test
	public void one_confederate_fights_two_union_units()
	{
		testGame.setGameTurn(2);
		testGame.clearBoard();
		
		testGame.putUnitAt(devin, 1, 1, EAST);
		testGame.putUnitAt(gamble, 1, 2, EAST);
		testGame.putUnitAt(heth, 3, 1, WEST);
		
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(3, 1), makeCoordinate(2, 1));
		game.endStep();
		
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		
		assertEquals(BattleResult.DELIM, game.resolveBattle(battle).getBattleResult());
	}
	
//	@Test(expected = GbgInvalidActionException.class)
//	public void it_is_invalid_when_end_battle_step_without_resolving_battles()
//	{
//		testGame.setGameTurn(2);
//		testGame.clearBoard();
//		
//		testGame.putUnitAt(devin, 1, 1, EAST);
//		testGame.putUnitAt(gamble, 1, 2, EAST);
//		testGame.putUnitAt(heth, 3, 1, WEST);
//		
//		testGame.setGameStep(CMOVE);
//		game.moveUnit(heth, makeCoordinate(3, 1), makeCoordinate(2, 1));
//		game.endStep();
//		
//		game.endStep();
//	}
}

class TestCoordinate implements Coordinate
{
	private int x, y;
	
	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	public TestCoordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}