package dev.dunkleente.listener;

import dev.dunkleente.Main;
import dev.dunkleente.database.InvestData;
import dev.dunkleente.utility.ColorUtil;
import dev.dunkleente.utility.MoneyUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private final Map<UUID, Long> waitingForInput = new ConcurrentHashMap<>();

    public ChatListener() {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void addWaitingPlayer(UUID uuid) {
        waitingForInput.put(uuid, System.currentTimeMillis());
    }

    public boolean isWaiting(UUID uuid) {
        return waitingForInput.containsKey(uuid);
    }

    public void removeWaitingPlayer(UUID uuid) {
        waitingForInput.remove(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (!isWaiting(uuid)) {
            return;
        }

        event.setCancelled(true);
        removeWaitingPlayer(uuid);

        final String input = event.getMessage().trim();

        try {
            long amount = MoneyUtil.parse(input);

            if (amount <= 0) {
                player.sendRichMessage("<#8DFB08><b>INVEST</b> <#CACACA> <white>The amount must be higher than 0!");
                player.playSound(player, Sound.ENTITY_VILLAGER_HURT,1,1);

                return;
            }

            if (!Main.getInstance().getVaultManager().has(player, amount)) {
                player.sendRichMessage("<#8DFB08><b>INVEST</b> <#CACACA> <#FF0000>You don't have enough money for this.");
                player.playSound(player, Sound.ENTITY_VILLAGER_HURT,1,1);

                return;
            }

            final InvestData data = Main.getInstance().getInvestManager().getData(uuid);

            Main.getInstance().getVaultManager().withdraw(player, amount);
            data.setInvestedMoney(data.getInvestedMoney() + amount);
            Main.getInstance().getDbManager().markDirty(data);

            player.sendRichMessage("<#8DFB08><b>INVEST</b> <#CACACA> <white>Successfully invested <#8DFB08>$" + MoneyUtil.format(amount));
        } catch (IllegalArgumentException e) {
            player.sendMessage(ColorUtil.parse("<#8DFB08><b>INVEST</b> <#CACACA> <#FF0000>Invalid amount format."));
        }
    }
}
