/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experimentalgeography;

import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Biome;

/**
 * This class holds onto data about a chunk that is captured when the chunk is loaded. This lets us 'remember' stuff that we'll
 * change later.
 *
 * @author DanJ
 */
public final class OriginalChunkInfo implements MapFileMap.Storable {

    public final ChunkPosition position;
    public final int highestBlockY;
    public final int spotBiome;
    public final int nodeY;

    public OriginalChunkInfo(Chunk chunk) {
        this.position = ChunkPosition.of(chunk);
        Location center = ExperimentalGeography.perturbNode(chunk.getWorld(), position, 0);
        int centerX = (int) center.getX();
        int centerZ = (int) center.getZ();

        highestBlockY = chunk.getWorld().getHighestBlockYAt(centerX, centerZ);
        spotBiome = chunk.getBlock(8, 8, 8).getBiome().ordinal();
        //from 7 to 67

        if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
            Random rnd = ExperimentalGeography.getChunkRandom(chunk.getWorld(), position);
            rnd.nextInt(64);
            if (chunk.getBlock(8, 8, 8).getBiome() == Biome.HELL) {
                nodeY = rnd.nextInt(60) + 50;
                //nether is crazy catwalks of disorienting steepness
            } else {
                nodeY = rnd.nextInt(5) + 46;
                //end is strangely flat
            }
        } else {

            if (chunk.getBlock(8, 8, 8).getBiome().name().contains("S")) {
                nodeY = (int) Math.max(5, ((highestBlockY / 1.4) - (spotBiome * 0.62)) + 6);
                //entrances in hills. Extreme are plain, others will show blocks
            } else {
                nodeY = (int) Math.max(5, ((highestBlockY / 1.4) - (spotBiome * 0.62)) - 6);
            }
            //divisor controls steepness, subtract moves tunnels down nearer bedrock
            //these can be biome dependent as it will linearly shift between them
            //we can have some biomes very flat, or some where it intersects surface a lot
        }
    }

    ////////////////////////////////////////////////////////////////
    // MapFileMap.Storage
    public OriginalChunkInfo(MapFileMap map) {
        this.position = map.getValue("position", ChunkPosition.class);
        this.highestBlockY = map.getInteger("highestBlockY");
        this.spotBiome = map.getInteger("spotBiome");
        this.nodeY = map.getInteger("nodeY");
    }

    @Override
    public Map<?, ?> toMap() {
        MapFileMap map = new MapFileMap();
        map.put("position", position);
        map.put("highestBlockY", highestBlockY);
        map.put("spotBiome", spotBiome);
        map.put("nodeY", nodeY);
        return map;
    }
}
