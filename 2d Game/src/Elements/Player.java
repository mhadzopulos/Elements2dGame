package Elements;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.MouseInfo;

public class Player 
{
	private String pathName = "PlayerImages/";
    private String still = pathName + "player.png";
    private String move1 = pathName +"playerm1.png";
    private String move2 = pathName +"playerm2.png";
    private String Air = pathName +"playera.png";
    private String move11 = pathName +"playerm11.png";
    private ImageIcon ii = new ImageIcon(this.getClass().getResource(still));
    private ImageIcon im1 = new ImageIcon(this.getClass().getResource(move1));
    private ImageIcon im2 = new ImageIcon(this.getClass().getResource(move2));
    private ImageIcon ima = new ImageIcon(this.getClass().getResource(Air));
    private ImageIcon im11 = new ImageIcon(this.getClass().getResource(move11));
    private Image image;
    
    private ArrayList<SkillHandler> missiles; //Missiles are skills in the area casted by player
    private Area area;
    
    int dx, x,y,width,height, level, hp, stamina;
    int armor, armorHP;
    int newx,newy,timeCounter,dy, speed, runTime, chargeCount;
    int desx,desy, desdy, desdx, type, maxCharge = 3, charge;
    String name;
    double fallCounter, aniTime, coolDownTime, z;
    boolean visible, inAir,moving, isRight, canCast =true;
    boolean space,mouse1,mouse2,imageSet, charged;
    boolean hasSkin;
    int iconCounter1, iconCounter2, iconCounter3, iconPointer; //icon counters point to what you're using for your attacks
    boolean showIcon, parrying = false, parried = false;
    
	Timer fall = new Timer();
	ArrayList<Skill> oneSkills = new ArrayList<Skill>(); // Skills used for quick, charge, and parry attacks
	ArrayList<Skill> twoSkills = new ArrayList<Skill>();
	ArrayList<Skill> threeSkills = new ArrayList<Skill>();
	int iconList[][];
    //DX = speedx
    //DY = speedy

