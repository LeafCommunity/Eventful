/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.Event;

/**
 * Calls Bukkit events.
 */
@FunctionalInterface
public interface EventDispatcher
{
    /**
     * Calls the provided event then
     * immediately returns it.
     *
     * @param event     the event to call
     * @param <E>       event type
     *
     * @return      the called event
     */
    <E extends Event> E call(E event);
}
