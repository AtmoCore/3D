package com.thebigo.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;


public class main extends JavaPlugin implements Listener, CommandExecutor{
	File objectFile;
	HashMap<Integer, Location> dots = new HashMap<Integer, Location>();
	Location loc1,loc2 = null;
	public void onEnable() { 
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() { 
	
	}
	
	public void connectTwoDots(Location location, Location newLocation){
		if(Math.floor(location.distance(newLocation)) == 0) {
			return;
		}
		BlockIterator blocksToAdd = new BlockIterator(location.getWorld(), location.toVector(),
				new Vector(newLocation.getBlockX()-location.getBlockX(), 
						newLocation.getBlockY()-location.getBlockY(), 
						newLocation.getBlockZ()-location.getBlockZ()), 0,
				(int) Math.floor(location.distance(newLocation)));
		Location blockToAdd;
		while(blocksToAdd.hasNext()) {
		    blockToAdd = blocksToAdd.next().getLocation();
			 if(blockToAdd.getBlock().getType().equals(Material.AIR)) (new Location(blockToAdd.getWorld(), blockToAdd.getBlockX(), blockToAdd.getBlockY(), blockToAdd.getBlockZ())).getBlock().setType(Material.STONE);;
		}
	}
	
	public void createTriangle(Location first, Location second, Location third){
		connectTwoDots(first, second);
		connectTwoDots(second, third);
		connectTwoDots(third, first);
	}
	
	@EventHandler
	public void selectPos(PlayerInteractEvent e) {
		if(e.getPlayer().getItemInHand().getType().equals(Material.STICK) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			loc1 = e.getClickedBlock().getLocation();
			e.getPlayer().sendMessage("1 made");
		}
		if(e.getPlayer().getItemInHand().getType().equals(Material.STICK) && e.getAction() == Action.LEFT_CLICK_BLOCK) {
			loc2 = e.getClickedBlock().getLocation();
			e.getPlayer().sendMessage("2 made");
			connectTwoDots(loc1,loc2);
		}
	}

	
	@Override
	 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("empty")) {
		for(int x = -50; x <= 50; x++) {
			for(int y = -50; y <= 50; y++) {
				for(int z = -50; z <= 50; z++) {
					if(!new Location(Bukkit.getWorld("world"),x,y,z).getBlock().getType().equals(Material.AIR))
					new Location(Bukkit.getWorld("world"),x,y,z).getBlock().setType(Material.AIR);
				}
			}
		}
		
		}
		if(label.equalsIgnoreCase("create")) {
			float times = 25;
			if(args.length == 2) {
				times = Float.parseFloat(args[0]);
				objectFile = new File("E:\\objects\\"+args[1]);
			}
			try (BufferedReader br = new BufferedReader(new FileReader(objectFile))) {
			    String line;
			    try {
			    	int i = 0;
			    	int o = 0;
					   Player p = (Player) sender;
					   Location loc = p.getLocation();
					   int x,y,z;
					   x = loc.getBlockX();
					   y = loc.getBlockY();
					   z = loc.getBlockZ();
					   World w = Bukkit.getWorld("world");
			    	  Bukkit.broadcastMessage("start");
					while ((line = br.readLine()) != null) {
					   if(line.startsWith("v ")) {
						 i++;
						 String[] pos = line.replaceAll("v ", "").split(" ");
						 Bukkit.broadcastMessage(x+" - "+y+" - "+z);
						 Block b = (new Location(w, x+Float.parseFloat(pos[0])*times, y+Float.parseFloat(pos[1])*times, z+Float.parseFloat(pos[2])*times)).getBlock();
						 dots.put(i, b.getLocation());
						 //Bukkit.broadcastMessage("BLOCK AT: "+b.getLocation().getBlockX());
						 if(b.getType().equals(Material.AIR)) b.setType(Material.STONE);
						   if(i%5000 == 0) {
								try {
									TimeUnit.SECONDS.sleep(5);
								} catch (InterruptedException e) {
									e.printStackTrace();
									}
							   	}
					   }else if(line.startsWith("f ")) {
						   o++;
						   Bukkit.broadcastMessage("yo heres one f");
						  String[] prePos = line.replaceAll("f ", "").split(" ");
						  int pos1,pos2,pos3;
						  if(line.contains("/")) {
							  pos1 = Integer.parseInt(prePos[0].split("/")[0]);
							  pos2 = Integer.parseInt(prePos[1].split("/")[0]);
							  pos3 = Integer.parseInt(prePos[2].split("/")[0]);
						  }else {
						  pos1 = Integer.parseInt(prePos[0]);
						  pos2 = Integer.parseInt(prePos[1]);
						  pos3 = Integer.parseInt(prePos[2]);
						  }
						  Bukkit.broadcastMessage("\n"+dots.get(pos1).getBlockX()+" - "+ dots.get(pos2).getBlockY()+" - "+dots.get(pos3).getBlockZ());
							  createTriangle(dots.get(pos1), dots.get(pos2), dots.get(pos3));
					if(o%5000 == 0) {
									try {
										TimeUnit.SECONDS.sleep(5);
									} catch (InterruptedException e) {
										e.printStackTrace();
										}
								   	}
					   }
					   }
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				
				e1.printStackTrace();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		}
		return false;
	}
	
}
