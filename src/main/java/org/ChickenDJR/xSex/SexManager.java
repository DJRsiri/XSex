package org.ChickenDJR.xSex;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SexManager {
    private Main pl;
    public HashMap<String, SexPlayer> players = new HashMap<>();
    public HashMap<Player, Sex> alreadySex = new HashMap<>();
    public HashMap<String, String> requests = new HashMap<>();
    public HashMap<String, Integer> cooldowns = new HashMap<>();
    public HashMap<String, Integer> timeout = new HashMap<>();


    public SexManager(Main paramMain) {
        this.pl = paramMain;
        (new BukkitRunnable()
        {

            public void run()
            {
                Iterator<String> localIterator = SexManager.this.cooldowns.keySet().iterator();
                while (localIterator.hasNext())
                {
                    String str = localIterator.next();
                    int i = ((Integer)SexManager.this.cooldowns.get(str)).intValue() - 1;
                    if (i == 0) {
                        localIterator.remove(); continue;
                    }
                    SexManager.this.cooldowns.replace(str, Integer.valueOf(i));
                }

            }
        }).runTaskTimerAsynchronously((Plugin)this.pl, 0L, 20L);
        (new BukkitRunnable()
        {
            public void run()
            {
                Iterator<String> localIterator = SexManager.this.timeout.keySet().iterator();
                while (localIterator.hasNext())
                {
                    String str = localIterator.next();
                    int i = ((Integer)SexManager.this.timeout.get(str)).intValue() - 1;
                    if (i == 0) {

                        localIterator.remove();
                        SexManager.this.requests.remove(str);

                        continue;
                    }
                    SexManager.this.timeout.replace(str, Integer.valueOf(i));
                }

            }
        }).runTaskTimerAsynchronously((Plugin)this.pl, 0L, 20L);
    }


    public void stop() {
        Iterator<Player> localIterator = this.alreadySex.keySet().iterator();
        while (localIterator.hasNext()) {

            Player localPlayer = localIterator.next();
            Sex localSex = this.alreadySex.get(localPlayer);
            localSex.extraCancel();
            localIterator.remove();
        }
    }


    public void sex(Player paramPlayer1, Player paramPlayer2) {
        Sex localSex = new Sex(paramPlayer1, paramPlayer2);
        this.alreadySex.put(paramPlayer1, localSex);
        this.alreadySex.put(paramPlayer2, localSex);
        this.requests.remove(paramPlayer1.getName());
        this.requests.remove(paramPlayer2.getName());
    }


    public void loadPlayer(Player paramPlayer) {
        this.players.put(paramPlayer.getName(), new SexPlayer(paramPlayer));
    }


    public SexPlayer getPlayer(Player paramPlayer) {
        if (this.players.get(paramPlayer.getName()) == null) {

            this.players.put(paramPlayer.getName(), new SexPlayer(paramPlayer));
            return this.players.get(paramPlayer.getName());
        }
        return this.players.get(paramPlayer.getName());
    }


    public void checkGender(Player paramPlayer1, Player paramPlayer2) {
        SexPlayer localSexPlayer = getPlayer(paramPlayer1);
        if (localSexPlayer.getGender() == 1) {
            sex(paramPlayer1, paramPlayer2);
        } else {
            sex(paramPlayer2, paramPlayer1);
        }
    }


    public void acceptRequest(Player paramPlayer, String paramString) {
        if (!this.requests.containsKey(paramString) || (this.requests.containsKey(paramString) && !((String)this.requests.get(paramString)).equals(paramPlayer.getName()))) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-10").replace("%player", paramString));
            return;
        }
        if (this.cooldowns.containsKey(paramPlayer.getName())) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-8").replace("%time", String.valueOf(this.cooldowns.get(paramPlayer.getName()))));
            return;
        }
        if (this.alreadySex.containsKey(paramPlayer)) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-7"));
            return;
        }
        Player localPlayer = Bukkit.getPlayer(paramString);
        if (localPlayer == null) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-11"));
            return;
        }
        if (this.alreadySex.containsKey(localPlayer)) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-12"));
            return;
        }
        SexPlayer localSexPlayer1 = getPlayer(localPlayer);
        SexPlayer localSexPlayer2 = getPlayer(paramPlayer);
        if (localSexPlayer1.getGender() == 0) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-4"));
            return;
        }
        if (localSexPlayer2.getGender() == 0) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-3"));
            return;
        }
        if (localSexPlayer1.getGender() == localSexPlayer2.getGender()) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-2"));
            return;
        }
        if (paramPlayer.getLocation().distanceSquared(localPlayer.getLocation()) >= 10.0D) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-1"));
            return;
        }
        if (this.cooldowns.containsKey(localPlayer.getName())) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-13"));
            return;
        }
        checkGender(paramPlayer, localPlayer);
    }


    public void denyRequest(Player paramPlayer, String paramString) {
        if (!this.requests.containsKey(paramString) || (this.requests.containsKey(paramString) && !((String)this.requests.get(paramString)).equals(paramPlayer.getName()))) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-10").replace("%player", paramString));
        }
        else {

            this.requests.remove(paramString);
            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.sucessfull-1").replace("%player", paramString));
        }
    }


    public void sendRequest(Player paramPlayer, String paramString) {
        if (paramPlayer.getName().equals(paramString)) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-9"));
            return;
        }
        if (this.cooldowns.containsKey(paramPlayer.getName())) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-8").replace("%time", String.valueOf(this.cooldowns.get(paramPlayer.getName()))));
            return;
        }
        if (this.alreadySex.containsKey(paramPlayer)) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-7"));
            return;
        }
        if (this.timeout.containsKey(paramPlayer.getName())) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-6"));
            return;
        }
        Player localPlayer = Bukkit.getPlayer(paramString);
        if (localPlayer == null) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-5"));
            return;
        }
        SexPlayer localSexPlayer1 = getPlayer(localPlayer);
        SexPlayer localSexPlayer2 = getPlayer(paramPlayer);
        if (localSexPlayer1.getGender() == 0) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-4"));
            return;
        }
        if (localSexPlayer2.getGender() == 0) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-3"));
            return;
        }
        if (localSexPlayer1.getGender() == localSexPlayer2.getGender()) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-2"));
            return;
        }
        if (paramPlayer.getLocation().distanceSquared(localPlayer.getLocation()) > 10.0D) {

            paramPlayer.sendMessage(this.pl.getConfig().getString("messages.requests.error-1"));
        }
        else {

            this.requests.put(paramPlayer.getName(), localPlayer.getName());
            Iterator<String> localIterator = this.pl.getConfig().getStringList("messages.requests.request-1").iterator();

            while (localIterator.hasNext()) {

                String str = localIterator.next();
                localPlayer.sendMessage(str.replace("%player", paramPlayer.getName()));
            }
            localIterator = this.pl.getConfig().getStringList("messages.requests.request-2").iterator();
            while (localIterator.hasNext()) {

                String str = localIterator.next();
                paramPlayer.sendMessage(str.replace("%player", localPlayer.getName()));
            }
            this.timeout.put(paramPlayer.getName(), Integer.valueOf(60));
        }
    }
}