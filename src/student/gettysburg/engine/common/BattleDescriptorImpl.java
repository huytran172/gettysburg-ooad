/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.Collection;

import gettysburg.common.*;

/**
 * Implementation of the BattleDescriptor interface. There is als
 * a factory method that creates a battle unit. The constructor and
 * is up to the implementor and additional methods may be added as
 * necessary for the student's design needs.
 * 
 * @version Jul 27, 2017
 */
public class BattleDescriptorImpl implements BattleDescriptor
{
	private final Collection<GbgUnit> attackers;
	private final Collection<GbgUnit> defenders;
	
	public BattleDescriptorImpl()
	{
		attackers = new ArrayList<GbgUnit>();
		defenders = new ArrayList<GbgUnit>();
	}
	
	public void addAttacker(GbgUnit unit) 
	{
		attackers.add(unit);
	}
	
	public void addDefender(GbgUnit unit)
	{
		defenders.add(unit);
	}
	
	/*
	 * @see gettysburg.common.BattleDescriptor#getAttackers()
	 */
	@Override
	public Collection<GbgUnit> getAttackers()
	{
		return attackers;
	}

	/*
	 * @see gettysburg.common.BattleDescriptor#getDefenders()
	 */
	@Override
	public Collection<GbgUnit> getDefenders()
	{
		return defenders;
	}
	
	public ArmyID getArmyTypeAttackers() {
		return this.attackers != null ? this.attackers.iterator().next().getArmy() : null;
	}

	public ArmyID getArmyTypeDefenders() {
		return this.defenders != null ? this.defenders.iterator().next().getArmy() : null;
	}
}
