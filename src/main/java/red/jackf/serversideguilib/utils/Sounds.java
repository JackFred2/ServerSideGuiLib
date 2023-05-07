package red.jackf.serversideguilib.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Collection of common sounds to play when using menus.
 */
public class Sounds {
    private Sounds() {}

    private static void play(ServerPlayer player, SoundEvent sound, float pitch) {
        if (player == null) return;
        player.playNotifySound(sound, SoundSource.PLAYERS, 1f, pitch);
    }

    public static void interact(ServerPlayer player, float pitch) {
        play(player, SoundEvents.NOTE_BLOCK_CHIME.value(), pitch);
    }

    /**
     * Generic button click sound
     */
    public static void interact(ServerPlayer player) {
        interact(player, 1f);
    }

    /**
     * Success sound, used when an action is successful (i.e. saved value, something executed)
     */
    public static void success(ServerPlayer player) {
        interact(player, 2f);
    }

    /**
     * Failure sound, used on error, cancellation or closing of a menu.
     */
    public static void failure(ServerPlayer player) {
        interact(player, 0.75f);
    }

    /**
     * Clear sound, used when e.g. a value is reset to default
     */
    public static void clear(ServerPlayer player) {
        play(player, SoundEvents.BUCKET_EMPTY, 1f);
    }
}
