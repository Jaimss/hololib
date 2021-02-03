[![Stable Release](https://img.shields.io/github/v/release/jaimss/hololib?color=brightgreen&label=stable)](https://github.com/Jaimss/hololib/releases) 
[![Latest Release](https://img.shields.io/github/v/release/jaimss/hololib?color=ffaa00&include_prereleases&label=snapshot)](https://github.com/Jaimss/hololib/releases)
# HoloLib
A Holograms Library for Spigot Development.

## Including Hololib
- Platform
  - Replace `{platform}` below with `core`. If you would like to use gson to save your holograms, you can also add the `gson` platform, which has a built in GsonBuilder which will nicely handle Holograms!
- Version
  - Replace `{version}` with the latest above.
- (Dont forget to use your favorite shading tool!)
### Gradle
First, you need to add the repository. The public repository pulls both snapshots and releases.
```groovy
repository {
    //...
    maven { url 'https://repo.jaims.dev/repository/maven-public/' }
}
```
And then you simply provide the ModuCore API dependency:
```groovy
dependencies {
    //...
    implementation "dev.jaims.hololib:{platform}:{version}"
}
```

### Maven
First, you need to add the repository. The public repository will pull both snapshots and releases.
```xml
<repository>
  <id>jaims-public</id>
  <url>https://repo.jaims.dev/repository/maven-public/</url>
</repository>
```
And then you need to add the ModuCore API dependency:
```xml
<dependency>
    <groupId>dev.jaims.hololib</groupId>
    <artifactId>{platform}</artifactId>
  <version>{version}</version>
</dependency>
```
