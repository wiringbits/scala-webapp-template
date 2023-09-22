//> using scala "3.3.0"

//> using lib "com.olvind.typo::typo:0.3.1"
//> using lib "com.olvind.typo::typo-dsl-anorm:0.3.1"
//> using lib "com.typesafe.play::play-json::2.10.0-RC9"
//> using lib "com.lihaoyi::os-lib:0.9.1"

import typo.*
import typo.db.*
import os.*

object TypeOverrides {
  private val emailTypeImport = "net.wiringbits.common.models.Email"
  private val nameTypeImport = "net.wiringbits.common.models.Name"
  private val instantTypeImport = "net.wiringbits.common.models.InstantCustom"
  private val userTokenTypeImport = "net.wiringbits.common.models.enums.UserTokenType"
  private val backgroundJobTypeImport = "net.wiringbits.common.models.enums.BackgroundJobType"
  private val backgroundJobStatusImport = "net.wiringbits.common.models.enums.BackgroundJobStatus"
  private def idImport(name: String) = s"net.wiringbits.common.models.id.$name"

  def apply(): TypeOverride = usersOverrides
    .orElse(userTokensOverrides)
    .orElse(backgroundJobsOverrides)
    .orElse(userLogsOverrides)

  private val usersOverrides = {
    val relationName = RelationName(Some("public"), "users")

    TypeOverride.of {
      case (relationName, ColName("user_id")) => idImport("UserId")
      case (relationName, ColName("email")) => emailTypeImport
      case (relationName, ColName("name")) => nameTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("verified_on")) => instantTypeImport
    }
  }

  private val userTokensOverrides = {
    val relationName = RelationName(Some("public"), "user_tokens")

    TypeOverride.of {
      case (relationName, ColName("user_token_id")) => idImport("UserTokenId")
      case (relationName, ColName("token_type")) => userTokenTypeImport
      case (relationName, ColName("created_at")) => instantTypeImport
      case (relationName, ColName("expires_at")) => instantTypeImport
    }
  }

  private val backgroundJobsOverrides = {
    val relationName = RelationName(Some("public"), "background_jobs")

    TypeOverride.of {
      case (relationName, ColName("background_job_id")) => idImport("BackgroundJobId")
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
      case (relationName, ColName("user_log_id")) => idImport("UserLogId")
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

  // destination folder. All files in this dir will be overwritten!
  val targetDir = os.pwd / "typo" / "shared" / "src" / "main" / "scala"

  // where typo will look for sql files
  val sqlScriptsFolder = os.pwd / "server" / "src" / "main" / "resources" / "sql"

  val pkg = "net.wiringbits.typo_generated"

  def runScript = {
    // adapt to your instance and credentials
    val options = Options(
      pkg = pkg,
      dbLib = Some(DbLibName.Anorm),
      jsonLibs = List(JsonLibName.PlayJson),
      enableDsl = true,
      typeOverride = TypeOverrides()
    )

    generateFromDb(options, scriptsPaths = List(sqlScriptsFolder.toNIO))
      .overwriteFolder(folder = targetDir.toNIO)
  }

  def addCustomCodecs = {
    // TODO: get from server
    val typoCustomCodecs = os.read(os.pwd / "server" / "src" / "main" / "scala" / "net" / "wiringbits" / "repositories" / "TypoCodecs.scala")
    val firstOpenningBracket = typoCustomCodecs.indexOf("{")
    // + 1 because we are skipping the openning bracket
    val code = typoCustomCodecs.substring(firstOpenningBracket + 1)

    val typoPackageDir = targetDir / os.RelPath(pkg.replace('.', '/')) / "package.scala"
    // - 2 because we want to remove the '\n' at the end of the file and the last '}'
    os.truncate(typoPackageDir, os.size(typoPackageDir) - 2)
    os.write.append(typoPackageDir, code)
  }

  runScript
  addCustomCodecs
}
