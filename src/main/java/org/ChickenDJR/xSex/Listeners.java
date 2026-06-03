package org.ChickenDJR.xSex;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners
        implements Listener {
    private Main pl;

    public Listeners(Main paramMain) {
        this.pl = paramMain;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player localPlayer = (Player)e.getEntity();
        if ((this.pl.getManager()).alreadySex.containsKey(localPlayer)) {
            ((Sex)(this.pl.getManager()).alreadySex.get(localPlayer)).extraCancel();
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent paramPlayerJoinEvent) {
        this.pl.getManager().loadPlayer(paramPlayerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent paramPlayerQuitEvent) {
        if ((this.pl.getManager()).players.containsKey(paramPlayerQuitEvent.getPlayer().getName())) {
            (this.pl.getManager()).players.remove(paramPlayerQuitEvent.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent paramPlayerKickEvent) {
        if ((this.pl.getManager()).players.containsKey(paramPlayerKickEvent.getPlayer().getName()))
            (this.pl.getManager()).players.remove(paramPlayerKickEvent.getPlayer().getName());
    }
}