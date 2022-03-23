To build an executable you need:

- Maven (https://maven.apache.org/install.html)

- GraalVM (https://docs.gluonhq.com/#_graalvm_3114)

- Visual Studio Community (yeah, that's weird I know)

  - Workloads > Desktop development with C++

    - Windows 10 SDK (and/or Windows 11 SDK)
  
  - More complete guide: https://medium.com/graalvm/using-graalvm-and-native-image-on-windows-10-9954dc071311

- You need to have `%JAVA_HOME%` environment variable same as `%GRAALVM_HOME%`

- You need to have `%GRAALVM_HOME%\bin` inside `%PATH%` variable and no other java paths! Otherwise your `*.exe` file won't run!

How to build:

- Open `x64 Native Tools Command Prompt for VS` that was installed alongside with Visual Studio. (you can find it in Windows Start menu)

- Go to project folder (ex: `cd /d D:\Downloads\LeagueTeamComp`)

- Execute `mvn gluonfx:build` command from project folder

- Go to `<project folder>\target\gluonfx\x86_64-windows` and run `LeagueTeamComp.exe`