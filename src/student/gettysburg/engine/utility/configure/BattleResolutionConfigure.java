package student.gettysburg.engine.utility.configure;

import gettysburg.common.BattleResult;
import static gettysburg.common.BattleResult.*;

public class BattleResolutionConfigure 
{
	public final static BattleResult[][] battleResolution = 
		{
			{
				DELIM, DBACK, DELIM, DELIM, DELIM, DELIM
			},
			{
				DELIM, DBACK, DELIM, DBACK, DELIM, DELIM
			},
			{
				DELIM, EXCHANGE, DELIM, DBACK, DBACK, DELIM
			},
			{
				DELIM, EXCHANGE, DBACK, DBACK, EXCHANGE, DELIM
			},
			{
				DELIM, EXCHANGE, DBACK, ABACK, EXCHANGE, AELIM
			},
			{
				DELIM, ABACK, DBACK, ABACK, EXCHANGE, AELIM
			},
			{
				DELIM, EXCHANGE, DBACK, ABACK, AELIM, AELIM
			},
			{
				DBACK, EXCHANGE, ABACK, ABACK, AELIM, AELIM
			},
			{
				ABACK, ABACK, ABACK, ABACK, AELIM, AELIM
			},
			{
				ABACK, AELIM, ABACK, ABACK, AELIM, AELIM
			},
			{
				AELIM, AELIM, ABACK, ABACK, AELIM, AELIM
			},
			{
				AELIM, AELIM, AELIM, AELIM, AELIM, AELIM
			}
	};
}
