package net.wiringbits.modules

import com.google.inject.AbstractModule
import net.wiringbits.services.{BcryptPasswordHasher, PasswordHasher}

class ServicesModule extends AbstractModule {
  override def configure(): Unit = {
    // Default bcrypt cost. Tests override this binding with a lower cost to keep real hashing cheap.
    val _ = bind(classOf[PasswordHasher]).toInstance(new BcryptPasswordHasher(logRounds = 10))
  }
}
