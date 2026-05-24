package de.jpx3.intave.module.filter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.module.linker.packet.PacketSubscription;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_EFFECT;

public class PotionsFilter extends Filter {

    public PotionsFilter() {
        super("potions");
    }

    @PacketSubscription(
            packetsOut = {
                    ENTITY_EFFECT
            }
    )
    public void filterPotions(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        int entityId = packet.getIntegers().read(0);
        if (event.getPlayer().getEntityId() != entityId)
            event.setCancelled(true);
    }

}
