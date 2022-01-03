/*
 * Copyright © 2021-2022, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit.annotations;

import community.leaf.eventful.bukkit.CancellationPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the policy for receiving cancelled events.
 * This annotation can be defined at the method, class, or package
 * level, and is checked in that order (from most specific to least).
 * Whichever annotation is found first will be the policy used by
 * event listener methods. If no policy is found, then event
 * listener methods will accept cancelled events by default
 * ({@link CancellationPolicy#ACCEPT}).
 * <p>
 * <b>Note: </b> this annotation is only meaningful in conjunction
 * with {@link EventListener}. If your method uses
 * {@link org.bukkit.event.EventHandler},
 * then this annotation's policy will have no effect since Bukkit's
 * standard annotation has its own {@code ignoreCancelled} option.
 *
 * @see EventListener
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
public @interface CancelledEvents
{
    /**
     * Gets the declared cancellation policy.
     *
     * @return  the cancellation policy
     */
    CancellationPolicy value();
}
