package dev.satherov.ftbxaerocompat;

import lombok.experimental.UtilityClass;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import dev.ftb.mods.ftbchunks.client.map.MapChunk;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class Claims {

    public static final int REGION_SIZE = 32;

    public record ChunkColors(int fill, int border, int top, int right, int bottom, int left) { }

    public static boolean hasRegion(int regionX, int regionZ) {
        return MapDimension.getCurrent().map(current -> current.getRegions().get(XZ.of(regionX, regionZ)) != null).orElse(false);
    }

    public static boolean isClaimed(int chunkX, int chunkZ) {
        MapChunk chunk = Claims.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.getTeam().isPresent();
    }

    public static boolean isForceLoaded(int chunkX, int chunkZ) {
        MapChunk chunk = Claims.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.getTeam().isPresent() && chunk.getForceLoadedDate().isPresent();
    }

    public static boolean isStripe(int pixelX, int pixelZ) {
        return ((pixelX + pixelZ) & 3) < 2;
    }

    public static @Nullable ChunkColors colors(int chunkX, int chunkZ, int fillOpacity, int borderOpacity) {
        MapChunk center = Claims.getChunk(chunkX, chunkZ);
        if (center == null) {
            return null;
        }

        Optional<Team> team = center.getTeam();
        if (team.isEmpty()) {
            return null;
        }

        int rgb = team.get().getProperties().get(TeamProperties.COLOR).rgb() & 0xFFFFFF;
        int packed = ((rgb & 0xFF) << 24) | ((rgb >> 8 & 0xFF) << 16) | ((rgb >> 16 & 0xFF) << 8);
        int fill = packed | (255 * fillOpacity / 100);
        int border = packed | (255 * borderOpacity / 100);
        return new ChunkColors(
                fill,
                border,
                Claims.isSameOwner(center, Claims.getChunk(chunkX, chunkZ - 1)) ? fill : border,
                Claims.isSameOwner(center, Claims.getChunk(chunkX + 1, chunkZ)) ? fill : border,
                Claims.isSameOwner(center, Claims.getChunk(chunkX, chunkZ + 1)) ? fill : border,
                Claims.isSameOwner(center, Claims.getChunk(chunkX - 1, chunkZ)) ? fill : border
        );
    }

    public static int regionHash(int regionX, int regionZ, int fillOpacity, int borderOpacity) {
        if (MapDimension.getCurrent().isEmpty()) {
            return 0;
        }

        int chunkX = regionX * Claims.REGION_SIZE;
        int chunkZ = regionZ * Claims.REGION_SIZE;
        long hash = 0L;
        hash = hash * 37L + borderOpacity;
        hash = hash * 37L + fillOpacity;

        for (int i = 0; i < Claims.REGION_SIZE; i++) {
            hash = Claims.hashChunk(hash, Claims.getChunk(chunkX + i, chunkZ - 1));
            hash = Claims.hashChunk(hash, Claims.getChunk(chunkX + Claims.REGION_SIZE, chunkZ + i));
            hash = Claims.hashChunk(hash, Claims.getChunk(chunkX + i, chunkZ + Claims.REGION_SIZE));
            hash = Claims.hashChunk(hash, Claims.getChunk(chunkX - 1, chunkZ + i));

            for (int j = 0; j < Claims.REGION_SIZE; j++) {
                hash = Claims.hashChunk(hash, Claims.getChunk(chunkX + i, chunkZ + j));
            }
        }

        return (int) (hash >> 32) * 37 + (int) (hash & 0xFFFFFFFFL);
    }

    public static Component tooltip(int chunkX, int chunkZ) {
        MapChunk chunk = Claims.getChunk(chunkX, chunkZ);
        if (chunk == null) {
            return Component.empty();
        }

        Optional<Team> team = chunk.getTeam();
        if (team.isEmpty()) {
            return Component.empty();
        }

        MutableComponent tooltip = Component.empty().withStyle(team.get().getProperty(TeamProperties.COLOR).toStyle()).append(team.get().getColoredName());
        if (chunk.getForceLoadedDate().isPresent()) {
            tooltip.append(Component.literal(" □ ").append(Component.translatable("ftbchunks.gui.force_loaded")));
        }

        return tooltip;
    }

    private static @Nullable MapChunk getChunk(int chunkX, int chunkZ) {
        Optional<MapDimension> current = MapDimension.getCurrent();
        if (current.isEmpty()) {
            return null;
        }

        return current.get().getRegion(XZ.regionFromChunk(chunkX, chunkZ)).getChunkForAbsoluteChunkPos(XZ.of(chunkX, chunkZ));
    }

    private static boolean isSameOwner(MapChunk first, @Nullable MapChunk second) {
        if (second == null) {
            return false;
        }

        Optional<Team> firstTeam = first.getTeam();
        Optional<Team> secondTeam = second.getTeam();
        return firstTeam.isPresent() && secondTeam.isPresent() && firstTeam.get().getId().equals(secondTeam.get().getId());
    }

    private static long hashChunk(long hash, @Nullable MapChunk chunk) {
        if (chunk == null) {
            return hash * 37L;
        }

        Optional<Team> team = chunk.getTeam();
        if (team.isEmpty()) {
            return hash * 37L * 37L;
        }

        UUID teamId = team.get().getId();
        long result = hash;
        result = (result + teamId.getLeastSignificantBits()) * 37L;
        result = (result + teamId.getMostSignificantBits()) * 37L;
        result = (result + team.get().getProperties().get(TeamProperties.COLOR).rgb()) * 37L;
        result = (result + (chunk.getForceLoadedDate().isPresent() ? 1L : 0L)) * 37L;
        return result * 37L;
    }
}
