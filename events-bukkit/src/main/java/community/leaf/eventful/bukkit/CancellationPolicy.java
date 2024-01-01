/*
 * Copyright © 2021-2024, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

/**
 * How to handle cancelled events.
 */
public enum CancellationPolicy
{
    /**
     * Rejects cancelled events, thus "ignoring" them.
     */
    REJECT,
    /**
     * Accepts cancelled events (the default policy).
     */
    ACCEPT;
    
    /**
     * Whether this policy "ignores" cancelled events.
     *
     * @return {@code true} if this policy is {@link #REJECT}.
     */
    public boolean ignoresCancelledEvents()
    {
        return this == REJECT;
    }
    
    /**
     * Gets the policy equivalent of the provided Bukkit {@code ignoreCancelled} state.
     *
     * @param ignoreCancelled   whether to ignore cancelled events or not
     *
     * @return the corresponding cancellation policy
     */
    public static CancellationPolicy ofIgnoreCancelled(boolean ignoreCancelled)
    {
        return (ignoreCancelled) ? REJECT : ACCEPT;
    }
}
