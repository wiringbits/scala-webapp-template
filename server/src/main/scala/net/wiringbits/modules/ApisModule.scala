package net.wiringbits.modules

import com.google.inject.{AbstractModule, Provider}
import net.wiringbits.apis.{EmailApi, EmailApiAWSImpl}
import net.wiringbits.config.EmailConfig
import org.slf4j.LoggerFactory

import javax.inject.Inject

class ApisModule extends AbstractModule {
  override def configure(): Unit = {
    val _ = bind(classOf[EmailApi])
      .toProvider(classOf[ApisModule.EmailApiProvider])
      .asEagerSingleton()
  }
}

object ApisModule {

  class EmailApiProvider @Inject() (config: EmailConfig, logImpl: EmailApi.LogImpl, awsImpl: EmailApiAWSImpl)
      extends Provider[EmailApi] {

    private val logger = LoggerFactory.getLogger(this.getClass)

    override def get(): EmailApi = {
      if (config.provider equalsIgnoreCase "aws") {
        logger.info("Mail provider set to AWS")
        awsImpl
      } else {
        logger.info("Mail provider set to none, emails will be printed as logs")
        logImpl
      }
    }
  }
}
