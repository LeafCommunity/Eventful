/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

public enum CancellationPolicy
{
    IGNORE,
    ACCEPT;
    
    public boolean ignoresCancelledEvents()
    {
        return this == IGNORE;
    }
    
    public static CancellationPolicy ofIgnoreCancelled(boolean ignoreCancelled)
    {
        return (ignoreCancelled) ? IGNORE : ACCEPT;
    }
}
