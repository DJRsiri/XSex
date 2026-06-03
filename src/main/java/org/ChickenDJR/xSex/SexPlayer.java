package org.ChickenDJR.xSex;

import org.bukkit.entity.Player;


public class SexPlayer
{
    private Player player;
    private int gender;
    private int amount;

    public SexPlayer(Player paramPlayer) {
        this.player = paramPlayer;
        this.gender = Main.getInstance().getConfig().getInt("players." + this.player.getName() + ".gender");
        this.amount = Main.getInstance().getConfig().getInt("players." + this.player.getName() + ".amount");
    }


    public Player getPlayer() {
        return this.player;
    }


    public int getGender() {
        return this.gender;
    }


    public void setGender(int paramInt) {
        this.gender = paramInt;
        Main.getInstance().getConfig().set("players." + getPlayer().getName() + ".gender", Integer.valueOf(this.gender));
        Main.getInstance().saveConfig();
    }


    public int getAmount() {
        return this.amount;
    }


    public void addAmount() {
        this.amount++;
        Main.getInstance().getConfig().set("players." + getPlayer().getName() + ".amount", Integer.valueOf(this.amount));
        Main.getInstance().saveConfig();
    }
}