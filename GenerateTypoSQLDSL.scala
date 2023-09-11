//> using scala "3.3.0"

//> using lib "com.olvind.typo::typo:0.3.0"
//> using lib "com.olvind.typo::typo-dsl-anorm:0.3.0"
//> using lib "com.typesafe.play::play-json::2.10.0-RC9"

import typo.*

object GenerateTypoSQLDSL extends App {
  given conn: java.sql.Connection =
    // TODO: get connection from config
    java.sql.DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/wiringbits_db?user=postgres&password=postgres"
    )

  def runScript = {
    // adapt to your instance and credentials
    val options = Options(
      pkg = "net.wiringbits.typo_generated",
      dbLib = Some(DbLibName.Anorm),
      jsonLibs = List(JsonLibName.PlayJson),
      enableDsl = true
    )

    // current folder, where you run the script from
    val location = java.nio.file.Path.of(sys.props("user.dir"))

    // destination folder. All files in this dir will be overwritten!
    val targetDir = location.resolve("typo/shared/src/main/scala")

    // where typo will look for sql files
    val scriptsFolder = location.resolve("server/src/main/resources/sql")

    generateFromDb(options, scriptsPaths = List(scriptsFolder))
      .overwriteFolder(folder = targetDir)
  }

  runScript
}
