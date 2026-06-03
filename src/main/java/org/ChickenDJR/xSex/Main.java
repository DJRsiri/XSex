package org.ChickenDJR.xSex;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class Main
        extends JavaPlugin
{
    public static Main instance;
    private SexManager manager;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Listeners(this), (Plugin)this);
        this.manager = new SexManager(this);
    }

    public void onDisable() {
        this.manager.stop();
    }

    public static Main getInstance() {
        return instance;
    }

    public SexManager getManager() {
        return this.manager;
    }

    public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player)) {
            return true;
        }
        Player localPlayer = (Player)paramCommandSender;
        if (paramArrayOfString.length > 0)
        { String str;
            switch ((str = paramArrayOfString[0].toLowerCase()).hashCode()) { case -1423461112: if (!str.equals("accept"))
                break;
                if (paramArrayOfString.length > 1) {
                    this.manager.acceptRequest(localPlayer, paramArrayOfString[1]);
                    return true;
                }
                paramCommandSender.sendMessage(getConfig().getString("messages.help-accept"));
                return true;




                case -1249512767:
                    if (!str.equals("gender")) {
                        break;
                    }
                    if (paramArrayOfString.length > 1)
                    { if (paramArrayOfString[1].equalsIgnoreCase("male")) {
                        getManager().getPlayer(localPlayer).setGender(1);
                        paramCommandSender.sendMessage(getConfig().getString("messages.help-gender-2"));
                        return true;
                    }
                        if (paramArrayOfString[1].equalsIgnoreCase("female")) {
                            getManager().getPlayer(localPlayer).setGender(2);
                            paramCommandSender.sendMessage(getConfig().getString("messages.help-gender-3"));
                            return true;
                        }

                        paramCommandSender.sendMessage(getConfig().getString("messages.help-gender-1"));
                        return true; }  break;
                case 3079692: if (!str.equals("deny"))
                    break;  if (paramArrayOfString.length > 1) { this.manager.denyRequest(localPlayer, paramArrayOfString[1]); return true; }  paramCommandSender.sendMessage(getConfig().getString("messages.help-deny")); return true; }  }  if (paramArrayOfString.length ==


                1) {
            this.manager.sendRequest(localPlayer, paramArrayOfString[0]);
            return true;
        }

        SexPlayer localSexPlayer = getManager().getPlayer(localPlayer);
        String str2 = "未指定";
        switch (localSexPlayer.getGender()) {
            case 0:
                str2 = "未指定";
                break;
            case 1:
                str2 = "男性";
                break;
            case 2:
                str2 = "女性"; break;
        }
        for (String str3 : getConfig().getStringList("messages.help")) {
            paramCommandSender.sendMessage(str3.replace("%amount", String.valueOf(localSexPlayer.getAmount())).replace("%gender", str2));
        }
        return true;
    }
}
