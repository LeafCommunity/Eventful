# Eventful

[![](https://jitpack.io/v/community.leaf/eventful.svg)](https://jitpack.io/#community.leaf/eventful "Get maven artifacts on JitPack")
[![](https://img.shields.io/badge/License-MPL--2.0-blue)](./LICENSE "Project license: MPL-2.0")
[![](https://img.shields.io/badge/Java-11-orange)](#java-version "This project targets Java 11")
[![](https://img.shields.io/badge/View-Javadocs-%234D7A97)](https://javadoc.jitpack.io/community/leaf/eventful/events-parent/latest/javadoc/ "View javadocs")

Call and register Bukkit events.

## Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>community.leaf.eventful</groupId>
    <artifactId>events-bukkit</artifactId>
    <version><!--release--></version>
</dependency>
```

### Versions

Since we use JitPack to distribute this library, the versions available 
are the same as the "tags" found on the [releases page](https://github.com/LeafCommunity/Eventful/releases)
of this repository.

### Shading

When shading this library, please remember to **relocate** the packages
so other projects may also use it without conflict. This library also
utilizes nullness annotations, which may be undesirable in a shaded
uber-jar. They can safely be excluded, and you are encouraged to do so.