    public Player() throws IOException{
        image = ii.getImage();
        width = image.getWidth(null); //height and width are used for drawing
        height = image.getHeight(null);
        missiles = new ArrayList<SkillHandler>(); //list of attacks in the area that the player casts
        visible = true;
        x = 40;
        y = 300;
        newx = x;
        newy = y;
        area = null;
        inAir = true;
        fall.schedule(new animation(), 0,10);
        timeCounter = 0;	hp = 100; stamina = 100;
        armor = 0;
        aniTime = 0;
        speed = 4;
        isRight = true;
        
		FileReader getData = new FileReader("src/Elements/Database/CharacterSheet.txt");
		BufferedReader gd = new BufferedReader(getData);

		name = gd.readLine();
        type = Integer.valueOf(gd.readLine());
        
        if(type == 1) //type is earth skills
        {
        	for(int i = 0; i < 7; i++) //5 is the amount of earth skills right now
        	{
        		Skill s = new Skill(i);
        		System.out.println("skillLoaded"); //loads skills in the type of attack arrays
        		if(s.typeA != 0)
        		{
        			if(s.typeA == 1)
        				oneSkills.add(s);
        			if(s.typeA == 2)
        				twoSkills.add(s);
        			if(s.typeA == 3)
        				threeSkills.add(s);
        		}	
        	}
        	Skill s = new Skill(100);
        	threeSkills.add(s);
        	s = new Skill(101);
        	threeSkills.add(s);
        	s = new Skill(102);
        	threeSkills.add(s);
        }
        else if(type == 2) //if element is fire
        {
        	for(int i = 16; i < 17; i++) //5 is the amount of earth skills right now
        	{
        		Skill s = new Skill(i);
        		System.out.println("skillLoaded");
        		if(s.typeA != 0)
        		{
        			if(s.typeA == 1)
        				oneSkills.add(s);
        			if(s.typeA == 2)
        				twoSkills.add(s);
        			if(s.typeA == 3)
        				threeSkills.add(s);
        		}	
        	}
        	Skill s = new Skill(100);
        	threeSkills.add(s);
        	s = new Skill(101);
        	threeSkills.add(s);
        	s = new Skill(102);
        	threeSkills.add(s);
        }
    }
    
    
    public void checkAnimation() //checks players movement and uses anitime to set the image
    {
    	if(imageSet == true) 
    		return;
    	
    	if(moving == true && inAir == false)
    	{
    		if(aniTime <= 10)
    		{
    			image = im1.getImage();
    		}
    		if((aniTime > 10 && aniTime <= 20) || (aniTime >= 30 && aniTime < 40))
    		{
    			image = im11.getImage();
    		}
    		if(aniTime >= 20 && aniTime < 30)
    		{
    			image = im2.getImage();
    		}	
    	}
    	else if(inAir == true)
    	{
    		image = ima.getImage();
    	}
    	else
    		image = ii.getImage();
    }
    public void checkCool()
    {
    	if(coolDownTime > 0) //not used, but used for if skills need cooldowntime
    	{
    		coolDownTime--;
    	}
    }
    public void setImage(String n)
    {
    	ImageIcon i =new ImageIcon(this.getClass().getResource(n)); //in order to get images, use ImageIcon and i.getImage();
    	image = i.getImage();
    	width = image.getWidth(null);
        height = image.getHeight(null);
    }
    public void move(Area a) //always callled in board, used as a ticking method
    {						//NOTE: if any problems or bugs with collisions and the walls/area, check here
    	checkCool();
    	checkAnimation();
    	
    	if(moving == false && inAir == false)
    		return;
    	
    	if(missiles.size() == 0)
    		canCast = true;
    	
    	if(inAir== true)
    	{
    		timeCounter++; //if in air start a time counter, otherwise reset to 0
    	}
    	else
    		timeCounter = 0;
    	
    	if(dx < 0)
    		isRight = false; //checks the direction player is facing
    	if(dx > 0)			//dx is change in x, negative is facing left
    		isRight = true;
    	
    	
    	fallCounter = timeCounter%2; //fall counter is gravity
    	
    	
    	if((inAir == true) && ((fallCounter ==0)))
    	{
    		if(dy < 16)
    			dy = dy + 2; //simulated gravity
    		newy += dy;
    	}
    	else
    		newy += dy;
        
    	newx += dx;
        for(Wall w: a.wall) //checks collisions with all the walls in the players area
        {
        	if(this.checkCollisions(w.wall) != true) //if no collisions with a wall, you're free to move
        	{
        		x = newx;
        		y = newy;
        		inAir = true; 
        	}
        	if(this.getBottom().intersects(w.wall) == true) //if the players bottom intersects with a wall
        	{
        		inAir = false; //no more falling
        		if(dx != 0)
        			x = newx; 
 
        		if(dy < 0) //if moving up on the screen
        		{
        			y = newy;
        			inAir = true;
        		}
        		if(dy > 0) //moving down on the screen
        		{
        			newy = w.wall.y - height;
        			dy = 0;
        		}
        		
        	}	
        	if(this.getTop().intersects(w.wall) == true) //if players head hits the ceiling
        	{
        		if(dx != 0)
        			x = newx; //allowed to move left or right
        		if(dy < 0)
        		{
        			newy = w.wall.y+ w.wall.height; //if you're moving up still, set dy to 0 so that can't happen
        			dy = 0;
        		}
        	}
        	if(this.getLeft().intersects(w.wall) == true) //if the players left intersects with a wall
        	{
        		if(dx > 0)
        			x = newx; //allowed to move right
        		if(dy != 0)
        			y = newy; //allowed to jump or move down
        		if(dx < 0)
        		{
        			newx = w.wall.x + w.wall.width; // collision with wall on the left, no movement allowed
        			dx = 0;
        			moving = false;
        		}
        		
        	}
        	if(this.getRight().intersects(w.wall) == true)//collision with wall on right
        	{
        		if(dx < 0)
        			x = newx; //if moving left, movement allowed
        		if(dy != 0)
        			y = newy; //jumping allowed
        		if(dx > 0)
        		{
        			newx = w.wall.x - width; //movement not allowed on the right
        			dx = 0;
        			moving = false;
        		}
        	}
        }
        for(SkillHandler sh: missiles) //checks to see if the player collides with any of his own skills that are solid
	        {
        		if (sh.isSolid)
		        {
		        	if(this.checkCollisions(sh.getBounds()) != true)
		        	{
		        		x = newx;
		        		y = newy;
		        		inAir = true;
		        	}
		        	if(this.getBottom().intersects(sh.getBounds()) == true)
		        	{
		        		inAir = false;
		        		if(dx != 0)
		        			x = newx;
		 
		        		if(dy < 0)
		        		{
		        			y = newy;
		        			inAir = true;
		        		}
		        		if(dy > 0)
		        		{
		        			newy = sh.y - height;
		        			dy = 0;
		        		}
		        		
		        	}	
		        	if(this.getTop().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx != 0)
		        			x = newx;
		        		if(dy < 0)
		        		{
		        			newy = sh.y+ sh.height;
		        			dy = 0;
		        		}
		        	}
		        	if(this.getLeft().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx > 0)
		        			x = newx;
		        		if(dy != 0)
		        			y = newy;
		        		if(dx < 0)
		        		{
		        			newx = sh.x + sh.width;
		        			dx = 0;
		        			moving = false;
		        		}
		        		
		        	}
		        	if(this.getRight().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx < 0)
		        			x = newx;
		        		if(dy != 0)
		        			y = newy;
		        		if(dx > 0)
		        		{
		        			newx = sh.x - width;
		        			dx = 0;
		        			moving = false;
		        		}
		        	}
		        }
        }
        if (x < 1) {
            newx = 1;
        }
        if (y < 1) {
            newy = 1;
            dy = 0;
        }
    }
    public boolean checkCollisions(Rectangle r)
    {
    	if(this.getBounds().intersects(r))
    	{
    		return true;
    	}
    	else
    		return false;
    }
    
    //whole bunch of getters/setters
    
    public Area getArea()
    {
    	return area;
    }
    public boolean getAir()
    {
    	return inAir;
    }
    public void setAir(boolean a)
    {
    	inAir = a;
    }
    public void setArea(Area a)
    {
    	area = a;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x)
    {
    	this.x = x;
    }
    public void setY(int y)
    {
    	this.y = y;
    }
    public Image getImage() {
        return image;
    }
    public ArrayList<SkillHandler> getMissiles() {
        return missiles;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean isVisible() {
        return visible;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public Rectangle getBottom()
    {
    	return new Rectangle((newx + width/2), y + height, 1, 1);
    }
    public Rectangle getTop()
    {
    	return new Rectangle(newx + width/2,newy,1,1);
    }
    public Rectangle getLeft()
    {
    	return new Rectangle(newx, newy + height/2, 1, 1);
    }
    public Rectangle getRight()
    {
    	return new Rectangle(newx + width, newy + height/2, 1, 1);
    }
   
    //inputs from the player
    
    public void mouseClicked(MouseEvent e)throws IOException
    {
    	int click = e.getButton();
    	if(stamina > 0)
    	{
	    	if(click == MouseEvent.BUTTON1) //mouse 1 - quick attacks, checks your type
	    	{
	    		if(charged == true && canCast == true && type == 1)//checks to see if charged if you're earth, need to charge
	    			//to attack
	        	{
	    			Point p = e.getPoint();
	    			
	    			if(p.x < x && isRight == true) //turns the player to face where they are throwing a skill
	    				isRight = false;
	    			if(p.x > x && isRight == false)
	    				isRight = true;
	    			
	        		boolean found = false; //creates temp variable to find chunks
	        		
	    			Skill s = oneSkills.get(iconCounter1); //takes the skill that your iconcounter is pointing at

	        		if (s.id == 3 && inAir == true)
	        			return;
	        		
	        		for(int i = 0; i < missiles.size(); i++) //checks the players casted skills to find any chunk skills
	        		{
	        			SkillHandler sh = (SkillHandler)missiles.get(i);
	        			if(sh.s.id == 0 && sh.aniTime > 40)
	        			{
	        				
	                		s.desx = p.x; //sets the destination for the x and y of the skill 
	                    	s.desy = p.y;
	                    	desx = p.x;
	                    	desy = p.y;
	                    	s.x = sh.x;
	                    	s.y = sh.y;
	                    	useSkill(s, sh.image); //uses the useSkill method in this class(Player.java)
	        				found = true;
	        				sh.isVisible = false; //sets the skill to be invisible (will end up removing it)
	        				missiles.remove(i);
	        				charge--;
	        				if(charge == 0) //
	        					charged = false;
	        				//stamina -= s.castCost;
	        			}
	        			if(found == true) //if you find a chunk, break the search
	        				break;
	        		}
	        		
	        	}
	    		if(type == 2 && canCast == true) //checks if player is a fire player and can cast
	    		{

	    			Point p = e.getPoint(); //gets the point from the click
	    			if(p.x < x && isRight == true) //sets the direction of the character based on the click
	    				isRight = false;
	    			if(p.x > x && isRight == false)
	    				isRight = true;
	    			Skill s = oneSkills.get(iconCounter1); //sets the skill to what the iconcounter is pointing to
	    			
                		s.desx = p.x; //sets the destination (same as above)
                    	s.desy = p.y;
                    	desx = p.x;
                    	desy = p.y;
                    	useSkill(s);
        				//stamina -= s.castCost;
	        			
	        			
	    		}
	    	}
	    	if(click == MouseEvent.BUTTON3) //uses skills from the charged attack command
	    	{
	    		Point p = e.getPoint(); //see above
	    		
				if(p.x < x && isRight == true)
					isRight = false;
				if(p.x > x && isRight == false)
					isRight = true;
				
	    		if(canCast == true)
	        	{
	    			Skill s = twoSkills.get(iconCounter2); //sets skill to the charged iconCounter pointer
	        		if(s.id == 5 && hasSkin == true)
	        			return;
	    			if (inAir == true)
	        			return;
	        		s.desx = p.x;
	            	s.desy = p.y;
	            	desx = p.x;
	            	desy = p.y;
	            	mouse2 = true; //mouse2 was intended to be used to charge attacks up (like holding down a key)
	            	useSkill(s);
	            	//coolDownTime = s.castTime;
	        	}
	    	}
    	}
    }
    public void mouseReleased(MouseEvent e)
    {
    	int click = e.getButton();
    	
    	if(click == MouseEvent.BUTTON1) //sets the charge of either mouses to false
    		mouse1 = false;
    	if(click == MouseEvent.BUTTON3)
    		mouse2 = false;
    }
    public void keyPressed(KeyEvent e) throws IOException
    {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_1) //for 1,2,3 buttons - changing where the iconCounter is pointing to in the skills arrays
        {
        	if(showIcon == true)
        		iconCounter1++;
        	if(iconCounter1 > oneSkills.size()-1)
        		iconCounter1 = 0;
        	iconPointer = 1; //points to what type of attack is to be displayed by the GUI
        	showIcon = true;
        }
        if(key == KeyEvent.VK_2) //need to draw in the icon for choosing skills
        {
        	if(showIcon == true)
        		iconCounter2++;
        	if(iconCounter2 > twoSkills.size()-1)
        		iconCounter2 = 0;
        	iconPointer = 2;
        	showIcon = true;
        }
        if(key == KeyEvent.VK_3) //need to draw in the icon for choosing skills
        {
        	if(showIcon == true)
        		iconCounter3++;
        	if(iconCounter3 > threeSkills.size()-1)
        		iconCounter3 = 0;
        	iconPointer = 3;
        	showIcon = true;
        }
        
        if (key == KeyEvent.VK_SPACE) 
        {		
        }
        if(key == KeyEvent.VK_F) //f is used for parrying
        {
        	Skill s = threeSkills.get(iconCounter3); //takes where you're iconcounter3 is pointing to in the three skills
        	if(s.id > 99)
        		parrying = true;
        	
        }
        if(key == KeyEvent.VK_E) //transportation for earth platform(for now)
        {
        	if(inAir == false && type == 1)
        	{
	        	boolean found = false;
	        	for(SkillHandler h: missiles)
	        	{
	        		if(h.s.id ==4)
	        			found = true;
	        	}
	        	if(found == false)
	        	{
	        		Skill s = new Skill(4);
	        		useSkill(s);
	        	}
        	}
        	
        }
        if(imageSet == false) //imageSet is used for checking if the player can move
        {
	        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
	        {
	        	if(runTime < 40) //runtime is used for running momentum
	        		dx = -speed;
	        	else			//after 40 whatevers, the players speed increases by 2
	        		dx = -speed*2;
	            moving = true;
	        }
	
	        if (key == KeyEvent.VK_RIGHT|| key == KeyEvent.VK_D) 
	        {
	        	
	        	if(runTime < 40)
	        		dx = speed;
	        	else
	        		dx = speed*2;
	            moving = true;
	        }
	
	        if (key == KeyEvent.VK_UP|| key == KeyEvent.VK_W) 
	        {
	        	if(inAir != true)
	        		jump(); //uses the jump method
	        }
        }
    }
    public void keyReleased(KeyEvent e) throws IOException{
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_1)
        {	
        }
        if(key == KeyEvent.VK_2)
        {
        }
        if(key == KeyEvent.VK_F)
        {
        	parrying = false;
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {

        		dx = 0; //sets moving to stop
            	moving = false;
        	
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
	            dx = 0;
	            moving = false;

        }
        if(key== KeyEvent.VK_SPACE) //used to charge up earth attacks
        	
        	if(charge < maxCharge && inAir == false && type == 1)
        	{
        		charged = true;
        		Skill s = new Skill(0); //creates a chunk skill
        		useSkill(s);
        		charge++;
        		
        	}

        
    }
  
    public void parry(EnemySkillHandler esh, int xe, int ye) //the parry method
    {
    	Skill s = threeSkills.get(iconCounter3); //checks what the iconCounter is pointing to, and then sets the skill ID
    	parried = true;
    	if(s.id == 102)
    	{
    		s = esh.s;
    		s.id = 102;
    		s.desx = xe;
    		s.desy = ye+10;
    		SkillHandler k = new SkillHandler(this, s, esh.image); //makes a skillhandler
    		
    		missiles.add(k);
        	desdy = (int)k.dy;
        	desdx = (int)k.dx;
        	z = k.z;
        	
    	}
    	if(s.id == 100)
    	{
    		if(s.isBreakable)
    		{
    			
    		}
    	}
    	if(s.id == 101)
    	{
    		
    	}
    }
    public void useSkill(Skill s) //makes a skillhandler out of a skill
    {
    	
	    	SkillHandler k = new SkillHandler(this, s, area);
	    	desdy = (int)k.dy; //sets the destination of x,y,z for the skillhandler
	    	desdx = (int)k.dx;
	    	z = k.z;
	    	missiles.add(k); //adds to your missiles
    	
    }
    public void useSkill(Skill s, Image i) //used for chunks specifically - with a needed image
    {
    	SkillHandler k = new SkillHandler(this, s, i);
    	desdy = (int)k.dy;
    	desdx = (int)k.dx;
    	z = k.z;
    	missiles.add(k);
    }
    public void jump()
    {
    	dy = -16;
		inAir = true;
    }

    private class animation extends TimerTask //this is a how a timer is created in java, used for moving
    {
    	public void run()
    	{
    		if(moving == true)
    		{
    			aniTime++; //animation time is incremented
    			if(inAir == false)
    				runTime++;
    		}
    		else
    		{
    			aniTime = 0;
    			runTime = 0;
    		}
    		if(aniTime > 40) //after 40 seconds animation time is reset
    			aniTime = 0;
    	}
    }
    
    public BufferedImage makeColorTransparent(Color color) 
    {    
        BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dimg.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(image,0,0,null);
        for(int i = 0; i < dimg.getHeight(); i++) {  
            for(int j = 0; j < dimg.getWidth(); j++) {  
                if(dimg.getRGB(j, i) == color.getRGB()) {  
                dimg.setRGB(j, i, AlphaComposite.CLEAR);  
                }  
            }  
        }  
        return dimg;  
    }
    public BufferedImage horizontalflip(BufferedImage img) //used for making face left/right
    {  
    	int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);  
        g.dispose();  
        return dimg;  
    }
    
    
    public void drawIcon(Graphics2D d)
    {
    	for(int i = 0; i < 4; i++)
    	{
    		if(i == 1)
    		{
    			for(int u = 0; u < oneSkills.size(); u++)
    			{
    				int x1 = 1;
			        int y1 = 0;
    				Skill s = oneSkills.get(u);
    				if(iconPointer == 1 && iconCounter1 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        x1 = x1 + 0;
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    			}
    		}
    		if(i == 2)
    		{
    			int x1 = 68+2;
		        int y1 = 0;
    			for(int u = 0; u < twoSkills.size(); u++)
    			{
    				Skill s = twoSkills.get(u);
    				if(iconPointer == 2 && iconCounter2 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    					d.drawImage(Iconimage, x1,y1,null);
    				}
    			}
    		}
    		if(i == 3)
    		{
    			int x1 = 68*2+2;
		        int y1 = 0;
    			for(int u = 0; u < threeSkills.size(); u++)
    			{
    				Skill s = threeSkills.get(u);
    				if(iconPointer == 3 && iconCounter3 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    					d.drawImage(Iconimage, x1,y1,null);
    				}
    			}
    		}
    	}
    }
    
    public void drawHUD(Graphics2D d)
    {
    	double perc = ((double)hp/100.0)*150;
    	int c = (int)perc;
    	{
    		d.setPaint(Color.red);
        	if(hp > 0)
        		d.fillRect(50, 525, c, 10);
    		d.drawRect(50, 525, 150, 10);
    		if(showIcon == true)
            	drawIcon(d);
    		
    		d.setColor(Color.WHITE);
    		Font small = new Font("Ariel", Font.BOLD, 14);
    		d.setFont(small);
    		d.drawString("HP: " + hp, 205, 535);
    	}
    	
    	{
    		perc = ((double)stamina/100.0)*150;
        	c = (int)perc;
    		d.setPaint(Color.blue);
        	if(stamina > 0)
        		d.fillRect(50, 550, c, 10);
    		d.drawRect(50, 550, 150, 10);
    		if(showIcon == true)
            	drawIcon(d);
    		
    		d.setColor(Color.WHITE);
    		Font small = new Font("Ariel", Font.BOLD, 14);
    		d.setFont(small);
    		d.drawString("ST: " + stamina, 205, 565);
    	}
    	
    }
    
}