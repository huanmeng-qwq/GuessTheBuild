package me.huanmeng.guessthebuild.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Cube {
	
	private int x1, x2, y1, y2, z1, z2;
	private World w;
	
	public Cube(Location loc1,Location loc2){
		
		this.w = loc1.getWorld();
		int x1=loc1.getBlockX();
		int y1=loc1.getBlockY();
		int z1=loc1.getBlockZ();
		int x2=loc2.getBlockX();
		int y2=loc2.getBlockY();
		int z2=loc2.getBlockZ();
		if(x1 > x2){
			this.x1 = x1;
			this.x2 = x2;
		}
		else{
			this.x1 = x2;
			this.x2 = x1;
		}
		if(y1 > y2){
			this.y1 = y1;
			this.y2 = y2;
		}
		else{
			this.y1 = y2;
			this.y2 = y1;
		}
		if(z1 > z2){
			this.z1 = z1;
			this.z2 = z2;
		}
		else{
			this.z1 = z2;
			this.z2 = z1;
		}
	}
	
	public int getMaxX() {
		return this.x1;
	}
	
	public int getMinX() {
		return this.x2;
	}
	
	public int getMaxY() {
		return this.y1;
	}
	
	public int getMinY() {
		return this.y2;
	}
	
	public int getMaxZ() {
		return this.z1;
	}
	
	public int getMinZ() {
		return this.z2;
	}
	
	public ArrayList<Block> getBlocks(){
		
		if(x1<0)this.x1--;
		if(x2<0)this.x2--;
		if(z1<0)this.z1--;
		if(z2<0)this.z2--;
		ArrayList<Block> blocks = new ArrayList<Block>();
		for(int a = x2; a <= x1; a++){
			for(int b = y2; b <= y1; b++){
				for(int c = z2; c <= z1; c++){
					
					blocks.add(new Location(w, a, b, c).getBlock());
					
				}
			}
		}
		if(x1<=0)this.x1++;
		if(x2<=0)this.x2++;
		if(z1<=0)this.z1++;
		if(z2<=0)this.z2++;
		return blocks;
	}
	
	public boolean isLocationInCube(Location loc) {
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		
		if(x1>=x && x2<=x && y1>=y && y2<=y && z1>=z && z2<=z) return true;
		return false;
	}
	public boolean isPlayerInCube(Player p){
		
		int x = (int) p.getLocation().getX();
		int y = (int) p.getLocation().getY();
		int z = (int) p.getLocation().getZ();
		
		if(x1>=x && x2<=x && y1>=y && y2<=y && z1>=z && z2<=z) return true;
		return false;
	}

}
