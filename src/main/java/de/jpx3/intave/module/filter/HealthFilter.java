package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityMetadataReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

import java.util.List;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_METADATA;

public final class HealthFilter extends Filter {

    private final IntavePlugin plugin;

    public HealthFilter(IntavePlugin plugin) {
        super("health");
        this.plugin = plugin;
    }

    @PacketSubscription(
            packetsOut = {
                    ENTITY_METADATA
            },
            priority = ListenerPriority.NORMAL
    )
    public void depriveHealth(PacketEvent event) {
        // Rule #3151235: When editing metadata, do a deepClone().
        // Why? I still don't know after 5 hours of debugging.
        event.setPacket(event.getPacket().deepClone());
        PacketContainer packet = event.getPacket();
        EntityMetadataReader reader = PacketReaders.readerOf(packet);
        Entity entity = reader.entityBy(event);
        if (entity == null || entity instanceof EnderDragon || entity instanceof Wither) {
            reader.release();
            return;
        }

        if (entity instanceof LivingEntity && entity.getEntityId() != event.getPlayer().getEntityId()) {
            List<WrappedDataValue> dataValues = packet.getDataValueCollectionModifier().read(0);
            if (dataValues != null) {
                for (WrappedDataValue dataValue : dataValues) {
                    if (dataValue.getIndex() == 9 && dataValue.getValue() instanceof Float) {
                        float currentHealth = (float) dataValue.getValue();
                        if (currentHealth > 0.0F) {
                            dataValue.setValue(createFakeHealth());
                        }
                    }
                }
                packet.getDataValueCollectionModifier().write(0, dataValues);
            }
        }

        reader.release();
    }

    private float createFakeHealth() {
        return Math.max(1, (float) (Math.random() * 20.0F));
    }

    @Override
    protected boolean enabled() {
        return super.enabled() && MinecraftVersions.VER1_21_4.atOrAbove();
    }

}
