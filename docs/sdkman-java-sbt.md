# Java/Scala sdkman setup on ubuntu 21.04

## Instalation

sdkman can be installed by runing this command and following the instructions

```sh
curl -s "https://get.sdkman.io" | bash
```

then verify sdkman was installed by

```sh
sdk version
```

it should print something like

```sh
SDKMAN 5.13.0
```

---

## Installing Java

The latest java stable version can be installed by using

```sh
sdk install java
```

However you can choose any java version and available provider available to list them use

```sh
sdk list java
```

then install the desired JDK by

```
sdk install java <identifier>
```

any desired system wide java version can be set using

```sh
sdk default java <identifier>
```

---

## Installing SBT

sbt can be installed using

```sh
sdk install sbt
```

---

[Offical skman](https://sdkman.io/)
