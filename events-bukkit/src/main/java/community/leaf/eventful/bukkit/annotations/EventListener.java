/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit.annotations;

import community.leaf.eventful.bukkit.ListenerOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that a method listens for events.
 * The listener's order (priority) is also defined here,
 * which is {@link ListenerOrder#NORMAL} by default.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener
{
    /**
     * Gets the listener's priority.
     *
     * @return  the listener order
     */
    ListenerOrder value() default ListenerOrder.NORMAL;
}
