package net.cmr.gaze.leveling;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

public class Skills {

	public enum Skill {
		
		CRAFTING(1),
		FORAGING(2),
		MINING(3),
		FISHING(4),
		COMBAT(5);
		
		int id;
		
		private Skill(int id) {
			this.id = id;
		}
		
		public int getID() {
			return id;
		}
		
		public static Skill getSkill(int id) {
			for(Skill skill : Skill.values()) {
				if(skill.getID()==id) {
					return skill;
				}
			}
			return null;
		}
	}
	
	private HashMap<Skill, Double> skillXP;
	
	
	public Skills() {
		skillXP = new HashMap<>(Skill.values().length);
		for(Skill skill : Skill.values()) {
			skillXP.put(skill, 0d);
		}
	}
	
	/**
	 * 
	 * Returns the floor of the inverse function for leveling
	 * 
	 * xp = 1.5L^2.2+1
	 * L = 0.831684(-1+x)^(5/11)
	 * 
	 * @return the level of the specified skill. Starting level is *1*
	 */
	public int getLevel(Skill skill) {
		//int v = (int) Math.max(1,Math.floor(Math.max(1, 0.831684*Math.pow((-1d+getXP(skill)),(5d/11d)))));
		int v = (int) Math.max(1d, Math.floor(Math.pow(getXP(skill)-1, 0.2857142857142857d)));
		if(v<=1) {
			return 1;
		}
		return v;
	}
	public double getXPFromLevel(Skill skill, int level) {
		//return 1.5d*Math.pow(level, 2.2d)+1d;
		return Math.pow(level, 3.5)+1;
	}
	
	public double getXP(Skill skill) {
		return skillXP.get(skill);
	}
	public float getProgress(Skill skill) {
		float xp = (float) getXP(skill);
		int currentLevel = getLevel(skill);
		
		float total = (float) getXPFromLevel(skill, currentLevel+1);
		
		if(currentLevel<=1) {
			return (float) (xp/total);
		}
		
		float last = (float) getXPFromLevel(skill, currentLevel);
		
		return (xp-last)/(total-last);
	}
	
	/**
	 * @param sill
	 * @param amount
	 * @return whether or not there was a level up
	 */
	public boolean addXP(Skill sill, double amount) {
		int beforeLevel = Math.max(getLevel(sill),1);
		skillXP.put(sill, getXP(sill)+amount);
		int afterLevel = Math.max(getLevel(sill),1);

		if(beforeLevel!=afterLevel) {
			//System.out.println("LEVELD UP: "+afterLevel);
		}
		
		return beforeLevel!=afterLevel;
	}
	
	public static Skills readSkills(DataInputStream input) throws IOException {
		Skills skills = new Skills();
		int skillTypeAmount = input.readInt(); // will be useful in the future when more skills are added
		for(int i = 0; i < skillTypeAmount; i++) {
			int id = input.readInt();
			double xp = input.readDouble();
			Skill skill = Skill.getSkill(id);
			skills.skillXP.put(skill, xp);
		}
		
		return skills;
	}
	public void writeSkills(DataBuffer buffer) throws IOException {
		buffer.writeInt(skillXP.size());
		for(Skill skill : Skill.values()) {
			buffer.writeInt(skill.getID());
			buffer.writeDouble(getXP(skill));
		}
	}
	
}
