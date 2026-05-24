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
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

import java.util.ArrayList;
import java.util.List;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_METADATA;

public final class HealthFilter extends Filter {

  private final IntavePlugin plugin;

  private final int HEALTH_METADATA_INDEX = 9;

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
      int entityId = packet.getIntegers().read(0);
      if (event.getPlayer().getEntityId() != entityId) {
          List<WrappedDataValue> dataValues = event.getPacket().getDataValueCollectionModifier().read(0);
          boolean shouldPush = false;

          for (int i = 0; i < dataValues.size(); i++) {
              WrappedDataValue dataValue = dataValues.get(i);
              if (dataValue.getIndex() == HEALTH_METADATA_INDEX && dataValue.getRawValue() instanceof Float && (float) dataValue.getRawValue() != 0.0F) {
                  dataValues.set(i, (new WrappedDataValue(
                          dataValue.getIndex(),
                          dataValue.getSerializer(),
                          createFakeHealth()
                  )));
                  shouldPush = true;
              }
          }

          if (shouldPush)
              event.getPacket().getDataValueCollectionModifier().write(0, dataValues);
      }
  }

  private float createFakeHealth() {
      return Math.max(1, (float) (Math.random() * 20.0F));
  }

    /*
  @PacketSubscription(
    packetsOut = {
      ENTITY_METADATA
    },
    priority = ListenerPriority.NORMAL
  )
  public void depriveHealth(PacketEvent event) {
    event.setPacket(event.getPacket().deepClone());
    PacketContainer packet = event.getPacket();
    EntityMetadataReader reader = PacketReaders.readerOf(packet);
    Entity entity = reader.entityBy(event);
    if (entity == null || entity instanceof EnderDragon || entity instanceof Wither) {
      reader.release();
      return;
    }
    List<WrappedWatchableObject> watchables = reader.legacyMetadataObjects();
    if (entity instanceof LivingEntity && entity.getEntityId() != event.getPlayer().getEntityId()) {
      if (watchables != null) {
        for (int i = 0; i < watchables.size(); i++) {
          WrappedWatchableObject watchable = watchables.get(i);
          if (watchable.getIndex() == 9 && watchable.getValue() instanceof Float) {
            watchable = new WrappedWatchableObject(watchable.getIndex(), watchable.getRawValue());
            stripHealthFrom(watchable);
            watchables.set(i, watchable);
          }
        }
      }
    }
    reader.setLegacyMetadataObjects(watchables);
    reader.release();
  }

  private void stripHealthFrom(WrappedWatchableObject watchable) {
    if (watchable != null && watchable.getIndex() == 6 && watchable.getRawValue() instanceof Float && (float) watchable.getRawValue() != 0.0F) {
      watchable.setValue(createFakeHealth());
    }
  }

  private float createFakeHealth() {
    return Math.max(1, (float) (Math.random() * 20.0F));
  }
     */

}
