//> using scala "3.3.0"

//> using lib "com.olvind.typo::typo:0.3.0"
//> using lib "com.olvind.typo::typo-dsl-anorm:0.3.0"
//> using lib "com.typesafe.play::play-json::2.10.0-RC9"

import typo.*
import typo.db.*

object TypeOverrides {
  def apply(): TypeOverride = usersOverrides
    .orElse(userTokensOverrides)
    .orElse(backgroundJobsOverrides)
    .orElse(userLogsOverrides)

  private val usersOverrides = TypeOverride.of {
    case (RelationName(Some("public"), "users"), ColName("user_id")) => "java.util.UUID"
    case (RelationName(Some("public"), "users"), ColName("email")) => "net.wiringbits.common.models.Email"
    case (RelationName(Some("public"), "users"), ColName("name")) => "net.wiringbits.common.models.Name"
    case (RelationName(Some("public"), "users"), ColName("created_at")) => "java.time.Instant"
    case (RelationName(Some("public"), "users"), ColName("verified_on")) => "java.time.Instant"
  }

  private val userTokensOverrides = TypeOverride.of {
    case (RelationName(Some("public"), "user_tokens"), ColName("user_token_id")) => "java.util.UUID"
    case (RelationName(Some("public"), "user_tokens"), ColName("created_at")) => "java.time.Instant"
    case (RelationName(Some("public"), "user_tokens"), ColName("expires_at")) => "java.time.Instant"
  }

  private val backgroundJobsOverrides = TypeOverride.of {
    case (RelationName(Some("public"), "background_jobs"), ColName("background_job_id")) => "java.util.UUID"
    case (RelationName(Some("public"), "background_jobs"), ColName("execute_at")) => "java.time.Instant"
    case (RelationName(Some("public"), "background_jobs"), ColName("created_at")) => "java.time.Instant"
    case (RelationName(Some("public"), "background_jobs"), ColName("updated_at")) => "java.time.Instant"
  }

  private val userLogsOverrides = TypeOverride.of {
    case (RelationName(Some("public"), "user_logs"), ColName("user_log_id")) => "java.util.UUID"
    case (RelationName(Some("public"), "user_logs"), ColName("created_at")) => "java.time.Instant"
  }
}

object GenerateTypoSQLDSL extends App {
  given conn: java.sql.Connection =
    // TODO: get connection from config
    java.sql.DriverManager.getConnection(
      "jdbc:postgresql://localhost:5432/wiringbits_db_v2?user=postgres&password=postgres"
    )

  def runScript = {
    // adapt to your instance and credentials
    val options = Options(
      pkg = "net.wiringbits.typo_generated",
      dbLib = Some(DbLibName.Anorm),
      jsonLibs = List(JsonLibName.PlayJson),
      enableDsl = true,
      typeOverride = TypeOverrides()
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
