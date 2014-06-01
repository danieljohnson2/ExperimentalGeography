/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experimentalgeography;

import java.util.*;
import com.google.common.collect.*;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.event.world.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main plugin class for the plugin; it listens to events.
 *
 * @author DanJ
 */
public class ExperimentalGeography extends JavaPlugin implements Listener {

    private final Set<ChunkPosition> loadedChunks = Sets.newHashSet();
    private final Set<ChunkPosition> pendingChunks = Sets.newHashSet();

    private void markNewChunk(ChunkPosition pos) {
        loadedChunks.add(pos);
        pendingChunks.add(pos);
    }

    private List<ChunkPosition> nextChunksToPopulate() {
        List<ChunkPosition> ready = Lists.newArrayList();

        for (ChunkPosition candidate : pendingChunks) {
            if (loadedChunks.containsAll(candidate.neighbors())) {
                ready.add(candidate);
            }
        }

        pendingChunks.removeAll(ready);
        return ready;
    }

    /**
     * This method is called once per chunk, when the chunk is first
     * initialized. It can add extra blocks to the chunk.
     *
     * @param chunk The chunk to populate.
     */
    private void populateChunk(Chunk chunk) {
        int centerX = chunk.getX() * 16 + 8;
        int centerZ = chunk.getZ() * 16 + 8;

        int y = chunk.getWorld().getHighestBlockYAt(centerX, centerZ);

        Block block = chunk.getBlock(8, y, 8);

        block.setType(Material.BEDROCK);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) {
            markNewChunk(ChunkPosition.of(e.getChunk()));

            for (ChunkPosition next : nextChunksToPopulate()) {
                Chunk ch = next.getWorld().getChunkAt(next.x, next.z);
                populateChunk(ch);
            }
        }
    }
}
