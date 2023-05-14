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

    /**
     * Play a sound to the player
     * @param player Player to play the sound for
     * @param sound Sound to play
     * @param pitch Custom pitch for the sound. Clamped internally to [0.5f, 2.0f]
     */
    public static void play(ServerPlayer player, SoundEvent sound, float pitch) {
        if (player == null) return;
        player.playNotifySound(sound, SoundSource.PLAYERS, 1f, pitch);
    }

    /**
     * Generic button click sound with custom pitch
     * @param player Player to play the sound for
     * @param pitch Custom pitch for the sound. Clamped internally to [0.5f, 2.0f]
     */
    public static void interact(ServerPlayer player, float pitch) {
        play(player, SoundEvents.NOTE_BLOCK_CHIME.value(), pitch);
    }

    /**
     * Generic button click sound
     * @param player Player to play the sound for
     */
    public static void interact(ServerPlayer player) {
        interact(player, 1f);
    }

    /**
     * Success sound, used when an action is successful (i.e. saved value, something executed)
     * @param player Player to play the sound for
     */
    public static void success(ServerPlayer player) {
        interact(player, 2f);
    }

    /**
     * Failure sound, used on error, cancellation or closing of a menu.
     * @param player Player to play the sound for
     */
    public static void failure(ServerPlayer player) {
        interact(player, 0.75f);
    }

    /**
     * Clear sound, used when e.g. a value is reset to default
     * @param player Player to play the sound for
     */
    public static void clear(ServerPlayer player) {
        play(player, SoundEvents.BUCKET_EMPTY, 1f);
    }
}
