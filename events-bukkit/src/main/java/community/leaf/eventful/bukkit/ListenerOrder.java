/*
 * Copyright © 2021-2022, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.EventPriority;

import java.util.EnumMap;
import java.util.Map;

/**
 * Listener order (priority).
 */
public enum ListenerOrder
{
    /**
     * Called first, or as early as possible.
     */
    FIRST(EventPriority.LOWEST),
    /**
     * Called early, but not first.
     */
    EARLY(EventPriority.LOW),
    /**
     * The default listener priority.
     */
    NORMAL(EventPriority.NORMAL),
    /**
     * Called late, but not last.
     */
    LATE(EventPriority.HIGH),
    /**
     * Called last, or as late as possible.
     */
    LAST(EventPriority.HIGHEST),
    /**
     * Called at the very end to monitor event outcomes.
     */
    MONITOR(EventPriority.MONITOR);
    
    private static final Map<EventPriority, ListenerOrder> orderByPriority = new EnumMap<>(EventPriority.class);
    
    static
    {
        for (ListenerOrder order : values()) {orderByPriority.put(order.priority, order);}
    }
    
    private final EventPriority priority;
    
    ListenerOrder(EventPriority priority)
    {
        this.priority = priority;
    }
    
    /**
     * Gets the order as Bukkit event priority.
     *
     * @return the priority
     */
    public EventPriority priority()
    {
        return priority;
    }
    
    /**
     * Gets the order equivalent of the provided Bukkit event priority.
     *
     * @param priority  the bukkit priority
     *
     * @return the corresponding listener order
     */
    public static ListenerOrder ofPriority(EventPriority priority)
    {
        return orderByPriority.get(priority);
    }
}
