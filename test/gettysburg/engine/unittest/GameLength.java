package gettysburg.engine.unittest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gettysburg.common.GbgGame;
import gettysburg.common.GbgGameStatus;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.CoordinateImpl;

public class GameLength 
{
	private GbgGame game;
	private GbgUnit gambleUnit;
	private GbgUnit devinUnit;
	private GbgUnit hethUnit;

	@Before
	public void setUp() throws Exception 
	{
		game = GettysburgFactory.makeGame();
		gambleUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(11, 11)).iterator().next();
		devinUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(13, 9)).iterator().next();
		hethUnit = game.getUnitsAt(CoordinateImpl.makeCoordinate(8, 8)).iterator().next();

	}
	
	@Test
	public void play_the_game()
	{
		game.moveUnit(gambleUnit, CoordinateImpl.makeCoordinate(11, 11), CoordinateImpl.makeCoordinate(11, 10));
		Assert.assertEquals(GbgGameStatus.IN_PROGRESS	, game.getGameStatus());
		game.moveUnit(devinUnit, CoordinateImpl.makeCoordinate(13, 9), CoordinateImpl.makeCoordinate(14, 9));
		Assert.assertEquals(GbgGameStatus.IN_PROGRESS	, game.getGameStatus());
		game.endMoveStep();
		game.endBattleStep();
		Assert.assertEquals(GbgGameStatus.IN_PROGRESS	, game.getGameStatus());
		
		game.moveUnit(hethUnit, CoordinateImpl.makeCoordinate(8, 8), CoordinateImpl.makeCoordinate(8, 9));
		game.endMoveStep();
		game.endBattleStep();
		Assert.assertEquals(GbgGameStatus.UNION_WINS, game.getGameStatus());
	}
}
