package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityMetadataReader;
import de.jpx3.intave.packet.reader.EntityReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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

        int entityId = packet.getIntegers().read(0);
        if (event.getPlayer().getEntityId() != entityId) {
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> slotStackPairs = packet.getSlotStackPairLists().read(0);
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> modifiedPairs = new ArrayList<>();

            for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : slotStackPairs) {
                ItemStack itemStack = pair.getSecond();
                itemStack = stripFromData(itemStack);
                modifiedPairs.add(new Pair<>(pair.getFirst(), itemStack));
            }

            event.getPacket().getSlotStackPairLists().write(0, modifiedPairs);
        }
    }

    private ItemStack stripFromData(ItemStack itemStack) {
        if (itemStack.getAmount() > 1)
            itemStack.setAmount(1);

        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasEnchants()) {
                for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                    itemStack.removeEnchantment(enchantment);
                }
                itemStack.addUnsafeEnchantment(Enchantment.THORNS, 1);
            }
            if (meta instanceof Damageable) {
                itemStack.setDurability((short) 0);
            }
        }

        return itemStack;
    }

    @Override
    protected boolean enabled() {
        return MinecraftVersions.VER1_21_4.atOrAbove() && super.enabled();
    }

}
