package com.nfsprodriver.gungame.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemDrop implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemMeta meta = event.getItemDrop().getItemStack().getItemMeta();
        if (meta != null && meta.getDisplayName().equals("Basic sword")) {
            event.setCancelled(true);
        }
    }
}
