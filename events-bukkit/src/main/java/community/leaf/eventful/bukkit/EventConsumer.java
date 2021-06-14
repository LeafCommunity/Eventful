/*
 * Copyright © 2021, RezzedUp <http://github.com/LeafCommunity/Eventful/events-bukkit>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.eventful.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public interface EventConsumer<E extends Event> extends Consumer<E>, Listener {}
