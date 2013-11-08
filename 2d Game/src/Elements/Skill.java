package Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Image;
import java.util.Random;


public class Skill 
{

	String name;
	int hp;
	int level,castCost,cooldown, type, id,gibs;
	int minDamage, maxDamage, desx,desy, speed;
	int breakTime,x,y, hitbox, typeA;
	boolean isVisible, isBreakable, isSolid;
	Image image;
	
	
	public Skill(int ID) throws IOException
	{

		FileReader getMove = new FileReader("src/Elements/Database/Skill.db.txt");
		BufferedReader gm = new BufferedReader(getMove);
		
		id = ID;
		Integer a = id;
		String k = gm.readLine();
		
		while(k.equals("#" + a.toString()) != true)
		{
			k = gm.readLine();
		}
		name = gm.readLine();
		typeA = Integer.valueOf(gm.readLine()); //charged, quick, parry
		hitbox = Integer.valueOf(gm.readLine());//0:none, 1:top,2:left, 3:right, 4:bottom, 5:all
		type = Integer.valueOf(gm.readLine()); //elemental type, 1:Earth, 2:Fire, 
		castCost = Integer.valueOf(gm.readLine()); //stamina use
		cooldown = Integer.valueOf(gm.readLine());
		minDamage = Integer.valueOf(gm.readLine());
		maxDamage = Integer.valueOf(gm.readLine());
		speed = Integer.valueOf(gm.readLine());
		hp = Integer.valueOf(gm.readLine());
		if(type == 1)
		{
			gibs = Integer.valueOf(gm.readLine()); //amount of blah
			isBreakable = Boolean.valueOf(gm.readLine()); //for ice or earth
			isSolid = Boolean.valueOf(gm.readLine()); //for walls or blocking, or platforms
		}	
		isVisible = true;
		gm.close();
		getMove.close();
		
	}
	public int hit()
	{
		Random rand = new Random();
		int dmg = rand.nextInt(maxDamage-minDamage) + minDamage;
		return dmg;
	}
}
