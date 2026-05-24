package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityMetadataReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import org.bukkit.entity.*;

import java.util.ArrayList;
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
        PacketContainer packet = event.getPacket();
        EntityMetadataReader reader = PacketReaders.readerOf(packet);
        Entity entity = reader.entityBy(event);
        if (entity == null || !entity.getType().isAlive() || entity instanceof EnderDragon || entity instanceof Wither || event.getPlayer().getEntityId() == entity.getEntityId()) {
            reader.release();
            return;
        }

        List<WrappedDataValue> dataValues = event.getPacket().getDataValueCollectionModifier().read(0);
        for (int i = 0; i < dataValues.size(); i++) {
            WrappedDataValue dataValue = dataValues.get(i);
            if (dataValue.getIndex() == 9) {
                dataValues.set(i, (new WrappedDataValue(
                        dataValue.getIndex(),
                        dataValue.getSerializer(),
                        createFakeHealth()
                )));
            }
        }

        event.getPacket().getDataValueCollectionModifier().write(0, dataValues);
        reader.release();
    }

    private float createFakeHealth() {
        return Math.max(1, (float) (Math.random() * 20.0F));
    }

    @Override
    protected boolean enabled() {
        return MinecraftVersions.VER1_21_4.atOrAbove() && super.enabled();
    }

}
