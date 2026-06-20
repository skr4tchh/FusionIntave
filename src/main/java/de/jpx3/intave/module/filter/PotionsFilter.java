package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityMetadataReader;
import de.jpx3.intave.packet.reader.EntityReader;
import de.jpx3.intave.packet.reader.PacketReaders;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_EFFECT;

public class PotionsFilter extends Filter {

    private final IntavePlugin plugin;

    public PotionsFilter(IntavePlugin plugin) {
        super("potions");
        this.plugin = plugin;
    }

    @PacketSubscription(
            packetsOut = {
                    ENTITY_EFFECT
            },
            priority = ListenerPriority.NORMAL
    )
    public void deprivePotions(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        EntityReader reader = PacketReaders.readerOf(packet);
        if(reader.entityId() != event.getPlayer().getEntityId()) {
            event.setCancelled(true);
        }
        reader.release();
    }

}
