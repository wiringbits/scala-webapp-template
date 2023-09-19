//> using scala "3.3.0"

//> using lib "com.olvind.typo::typo:0.3.0"
//> using lib "com.olvind.typo::typo-dsl-anorm:0.3.0"
//> using lib "com.typesafe.play::play-json::2.10.0-RC9"

import typo.*
import typo.db.*

object TypeOverrides {
  private val uuidTypeImport = "net.wiringbits.common.models.UUIDCustom"
  private val emailTypeImport = "net.wiringbits.common.models.Email"
  private val nameTypeImport = "net.wiringbits.common.models.Name"
  private val instantTypeImport = "net.wiringbits.common.models.InstantCustom"
  private val userTokenTypeImport = "net.wiringbits.common.models.enums.UserTokenType"
  private val backgroundJobTypeImport = "net.wiringbits.common.models.enums.BackgroundJobType"
  private val backgroundJobStatusImport = "net.wiringbits.common.models.enums.BackgroundJobStatus"

  def apply(): TypeOverride = usersOverrides
    .orElse(userTokensOverrides)
    .orElse(backgroundJobsOverrides)
    .orElse(userLogsOverrides)

  private val usersOverrides = {
    val relationName = RelationName(Some("public"), "users")

    TypeOverride.of {
      case (relationName, ColName("user_id")) => "net.wiringbits.common.models.id.UserId"
      case (relationName, ColName("email")) => emailTypeImport
      case (relationName, ColName("name")) => nameTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("verified_on")) => instantTypeImport
    }
  }

  private val userTokensOverrides = {
    val relationName = RelationName(Some("public"), "user_tokens")

    TypeOverride.of {
      case (relationName, ColName("user_token_id")) => uuidTypeImport
      case (relationName, ColName("token_type")) => userTokenTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("expires_at")) => instantTypeImport
    }
  }

  private val backgroundJobsOverrides = {
    val relationName = RelationName(Some("public"), "background_jobs")

    TypeOverride.of {
      case (relationName, ColName("background_job_id")) => uuidTypeImport
      case (relationName, ColName("type")) => backgroundJobTypeImport
      case (relationName, ColName("status")) => backgroundJobStatusImport
      case (relationName, ColName("execute_at")) => instantTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("updated_at")) => instantTypeImport
    }
  }

  private val userLogsOverrides = {
    val relationName = RelationName(Some("public"), "user_logs")

    TypeOverride.of {
      case (relationName, ColName("user_log_id")) => uuidTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
    }
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
