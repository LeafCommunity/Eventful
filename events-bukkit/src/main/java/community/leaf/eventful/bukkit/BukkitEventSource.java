/*
 * Copyright © 2021-2022, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.plugin.Plugin;

/**
 * A source of events.
 */
@FunctionalInterface
public interface BukkitEventSource
{
    /**
     * Gets the plugin used for registering events.
     *
     * @return  a plugin
     */
    Plugin plugin();
    
    /**
     * Gets utilities for registering and calling
     * events using this {@link #plugin()}.
     *
     * @return  event utilities
     */
    default Events events()
    {
        return this::plugin;
    }
}
