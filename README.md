# AgriCraft ![branch] [![build]][build-link] [![coverage]][coverage-link] [![curse]][curse-link]

Agricultural farming extended.

## Contact [![contact]][contact-link]
I have a Discord channel where you can contact me for support/suggestions which you don't think fit on this issue tracker. Also if you want to discuss something with me, this is the place to be!

## Supported Versions
AgriCraft has releases for Minecraft versions 1.7.10 and onwards, status per Minecraft version is as follows:

| Version  | Notes
|----------|:----------
|![1.7.10] | No more work will be done on 1.7.10 by [InfinityRaider](https://github.com/InfinityRaider) or [RlonRyan](https://github.com/InfinityRaider/AgriCraft/commits/master?author=RlonRyan), if you want to maintain 1.7.10, contact us on Discord
|![1.8.9]  | The 1.8.9 branch was highly experimental and the rendering is one big hack. Don't expect anything.
|![1.9]    | Skipped 1.9, as it was an incredibly short-lived release.
|![1.10]   | Closed.
|![1.11]   | Skipped 1.11.
|![1.12]   | Closed.
|![1.16]   | Ported and supported
|![1.18]   | Ported and supported

## Bug Reports [![bug]][bug-link]

Please report any and all bugs you might encounter while playing with this mod (this only applies to versions of Minecraft this mod is currently being developed for). Suggestions are also welcome.
However before reporting a bug please update to the latest version of the mod to see if it still persists.
If you want to post bug reports for older versions, make sure to tell me what version you are using and the version of Forge you are using.
If you report a bug and I request more feedback, the label 'Awaiting reply' will be added, if I have had no response for 5 days after adding that label, the issue will be closed.

## Technical Documentation [![wiki]][wiki-link]

AgriCraft is documented in the official GitHub wiki. Note that
this kind of information is, most of the time, only needed by pack creators and not the average user.

## Compilation Notes

The latest versions of ForgeGradle require some tweaking to get builds working. The two most important things to note are as follows:

1. ForgeGradle does not like JDKs newer than JDK8. To develop install [openjdk8](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot), and update `gradle.propeties` to contain the following:
```
org.gradle.java.home = C:\\Program Files\\AdoptOpenJDK\\jdk-8.0.275.1-hotspot
```

2. The default JVM heap size is too small to handle decompilation of MineCraft. Builds fail without increasing the heap size that Gradle allocates for the build. Update `gradle.propeties` to contain the following:
```
# Sets default memory used for gradle commands. Can be overridden by user or command line properties.
# This is required to provide enough memory for the Minecraft decompilation process.
org.gradle.jvmargs=-Xmx3G
```

> Note: We do not provide a default `gradle.properties` file as it is discouraged by the Gradle documentation, given that the file may contain maven login information and the likes.

[branch]:https://img.shields.io/badge/branch-master-aaaaff.svg "GitHub Branch"

[build-link]:https://travis-ci.org/AgriCraft/AgriCraft
[build]:https://travis-ci.org/AgriCraft/AgriCraft.svg?branch=master "Travis-CI Build Status"

[coverage]:https://codecov.io/gh/AgriCraft/AgriCore/branch/master/graph/badge.svg?token=DIEBA4U1AH
[coverage-link]:https://codecov.io/gh/AgriCraft/AgriCore

[minecraft]:https://agricraft.github.io/versions/1.12/minecraft.svg "Minecraft Version"

[curse-link]:https://agricraft.github.io/curse
[curse]:http://cf.way2muchnoise.eu/full_agricraft_downloads.svg "CurseForge"

[contact-link]:https://agricraft.github.io/contact
[contact]:https://agricraft.github.io/images/contact.svg "InfinityRaider Contact"

[bug-link]:https://agricraft.github.io/issues
[bug]:https://agricraft.github.io/images/bug.svg "AgriCraft Issues"

[wiki-link]:https://agridocs.readthedocs.io/en/master/
[wiki]:https://agricraft.github.io/images/wiki.svg "AgriCraft GitHub Wiki"

[1.7.10]:https://agricraft.github.io/versions/1.7.10/status.svg
[1.8.9]:https://agricraft.github.io/versions/1.8.9/status.svg
[1.9]:https://agricraft.github.io/versions/1.9/status.svg
[1.10]:https://agricraft.github.io/versions/1.10/status.svg
[1.11]:https://agricraft.github.io/versions/1.11/status.svg
[1.12]:https://agricraft.github.io/versions/1.12/status.svg
[1.16]:https://agricraft.github.io/versions/1.16/status.svg
[1.18]:https://agricraft.github.io/versions/1.18/status.svg
