package net.wiringbits.api.utils

object Validator {

  // taked from https://stackoverflow.com/questions/13912597/validate-email-one-liner-in-scala/32445372
  // TODO: Fix me, this is not accurate
  def isValidEmail(email: String): Boolean =
    """(?=[^\s]+)(?=(\w+)@([\w.]+))""".r.findFirstIn(email).nonEmpty
}
