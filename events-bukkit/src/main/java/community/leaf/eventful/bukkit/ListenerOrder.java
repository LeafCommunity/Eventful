/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.EventPriority;

import java.util.EnumMap;
import java.util.Map;

public enum ListenerOrder
{
    FIRST(EventPriority.LOWEST),
    EARLY(EventPriority.LOW),
    NORMAL(EventPriority.NORMAL),
    LATE(EventPriority.HIGH),
    LAST(EventPriority.HIGHEST),
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
    
    public EventPriority priority()
    {
        return priority;
    }
    
    public static ListenerOrder ofPriority(EventPriority priority)
    {
        return orderByPriority.get(priority);
    }
}
