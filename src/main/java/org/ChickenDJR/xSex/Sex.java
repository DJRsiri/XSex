package org.ChickenDJR.xSex;

import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Sex
{
    private Player male;
    private Player female;
    private int time = 0; private BukkitTask effects;

    public Sex(Player paramPlayer1, Player paramPlayer2) {
        this.male = paramPlayer1;
        this.female = paramPlayer2;
        start();
    }
    private BukkitTask end;

    public void start() {
        this.male.sendMessage(Main.getInstance().getConfig().getString("messages.start"));
        this.female.sendMessage(Main.getInstance().getConfig().getString("messages.start"));
        this.male.sendTitle(Main.getInstance().getConfig().getString("messages.start-title-header"), Main.getInstance().getConfig().getString("messages.start-title-footer"));
        this.female.sendTitle(Main.getInstance().getConfig().getString("messages.start-title-header"), Main.getInstance().getConfig().getString("messages.start-title-footer"));
        this.male.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 220, 1));
        this.female.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 220, 1));
        this.effects = (new BukkitRunnable() {
            public void run() {
                Sex.this.time++;
                if (!Sex.this.male.isOnline() || !Sex.this.female.isOnline()) {
                    Sex.this.effects.cancel();
                    Sex.this.end.cancel();
                    cancel();
                    return;
                }
                if (Sex.this.male.getLocation().distanceSquared(Sex.this.female.getLocation()) >= 10.0D) {
                    Sex.this.effects.cancel();
                    Sex.this.end.cancel();
                    cancel();
                    return;
                }
                Iterator<? extends Player> localIterator = Bukkit.getOnlinePlayers().iterator();
                while (localIterator.hasNext()) {
                    Player localPlayer = localIterator.next();
                    localPlayer.getWorld().spawnParticle(Particle.HEART, Sex.this.male.getLocation(), 0, 255.0D, 0.0D, 0.0D, 1.0D);
                    localPlayer.getWorld().spawnParticle(Particle.HEART, Sex.this.female.getLocation(), 0, 255.0D, 0.0D, 0.0D, 1.0D);
                }
                Sex.this.male.playSound(Sex.this.male.getLocation(), Sound.ENTITY_CAT_PURR, 2.0F, 1.0F);
                Sex.this.female.playSound(Sex.this.female.getLocation(), Sound.ENTITY_CAT_PURR, 2.0F, 1.0F);
                Sex.this.male.sendMessage(Main.getInstance().getConfig().getString("messages.sex-male-" + Sex.this.time));
                Sex.this.female.sendMessage(Main.getInstance().getConfig().getString("messages.sex-female-" + Sex.this.time));
            }
        }).runTaskTimer((Plugin)Main.getInstance(), 20L, 20L);
        this.end = (new BukkitRunnable() {
            public void run() {
                Sex.this.effects.cancel();
                Sex.this.end();
            }
        }).runTaskLater((Plugin)Main.getInstance(), 208L);
    }

    public void end() {
        Villager localVillager;
        if (!this.male.isOnline() || !this.female.isOnline()) {
            cancel();
            return;
        }
        int i = (new Random()).nextInt(5);
        switch (i) {
            case 0:
                this.male.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                this.male.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 1));
                this.female.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                this.male.sendMessage(Main.getInstance().getConfig().getString("messages.end-male-1"));
                this.female.sendMessage(Main.getInstance().getConfig().getString("messages.end-female-1"));
                break;
            case 1:
                this.male.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                this.female.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                this.male.sendMessage(Main.getInstance().getConfig().getString("messages.end-male-2"));
                this.female.sendMessage(Main.getInstance().getConfig().getString("messages.end-female-2"));
                break;
            case 2:
                this.male.sendMessage(Main.getInstance().getConfig().getString("messages.end-male-3"));
                this.female.sendMessage(Main.getInstance().getConfig().getString("messages.end-female-3"));
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), Main.getInstance().getConfig().getString("messages.end-command-3").replace("%player", this.male.getName()));
                Bukkit.broadcastMessage(Main.getInstance().getConfig().getString("messages.end-broadcast-3").replace("%player1", this.female.getName()).replace("%player2", this.male.getName()));
                break;
            case 3:
                this.male.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                this.female.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                this.male.sendMessage(Main.getInstance().getConfig().getString("messages.end-male-4"));
                this.female.sendMessage(Main.getInstance().getConfig().getString("messages.end-female-4"));
                break;
            case 4:
                this.male.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                this.female.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                localVillager = (Villager)this.female.getLocation().getWorld().spawnEntity(this.female.getLocation(), EntityType.VILLAGER);
                localVillager.setCustomNameVisible(true);
                localVillager.setCustomName("孩子");
                localVillager.setAge(55537);
                this.male.sendMessage(Main.getInstance().getConfig().getString("messages.end-male-5"));
                this.female.sendMessage(Main.getInstance().getConfig().getString("messages.end-female-5"));
                Bukkit.broadcastMessage(Main.getInstance().getConfig().getString("messages.end-broadcast-5").replace("%player1", this.female.getName()).replace("%player2", this.male.getName())); break;
        }
        this.male.sendTitle(Main.getInstance().getConfig().getString("messages.end-title-header"), Main.getInstance().getConfig().getString("messages.end-title-footer"));
        this.female.sendTitle(Main.getInstance().getConfig().getString("messages.end-title-header"), Main.getInstance().getConfig().getString("messages.start-title-footer"));
        (Main.getInstance().getManager()).alreadySex.remove(this.male);
        (Main.getInstance().getManager()).alreadySex.remove(this.female);
        (Main.getInstance().getManager()).cooldowns.put(this.male.getName(), Integer.valueOf(60));
        (Main.getInstance().getManager()).cooldowns.put(this.female.getName(), Integer.valueOf(60));
        Main.getInstance().getManager().getPlayer(this.male).addAmount();
        Main.getInstance().getManager().getPlayer(this.female).addAmount();
    }

    public void extraCancel() {
        this.effects.cancel();
        this.end.cancel();
        cancel();
    }

    public void cancel() {
        if (this.male.isOnline()) {
            this.male.removePotionEffect(PotionEffectType.BLINDNESS);
            this.male.sendMessage(Main.getInstance().getConfig().getString("messages.sex-cancel"));
        }
        if (this.female.isOnline()) {
            this.female.removePotionEffect(PotionEffectType.BLINDNESS);
            this.female.sendMessage(Main.getInstance().getConfig().getString("messages.sex-cancel"));
        }
        (Main.getInstance().getManager()).alreadySex.remove(this.male);
        (Main.getInstance().getManager()).alreadySex.remove(this.female);
    }
}