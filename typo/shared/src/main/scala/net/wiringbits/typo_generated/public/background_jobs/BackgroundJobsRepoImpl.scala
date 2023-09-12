/** File has been automatically generated by `typo`.
  *
  * IF YOU CHANGE THIS FILE YOUR CHANGES WILL BE OVERWRITTEN.
  */
package net
package wiringbits
package typo_generated
package public
package background_jobs

import anorm.NamedParameter
import anorm.ParameterMetaData
import anorm.ParameterValue
import anorm.RowParser
import anorm.SQL
import anorm.SimpleSql
import anorm.SqlStringInterpolation
import anorm.ToStatement
import java.sql.Connection
import net.wiringbits.typo_generated.customtypes.Defaulted
import net.wiringbits.typo_generated.customtypes.TypoJsonb
import net.wiringbits.typo_generated.customtypes.TypoOffsetDateTime
import typo.dsl.DeleteBuilder
import typo.dsl.SelectBuilder
import typo.dsl.SelectBuilderSql
import typo.dsl.UpdateBuilder

object BackgroundJobsRepoImpl extends BackgroundJobsRepo {
  override def delete(backgroundJobId: BackgroundJobsId)(implicit c: Connection): Boolean = {
    SQL"""delete from public.background_jobs where "background_job_id" = ${ParameterValue(
        backgroundJobId,
        null,
        BackgroundJobsId.toStatement
      )}""".executeUpdate() > 0
  }
  override def delete: DeleteBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    DeleteBuilder("public.background_jobs", BackgroundJobsFields)
  }
  override def insert(unsaved: BackgroundJobsRow)(implicit c: Connection): BackgroundJobsRow = {
    SQL"""insert into public.background_jobs("background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at", "created_at", "updated_at")
          values (${ParameterValue(
        unsaved.backgroundJobId,
        null,
        BackgroundJobsId.toStatement
      )}::uuid, ${ParameterValue(unsaved.`type`, null, ToStatement.stringToStatement)}, ${ParameterValue(
        unsaved.payload,
        null,
        TypoJsonb.toStatement
      )}::jsonb, ${ParameterValue(unsaved.status, null, ToStatement.stringToStatement)}, ${ParameterValue(
        unsaved.statusDetails,
        null,
        ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData)
      )}, ${ParameterValue(
        unsaved.errorCount,
        null,
        ToStatement.optionToStatement(ToStatement.intToStatement, ParameterMetaData.IntParameterMetaData)
      )}::int4, ${ParameterValue(
        unsaved.executeAt,
        null,
        TypoOffsetDateTime.toStatement
      )}::timestamptz, ${ParameterValue(
        unsaved.createdAt,
        null,
        TypoOffsetDateTime.toStatement
      )}::timestamptz, ${ParameterValue(unsaved.updatedAt, null, TypoOffsetDateTime.toStatement)}::timestamptz)
          returning "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
       """
      .executeInsert(BackgroundJobsRow.rowParser(1).single)

  }
  override def insert(unsaved: BackgroundJobsRowUnsaved)(implicit c: Connection): BackgroundJobsRow = {
    val namedParameters = List(
      Some(
        (
          NamedParameter(
            "background_job_id",
            ParameterValue(unsaved.backgroundJobId, null, BackgroundJobsId.toStatement)
          ),
          "::uuid"
        )
      ),
      Some((NamedParameter("type", ParameterValue(unsaved.`type`, null, ToStatement.stringToStatement)), "")),
      Some((NamedParameter("payload", ParameterValue(unsaved.payload, null, TypoJsonb.toStatement)), "::jsonb")),
      Some((NamedParameter("status", ParameterValue(unsaved.status, null, ToStatement.stringToStatement)), "")),
      Some(
        (
          NamedParameter(
            "status_details",
            ParameterValue(
              unsaved.statusDetails,
              null,
              ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData)
            )
          ),
          ""
        )
      ),
      unsaved.errorCount match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) =>
          Some(
            (
              NamedParameter(
                "error_count",
                ParameterValue(
                  value,
                  null,
                  ToStatement.optionToStatement(ToStatement.intToStatement, ParameterMetaData.IntParameterMetaData)
                )
              ),
              "::int4"
            )
          )
      },
      unsaved.executeAt match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) =>
          Some(
            (NamedParameter("execute_at", ParameterValue(value, null, TypoOffsetDateTime.toStatement)), "::timestamptz")
          )
      },
      unsaved.createdAt match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) =>
          Some(
            (NamedParameter("created_at", ParameterValue(value, null, TypoOffsetDateTime.toStatement)), "::timestamptz")
          )
      },
      unsaved.updatedAt match {
        case Defaulted.UseDefault => None
        case Defaulted.Provided(value) =>
          Some(
            (NamedParameter("updated_at", ParameterValue(value, null, TypoOffsetDateTime.toStatement)), "::timestamptz")
          )
      }
    ).flatten
    val quote = '"'.toString
    if (namedParameters.isEmpty) {
      SQL"""insert into public.background_jobs default values
            returning "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
         """
        .executeInsert(BackgroundJobsRow.rowParser(1).single)
    } else {
      val q = s"""insert into public.background_jobs(${namedParameters
          .map { case (x, _) => quote + x.name + quote }
          .mkString(", ")})
                  values (${namedParameters.map { case (np, cast) => s"{${np.name}}$cast" }.mkString(", ")})
                  returning "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
               """
      SimpleSql(SQL(q), namedParameters.map { case (np, _) => np.tupled }.toMap, RowParser.successful)
        .executeInsert(BackgroundJobsRow.rowParser(1).single)
    }

  }
  override def select: SelectBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    SelectBuilderSql("public.background_jobs", BackgroundJobsFields, BackgroundJobsRow.rowParser)
  }
  override def selectAll(implicit c: Connection): List[BackgroundJobsRow] = {
    SQL"""select "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
          from public.background_jobs
       """.as(BackgroundJobsRow.rowParser(1).*)
  }
  override def selectById(backgroundJobId: BackgroundJobsId)(implicit c: Connection): Option[BackgroundJobsRow] = {
    SQL"""select "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
          from public.background_jobs
          where "background_job_id" = ${ParameterValue(backgroundJobId, null, BackgroundJobsId.toStatement)}
       """.as(BackgroundJobsRow.rowParser(1).singleOpt)
  }
  override def selectByIds(
      backgroundJobIds: Array[BackgroundJobsId]
  )(implicit c: Connection): List[BackgroundJobsRow] = {
    SQL"""select "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
          from public.background_jobs
          where "background_job_id" = ANY(${backgroundJobIds})
       """.as(BackgroundJobsRow.rowParser(1).*)

  }
  override def update(row: BackgroundJobsRow)(implicit c: Connection): Boolean = {
    val backgroundJobId = row.backgroundJobId
    SQL"""update public.background_jobs
          set "type" = ${ParameterValue(row.`type`, null, ToStatement.stringToStatement)},
              "payload" = ${ParameterValue(row.payload, null, TypoJsonb.toStatement)}::jsonb,
              "status" = ${ParameterValue(row.status, null, ToStatement.stringToStatement)},
              "status_details" = ${ParameterValue(
        row.statusDetails,
        null,
        ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData)
      )},
              "error_count" = ${ParameterValue(
        row.errorCount,
        null,
        ToStatement.optionToStatement(ToStatement.intToStatement, ParameterMetaData.IntParameterMetaData)
      )}::int4,
              "execute_at" = ${ParameterValue(row.executeAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
              "created_at" = ${ParameterValue(row.createdAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
              "updated_at" = ${ParameterValue(row.updatedAt, null, TypoOffsetDateTime.toStatement)}::timestamptz
          where "background_job_id" = ${ParameterValue(backgroundJobId, null, BackgroundJobsId.toStatement)}
       """.executeUpdate() > 0
  }
  override def update: UpdateBuilder[BackgroundJobsFields, BackgroundJobsRow] = {
    UpdateBuilder("public.background_jobs", BackgroundJobsFields, BackgroundJobsRow.rowParser)
  }
  override def upsert(unsaved: BackgroundJobsRow)(implicit c: Connection): BackgroundJobsRow = {
    SQL"""insert into public.background_jobs("background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at", "created_at", "updated_at")
          values (
            ${ParameterValue(unsaved.backgroundJobId, null, BackgroundJobsId.toStatement)}::uuid,
            ${ParameterValue(unsaved.`type`, null, ToStatement.stringToStatement)},
            ${ParameterValue(unsaved.payload, null, TypoJsonb.toStatement)}::jsonb,
            ${ParameterValue(unsaved.status, null, ToStatement.stringToStatement)},
            ${ParameterValue(
        unsaved.statusDetails,
        null,
        ToStatement.optionToStatement(ToStatement.stringToStatement, ParameterMetaData.StringParameterMetaData)
      )},
            ${ParameterValue(
        unsaved.errorCount,
        null,
        ToStatement.optionToStatement(ToStatement.intToStatement, ParameterMetaData.IntParameterMetaData)
      )}::int4,
            ${ParameterValue(unsaved.executeAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
            ${ParameterValue(unsaved.createdAt, null, TypoOffsetDateTime.toStatement)}::timestamptz,
            ${ParameterValue(unsaved.updatedAt, null, TypoOffsetDateTime.toStatement)}::timestamptz
          )
          on conflict ("background_job_id")
          do update set
            "type" = EXCLUDED."type",
            "payload" = EXCLUDED."payload",
            "status" = EXCLUDED."status",
            "status_details" = EXCLUDED."status_details",
            "error_count" = EXCLUDED."error_count",
            "execute_at" = EXCLUDED."execute_at",
            "created_at" = EXCLUDED."created_at",
            "updated_at" = EXCLUDED."updated_at"
          returning "background_job_id", "type", "payload", "status", "status_details", "error_count", "execute_at"::text, "created_at"::text, "updated_at"::text
       """
      .executeInsert(BackgroundJobsRow.rowParser(1).single)

  }
}
