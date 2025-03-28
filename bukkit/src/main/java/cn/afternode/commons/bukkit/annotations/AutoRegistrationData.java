package cn.afternode.commons.bukkit.annotations;

import java.util.List;
import java.util.Map;

/**
 * Generated auto-registration data
 * @param commands Command classes
 * @param listeners Listener classes
 * @param pluginCommands Plugin command executor/completer classes
 */
public record AutoRegistrationData(
        List<String> commands,
        List<String> listeners,
        Map<String, String> pluginCommands
) {
    /**
     * Generated resource location
     */
    public static final String LOCATION = "META-INF/afternode-commons/registration.json";
}
