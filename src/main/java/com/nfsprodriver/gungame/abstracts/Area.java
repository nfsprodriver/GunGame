package com.nfsprodriver.gungame.abstracts;

import org.bukkit.Location;

public class Area {
    public Location loc1;
    public Location loc2;

    public Area(Location defLoc, double x1, double x2, double z1, double z2) {
        this.loc1 = defLoc.clone();
        this.loc1.setX(x1);
        this.loc1.setZ(z1);
        this.loc2 = defLoc.clone();
        this.loc2.setX(x2);
        this.loc2.setZ(z2);
    }
    
    public boolean locIsInArea(Location loc) {
        return (loc.getX() > this.loc1.getX() && loc.getX() < this.loc2.getX() && loc.getZ() > this.loc1.getZ() && loc.getZ() < this.loc2.getZ() && this.loc1.getWorld() == loc.getWorld());
    }
}
