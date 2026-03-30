package ac.boar.geyser.util;

import ac.boar.geyser.GeyserBoar;
import lombok.Getter;

/**
 * Utility class for ViaVersion integration.
 * Provides protocol version detection and version-aware compatibility checks.
 *
 * When ViaVersion is present on the server, Geyser can connect Bedrock players
 * to Java servers running different versions. This utility detects ViaVersion
 * and provides the server's effective Java protocol version.
 */
public class ViaVersionUtil {
    // JE 26.1 protocol version (775)
    public static final int PROTOCOL_JE_26_1 = 775;
    // JE 1.21.4 protocol version (769)
    public static final int PROTOCOL_JE_1_21_4 = 769;

    @Getter
    private static boolean viaVersionPresent = false;
    @Getter
    private static int serverProtocolVersion = -1;

    /**
     * Attempts to detect ViaVersion on the classpath.
     * Should be called during plugin initialization.
     */
    public static void init() {
        try {
            Class.forName("com.viaversion.viaversion.api.Via");
            viaVersionPresent = true;
            detectServerVersion();
            GeyserBoar.getLogger().info("ViaVersion detected! Server protocol version: " + serverProtocolVersion);
        } catch (ClassNotFoundException e) {
            viaVersionPresent = false;
            GeyserBoar.getLogger().info("ViaVersion not detected, running in standard mode.");
        }
    }

    private static void detectServerVersion() {
        try {
            Object api = Class.forName("com.viaversion.viaversion.api.Via")
                    .getMethod("getAPI")
                    .invoke(null);
            Object version = api.getClass()
                    .getMethod("getServerVersion")
                    .invoke(api);
            if (version instanceof Number) {
                serverProtocolVersion = ((Number) version).intValue();
            }
        } catch (Exception e) {
            GeyserBoar.getLogger().warning("Failed to detect ViaVersion server protocol version.");
            if (e.getMessage() != null) {
                GeyserBoar.getLogger().warning("Reason: " + e.getMessage());
            }
        }
    }

    /**
     * Returns whether the server is running JE 26.1 or higher.
     * When ViaVersion is not present, assumes the server runs the version
     * that Geyser natively supports (i.e. the latest), so returns true.
     */
    public static boolean isJE26_1OrHigher() {
        if (!viaVersionPresent || serverProtocolVersion < 0) {
            return true; // Assume latest when ViaVersion is not present
        }
        return serverProtocolVersion >= PROTOCOL_JE_26_1;
    }

    /**
     * Returns whether the server is running JE 1.21.4 or higher.
     * When ViaVersion is not present, assumes the server runs the version
     * that Geyser natively supports (i.e. the latest), so returns true.
     */
    public static boolean isJE1_21_4OrHigher() {
        if (!viaVersionPresent || serverProtocolVersion < 0) {
            return true;
        }
        return serverProtocolVersion >= PROTOCOL_JE_1_21_4;
    }
}
