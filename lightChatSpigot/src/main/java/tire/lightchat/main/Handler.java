package tire.lightchat.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tire.lightchat.messages.*;
import tire.lightchat.modules.ReplaceMethods;
import tire.lightchat.chats.Global;
import tire.lightchat.chats.Local;
import tire.lightchat.chats.World;

public class Handler implements Listener {
    private LightChat plugin;
    public Handler(LightChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        ReplaceMethods ReplaceMethods = new ReplaceMethods(plugin);

        String Message = e.getMessage();
        Player Player = e.getPlayer();
        String PlayerName = Player.getName();

        //Префиксы
        String[] prefixes  = new String[3];
        prefixes[0] = plugin.getConfig().getString("globalChat.prefix"); // Глобальный чат
        prefixes[1] = plugin.getConfig().getString("localChat.prefix"); // Локальный чат
        prefixes[2] = plugin.getConfig().getString("worldChat.prefix"); // Мировой чат

        String prefixError = plugin.getConfig().getString("prefixError");
        String remExtraSpaces = plugin.getConfig().getString("remExtraSpaces");

        //Режим вкл/выкл
        String[] enables  = new String[3];
        enables[0] = plugin.getConfig().getString("globalChat.enable"); // Глобальный чат
        enables[1] = plugin.getConfig().getString("localChat.enable"); // Локальный чат
        enables[2] = plugin.getConfig().getString("worldChat.enable"); // Мировой чат
        
        if(Message.length() != 0) {
            // Проверка на совпадение первого символа сообщения с одним из префиксов
            for (int i = 0; i < 3; i++) {
                if (enables[i].equalsIgnoreCase("true") && prefixes[i].equals(Message.substring(0, prefixes[i].length())) && !prefixes[i].equals("")) {
                    callChatClass(i, Message, prefixes[i], e, plugin);
                    return;
                }
            }
            // Если не совпало, то проверяем на совпадение
            // одного из префиксов с пустой строкой
            for (int i = 0; i < 3; i++) {
                if (enables[i].equalsIgnoreCase("true") && prefixes[i].equals("")) {
                    callChatClass(i, Message, prefixes[i], e, plugin); // При первом же совпадении вызываем соответствующий класс
                    return;
                }
            }
            // Иначе просто пишем игроку сообщение об ошибке
            prefixError = ReplaceMethods.prefixes(prefixError);
            prefixError = ReplaceMethods.player(prefixError, Player, "sender");
            Player.sendMessage(ReplaceMethods.message(prefixError, Message));
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        new Join(plugin).join(e);
    }
    @EventHandler
    public void quit(PlayerQuitEvent e) { new Quit(plugin).quit(e); }

    public void callChatClass(int id, String Message,
                              String Prefix, AsyncPlayerChatEvent e,
                              LightChat plugin) {
        Message = Message.substring(Prefix.length()).trim();
        if(Message.length() != 0) {
            switch (id) {
                case (0):
                    new Global(plugin).sendMessage(Message, e);
                    break;
                case (1):
                    new Local(plugin).sendMessage(Message, e);
                    break;
                case (2):
                    new World(plugin).sendMessage(Message, e);
                    break;
            }
        }
    }
}