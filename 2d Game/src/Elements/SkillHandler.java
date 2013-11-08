package Elements;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class SkillHandler 
{
	private Player player;
	private Timer aniTimer = new Timer();
	String name;
	int aniTime = 0,width, height, speed ;
	int x,y,k, f = 0, baseY,gibs, gibTime, id;
	double z,m,dx,dy, theta = 0;
	Rectangle hitbox;
	Area A;
	Image image;
	Skill s;
	
	boolean isVisible, isBreakable, right, inAir = true;
	boolean hitRight, hitLeft, hitUp, hitDown;
	boolean breakDown, isSolid; //breakdown used for breakables, solid used for running into shit/walls
	
	ImageIcon i =new ImageIcon(this.getClass().getResource("SkillImages/blank.png"));
	
	public SkillHandler(Player p, Skill s, Area A)
	{
		this.A = A; //sets area
		this.s = s; //sets skill
		id = s.id;
		name = s.name;
		image = i.getImage();
		player = p; //player belongs too
		if(id > 0)
			setImage("SkillImages/" + s.name + ".png");
		
		aniTimer.schedule(new animate(), 0,10); //makes a new timer
		isVisible = s.isVisible;
		width = image.getWidth(null); //width height used for drawing
        height = image.getHeight(null);
        isSolid = s.isSolid; //solid means a person cannot run through it
        switch(s.hitbox) //sets the hitbox from the skill
        {
	        case 0: hitbox = null;
	        	break;
	        case 1: hitbox = getTop();
	        	break;
	        case 2: hitbox = getBottom();
	       		break;
	        case 3: hitbox = getLeft();
	        	break;
	        case 4: hitbox = getRight();
	        	break;
	        case 5: hitbox = getBounds();
	        	break;
        	
        }
        right = player.isRight; //the direction of the object faces the direction of the player
        speed = s.speed;
        gibs = s.gibs; //gibs are used for when the skill breaks down
        if(s.id == 1)
        	isBreakable =false;
        else
        	isBreakable = s.isBreakable;
        
        setIDInfo(s.id);
		
		baseY = y; //whole bunch of math used to find path taken by thrown objects
		dx = s.desx - x;
		dy = s.desy - y;
		if(s.type == 2)
			theta = Math.toDegrees(Math.atan(dy/dx));
		z = Math.sqrt((dx*dx)+(dy*dy));
		k = (int)z/s.speed;
		dy =((dy/k));
		dx =((dx/k));
		
		
		System.out.println(theta);
		
	}
	public SkillHandler(Player p, Skill s, Image i)
	{
		this.s = s;
		player = p;
		image = i;
		id = s.id;
		aniTimer.schedule(new animate(), 0,10);
		isVisible = s.isVisible;
		width = image.getWidth(null);
        height = image.getHeight(null);
        right = player.isRight;
        speed = s.speed;
        gibs = s.gibs;
        isBreakable = s.isBreakable;
        
		if(s.id == 3)
        {
        	x = s.x;
        	y = s.y;
        }
		if(s.id > 99)
		{
			x = p.x + 5;
			y = p.y ;
		}
		baseY = y;
		dx = s.desx - x;
		dy = s.desy - y;
		z = Math.sqrt((dx*dx)+(dy*dy));
		k = (int)z/s.speed;
		dy =((dy/k));
		dx =((dx/k));
		
		switch(s.hitbox)
        {
	        case 0: hitbox = null;
	        	break;
	        case 1: hitbox = getTop();
	        	break;
	        case 2: hitbox = getBottom();
	       		break;
	        case 3: hitbox = getLeft();
	        	break;
	        case 4: hitbox = getRight();
	        	break;
	        case 5: hitbox = getBounds();
	        	break;
        }
	}
	public void setImage(String n)
    {
    	ImageIcon i =new ImageIcon(this.getClass().getResource(n));
    	image = i.getImage();
    	width = image.getWidth(null);
        height = image.getHeight(null);
    }
	public Rectangle getBounds()
	{
		return new Rectangle(x,y,width,height);
	}
	public void Handler()
	{
		if(id == 0)//summon rock
		{
			if(aniTime < 15) //uses animation time to decide what to do 
			{
				player.setImage("PlayerImages/playerE1.png"); //this sets the player to the E1 png
				player.imageSet = true; //makes sure the player can't move
				player.canCast = false; //makes sure the player can't cast
			}
			if(aniTime < 30 && aniTime > 15)
			{
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
			}
			if(aniTime > 30)
			{
				player.canCast = true;	//player can cast another skill
				player.imageSet = false; //player image can change
				if(y > s.desy) //if the y-destination hasn't been reached, keep moving up
					y -= 4; //move up 4 pixles
				if(right)
	        		x = player.getX() + player.width;//sets the x position to where you're facing and following
	        	else
	        		x = player.getX() - width; //facing left
				if(player.inAir == true)
					y = y + player.dy + 1; //this makes the chunk follow the player
				
			}
		}
		else if(s.id == 1 )// rockblast
		{
			if(aniTime < 20)
			{
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
				
			}
			if(aniTime < 90 && aniTime >= 20)
			{
				y -= 1;
				player.setImage("PlayerImages/playerE2.png");	
			}
			if(aniTime >=90  && aniTime < 100)
			{	
				isBreakable = true; //allows the rock to be broken **NOT IMPLEMENTED YET**
				player.setImage("PlayerImages/playerEp.png");
				if(dx > 0){
					player.newx +=5; //moves the player when you punch the rock
					player.x += 5; //hafta move both x and newx for the player otherwise shit goes wrong
				}
				else{
					player.newx +=5;
					player.x -= 5;
				}
				if(f <= Math.abs(z)) //this makes the rock move proportionally to the destination wanted
				{
					y = (int)(y+dy);
					x = (int)(x+dx);
					f+=speed;
				}
					hitbox = getBounds(); //updates hitbox
			}
			if(aniTime >= 100 && aniTime < 110 || s.isVisible == false)
			{
				player.imageSet = false;
				player.canCast = true;
			}
			if(aniTime > 100)
			{
				if(f <= Math.abs(z))
				{
					y = (int)(y+dy);
					x = (int)(x+dx);
					f+=speed;
				}
				else //this makes the rock start to fall
				{
					x = (int)(x+dx);
					dy += 1;
					y = (int)(y+dy);
				}
				hitbox = getBounds();
			}
			
		}
		else if(s.id == 2) //spire
		{
			if(aniTime < 26)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			if(aniTime < 150 && aniTime > 25)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime > 150 && aniTime < 400)
			{
				if(height < image.getHeight(null) - 9)
				{
					height +=10;
					y -= 10;
				}
				else
				{
					height = image.getHeight(null);
					y = s.desy;
					player.canCast = true;
				}
				hitbox = getTop();
				player.imageSet = false;	
			}
			else if( aniTime > 400)
			{
				breakDown = true;
			}
		}
		else if(id == 4) //rock transport
		{
			if(aniTime < 26)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			if(aniTime < 100 && aniTime > 25)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
			}
			else if(aniTime > 100 && aniTime < 200)
			{
				if(height < image.getHeight(null) - 9)
				{
					height +=10;
					y -= 10;
				}
				else
				{
					height = image.getHeight(null);
					y = s.desy;
					player.canCast = true;
				}
				
				player.imageSet = false;	
			}
			else if(aniTime > 200 && getBounds().intersects(player.getBottom()))
			{
				x += player.dx;
			}
		}
		
		else if(id == 3) //chunk
		{
			if(aniTime < 10) //sets images for the first 10 secs
			{
				player.inAir = false;
				player.moving = false;
				player.setImage("PlayerImages/playerEp.png");
				player.imageSet = true;
				player.canCast = false;
				
				
			}
			if(aniTime > 10 && aniTime < 20) //allows the player to move
			{
				player.imageSet = false;
				player.canCast = true;
			}
			if(f <= Math.abs(z))
			{
				
				y = (int)(y+dy);
				x = (int)(x+dx);
				
				f+=speed;
				hitbox = getBounds();
			}
			else
			{
				x = (int)(x+dx);
				dy += 1;
				y = (int)(y+dy);
				hitbox = getBounds();
			}
		}
		else if(s.id == 5) //rockskin
		{
			player.hasSkin = true;
			player.armor = 30;
			if(aniTime < 50)
			{
				player.y += 6;
				player.imageSet = true;
				player.canCast = false;
			}
			
			else if(aniTime > 50 && aniTime < 100)
			{
				player.y -= 6;
				height = image.getHeight(null);
				width = image.getWidth(null);
				y = player.getY();
	        	x = player.getX();
	        	player.canCast = false;
			}
			
			else if(aniTime >= 100)
			{
				if(player.isRight == false)
					right = false;
				else
					right = true;
				y = player.y;
				x = player.x;
				player.imageSet = false;
			}
			if(player.armorHP > s.hp)
			{
				isVisible = false;
				player.armor -= 30;
				player.hasSkin = false;
				player.armorHP = 0;
				
			}
			if(aniTime > 100 && aniTime < 115)
				player.canCast = true;
			
        	
		}
		else if(id == 6) //ROCKDUMMY
		{

			if(aniTime < 25)
			{
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime < 35 && aniTime > 24)
			{
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime > 35 && aniTime < 75)
			{
				player.imageSet = false;
				player.canCast = true;
				if(y > s.desy)
				{
					y -= 10;
					height += 10;
				}
				else
					y = s.desy;
			}
			
		}
		else if(id == 7) //RollingStone
		{
			setRollingImage();
		}
		else if(id == 16)// fireball
		{
			setFireImage();
			if(aniTime < 50)
			{

				player.moving = false;
				player.setImage("PlayerImages/playerEp.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime > 50 && aniTime < 60)
			{
				player.imageSet = false;
				player.canCast = true;
			}
			y = (int)(y+dy);
			x = (int)(x+dx);
			f+=speed;
			hitbox = getBounds();
		}
		
		
		else if(s.id == 102)
		{
			if(f <= Math.abs(z))
			{
				y = (int)(y+dy);
				x = (int)(x+dx);
				f+=speed;
				hitbox = getBounds();
			}
			else
			{
				x = (int)(x+dx);
				dy += 1;
				y = (int)(y+dy);
				hitbox = getBounds();
			}
		}
		
		
	}
	public void CheckCollisions(Rectangle r)
	{
		if(getRight().intersects(r))
			hitRight = true;
		if(getLeft().intersects(r))
			hitLeft = true;
		if(getTop().intersects(r))
			hitUp = true;
		if(getBottom().intersects(r))
			hitDown = true;
	}
	private class animate extends TimerTask
	{
		public void run()
		{
			aniTime++;
		if(inAir == false)
			gibTime++;
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
    public BufferedImage horizontalflip(BufferedImage img) {  
        
    	int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);  
        g.dispose();  
        return dimg;  
    }
    public BufferedImage rotate(BufferedImage img, int angle) 
    {  
        int w = img.getWidth();  
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w+10, h+10, img.getType());  
        Graphics2D g = dimg.createGraphics(); 
        if(theta < -35)
        {
        	dimg = new BufferedImage(w+15, h+30, img.getType()); 
        	g = dimg.createGraphics(); 
        	g.rotate(Math.toRadians(angle), 20, 35);
	        g.drawImage(img, null, 20,20);  //set up if for theta to draw image to fix fire drawing problem
        }
        if(theta < -15 && theta > -35)
        {
	        g.rotate(Math.toRadians(angle), 10, 10);
	        g.drawImage(img, null, 10, 15);  //set up if for theta to draw image to fix fire drawing problem
	    }
        if(theta > -15)
        {
        	g.rotate(Math.toRadians(angle), 0, 0);
	        g.drawImage(img, null, 0, 5);  //set up if for theta to draw image to fix fire drawing problem
        }
  //set up if for theta to draw image to fix fire drawing problem
        return dimg;  
    }  
    
    public Gib rockDestroy(int a)
    {
    		Random rand = new Random();
    		Integer rockID = rand.nextInt(a) + 1;
    		int x1, h;
    		
	    	h = rand.nextInt(height) + y;
	    	x1 = rand.nextInt(width) + x;

    		if(hitRight)
    		{
    			x1 = rand.nextInt(width) + x - 10;
    		}
    		ImageIcon ii = new ImageIcon(this.getClass().getResource("SkillImages/RockB" + rockID + ".png"));
    		return new Gib(ii.getImage(), x1, h) ;
    }
    public Rectangle getBottom()
    {
    	return new Rectangle((x + width/2), y + height, 1, 1);
    }
    public Rectangle getTop()
    {
    	return new Rectangle(x + width/2,y,1,1);
    }
    public Rectangle getLeft()
    {
    	return new Rectangle(x,y + height/2, 1, 1);
    }
    public Rectangle getRight()
    {
    	return new Rectangle(x + width, y + height/2, 1, 1);
    }
    public Rectangle getHitBox()
    {
    	return hitbox;
    }
    public void setFireImage()
    {
    	int time = aniTime%30;
    	if(aniTime < 36)
    	{
	    	if(aniTime < 5)
	    		setImage("SkillImages/" + name + "/" + name + "1.png");
	    	else if(aniTime < 10)
	    		setImage("SkillImages/" + name + "/" + name + "2.png");
	    	else if(aniTime < 15)
	    		setImage("SkillImages/" + name + "/" + name + "3.png");
	    	else if(aniTime < 20)
	    		setImage("SkillImages/" + name + "/" + name + "4.png");
	    	else if(aniTime < 25)
	    		setImage("SkillImages/" + name + "/" + name + "5.png");
	    	else if(aniTime < 30)
	    		setImage("SkillImages/" + name + "/" + name + "6.png");
	    }
	    	else 
	    	{
	    		if(time < 5)
	    			setImage("SkillImages/" + name + "/" + name + "7.png");
	    		else if(time < 10)
	    			setImage("SkillImages/" + name + "/" + name + "8.png");
	    		else if(time < 15)
	    			setImage("SkillImages/" + name + "/" + name + "9.png");
	    		else if(time < 20)
	    			setImage("SkillImages/" + name + "/" + name + "10.png");
	    		else if(time < 25)
	    			setImage("SkillImages/" + name + "/" + name + "11.png");
	    		else if(time < 30)
	    			setImage("SkillImages/" + name + "/" + name + "12.png");
	    	}
    		
    }
    public void setRollingImage() //what is this for?
    {
    	int time = aniTime%30;
    	if(aniTime < 26)
    	{
    		if(aniTime < 5)
        		setImage("SkillImages/" + name + "/" + name + "1.png");
        	else if(aniTime < 10)
        		setImage("SkillImages/" + name + "/" + name + "2.png");
        	else if(aniTime < 15)
        		setImage("SkillImages/" + name + "/" + name + "3.png");
        	else if(aniTime < 20)
        		setImage("SkillImages/" + name + "/" + name + "4.png");
        	else if(aniTime < 25)
        		setImage("SkillImages/" + name + "/" + name + "5.png");
    	}
    	else if (aniTime > 25)
    		setImage("SkillImages/" + name + "/" + name + "5.png");
    		
    }
    public void setIDInfo(int id)
    {
    	if(id == 0) //chunk
        {
        	Random rand = new Random();
        	Integer a = rand.nextInt(4)+1 ;
        	setImage("SkillImages/RockB" + a.toString() + ".png");
        	y = player.getY() + player.height - height - 5;
        	s.desy = rand.nextInt(player.height/2) + player.getY();
        	if(right)
        		x = player.getX() + player.width;
        	else
        		x = player.getX() - width;
        }
        else if(id == 2) //spire
		{
			y = player.getY() + player.height;
			x = s.desx;
			height = 1;
			boolean foundGround = false;
			while(foundGround == false)
			{
				for(Wall w: A.wall) //this is to make sure the spire spawns from the nearest ground
				{
					hitbox = getTop();
					if(w.wall.intersects(hitbox))
						foundGround = true;
					
				}
				y++;
			}
			s.desy = y - image.getHeight(null);
		}
        else if(id == 1)
        {
        	if(right)
        		x = player.getX() + player.width + 4;
        	else
        		x = player.getX() - 4;
			y = player.getY() + player.height - height - 4;
        }
        else if(s.id == 4)
        {
        	y = player.getY() + player.height;
        	if(right)
        		x = player.getX() + player.width;
        	else
        		x = player.getX() - width;
			height = 1;
			s.desy = player.getY() + player.height - image.getHeight(null);
        }
        else if(id == 5)
        {
        	height = 1;
        	width = 1;
        	x = player.x;
        	y = player.y;
        }
        else if(id == 6)
        {
        	height = 1;
        	x = s.desx;
        	boolean foundGround = false;
			while(foundGround == false)
			{
				for(Wall w: A.wall) //this is to make sure the spire spawns from the nearest ground
				{
					hitbox = getTop();
					if(w.wall.intersects(hitbox))
						foundGround = true;
					
				}
				y++;
			}
			isBreakable = false;
			s.desy = y - image.getHeight(null);
        	
        }
        else if(id == 7)
        {
        	y = player.getY() + player.height - height;
        	if(right)
        		x = player.getX() + 4 + player.width;
        	else
        		x = player.getX() - 4; //d
        }
        else if(id == 16)
        {
        	y = player.getY() + 20;
        	if(right)
        		x = player.getX() + player.width;
        	else
        		x = player.getX() - width;
        }
    }
}
