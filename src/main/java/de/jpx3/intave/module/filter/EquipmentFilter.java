package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_EQUIPMENT;

public final class EquipmentFilter extends Filter {

    private final IntavePlugin plugin;

    public EquipmentFilter(IntavePlugin plugin) {
        super("equipmentdata");
        this.plugin = plugin;
    }

    @PacketSubscription(
            packetsOut = {
                    ENTITY_EQUIPMENT
            }
    )
    public void filterEquipment(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        EntityReader reader = PacketReaders.readerOf(packet);
        if (reader.entityId() == event.getPlayer().getEntityId()) {
            reader.release();
            return;
        }

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> read = packet.getSlotStackPairLists().read(0);
        if (read != null && !read.isEmpty()) {
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> modifiedPairs = new ArrayList<>();
            for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : read) {
                ItemStack itemStack = pair.getSecond();
                if (itemStack == null || itemStack.getType().name().contains("AIR")) {
                    modifiedPairs.add(pair);
                    continue;
                }
                ItemStack newItemStack = stripFromData(itemStack.clone());
                modifiedPairs.add(new Pair<>(pair.getFirst(), newItemStack));
            }
            packet.getSlotStackPairLists().write(0, modifiedPairs);
        }

        reader.release();
    }

    private ItemStack stripFromData(ItemStack itemStack) {
        if (itemStack.getAmount() > 1) {
            itemStack.setAmount(1);
        }

        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta.hasEnchants()) {
                for (Enchantment enchantment : new ArrayList<>(meta.getEnchants().keySet())) {
                    meta.removeEnchant(enchantment);
                }
                meta.addEnchant(Enchantment.THORNS, 1, true);
            }

            if(meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                int maxDurability = itemStack.getType().getMaxDurability();
                if (maxDurability > 0) {
                    int fakeDamage = (int) (Math.random() * maxDurability);
                    int finalDamage = Math.max(1, Math.min(fakeDamage, maxDurability - 1));
                    damageable.setDamage(finalDamage);
                }
            }

            meta.setDisplayName("");
            if (meta.getLore() != null) {
                meta.setLore(Collections.emptyList());
            }

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    @Override
    protected boolean enabled() {
        return super.enabled() && MinecraftVersions.VER1_21_4.atOrAbove();
    }

}
