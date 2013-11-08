if(player.isRight == true)
	g2d.drawImage(player.makeColorTransparent(Color.white), player.getX(), player.getY(),this);
else
{
	BufferedImage i = player.makeColorTransparent(Color.white);
	g2d.drawImage(player.horizontalflip(i), player.getX(), player.getY(),this);
}