package com.projecki.economy;

/**
 * An exception that wraps any exception that may cause the plugin to fail to load.
 */
public class PluginStartException extends Exception {

    public PluginStartException(String message) {
        super(message);
    }

    public PluginStartException(String message, Exception causedBy) {
        super(message, causedBy);
    }

}
