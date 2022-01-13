<!-- Badges Config -->

[jitpack]: https://jitpack.io/#community.leaf/eventful "Get maven artifacts on JitPack"
[jitpack-version-badge]: https://jitpack.io/v/community.leaf/eventful.svg

[jitpack-downloads]: https://jitpack.io/#community.leaf/eventful "JitPack downloads this month"
[jitpack-downloads-badge]: https://img.shields.io/badge/dynamic/json?url=https://jitpack.io/api/downloads/community.leaf/eventful&label=Monthly+Downloads&query=$.month&color=ok

[license]: ./LICENSE "Project License: MPL-2.0"
[license-badge]: https://img.shields.io/badge/License-MPL--2.0-blue

[java-version]: # "Java Version: 11"
[java-version-badge]: https://img.shields.io/badge/Java-11-orange

[latest-javadoc]: https://javadoc.jitpack.io/community/leaf/eventful/events-parent/latest/javadoc/ "View latest javadoc"
[javadoc-badge]: https://img.shields.io/badge/dynamic/json?url=https://jitpack.io/api/builds/community.leaf/eventful/latestOk&label=Javadoc&query=$.version&color=%234D7A97

<!-- Header & Badges -->

# 🎫 Eventful

[![][jitpack-version-badge]][jitpack]
[![][jitpack-downloads-badge]][jitpack-downloads]
[![][license-badge]][license]
[![][java-version-badge]][java-version]
[![][javadoc-badge]][latest-javadoc]

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
are the same as the `tags` found on the **releases page** of this repository.

### Shading

If you intend to shade this library, please consider **relocating** the packages
to avoid potential conflicts with other projects. This library also utilizes
nullness annotations, which may be undesirable in a shaded uber-jar. They can
safely be excluded, and you are encouraged to do so.
