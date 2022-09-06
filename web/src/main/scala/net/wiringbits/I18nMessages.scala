package net.wiringbits

import net.wiringbits.common.models.Name
import net.wiringbits.core.I18nLang
import net.wiringbits.models.UserMenuOption

// TODO: conditionaly render messages when we support more than 1 language
class I18nMessages(_lang: I18nLang) {

  def appName = "Wiringbits Web App Template"
  def appNameCopyright = s"$appName ${java.time.ZonedDateTime.now.getYear}"
  def description =
    "While wiringbits is a company based in Culiacan, Mexico, there is no office, everyone works remotely. We strive for great quality on the software we built, and try to open source everything we can."
  def profile = "Profile"
  def home = "Home"
  def dashboard = "Dashboard"
  def user = "User"
  def about = "About"
  def signOut = "Sign out"
  def signUp: String = "Sign up"
  def signIn: String = "Sign in"
  def loading: String = "Loading"
  def welcome = "Welcome"

  def completeData = "Complete the necessary data"

  def contact = "Contact"
  def phone = "Phone"

  def name = "Name"
  def email = "Email"
  def password = "Password"
  def oldPassword = "Old password"
  def repeatPassword = "Repeat password"
  def createdAt = "Created at"

  def createAccount = "Create account"
  def login = "Login"
  def savePassword = "Save password"
  def resetPassword = "Reset password"
  def forgotYourPassword = "Forgot your password?"
  def recoverYourPassword = "Recover your password"
  def dontHaveAccountYet = "You don't have an account yet?"
  def alreadyHaveAccount = "Do you already have an account?"
  def enterNewPassword = "Enter your new password"
  def save = "Save"
  def recover = "Recover"
  def recoverIt = "Recover it"
  def reload = "Reload"
  def logs = "Logs"
  def resendEmail = "Re-send email"

  def aboutPage = "About page"
  def projectDetails = "Add details about the project"
  def dashboardPage = "Dashboard page"
  def homePage = "Home page"
  def landingPageContent = "The landing page content goes here"

  def verifyYourEmailAddress = "Verify your email address"
  def successfulEmailVerification = "Successful email verification"
  def failedEmailVerification = "Failed email verification"
  def invalidVerificationToken = "Invalid verification token"
  def goingToBeRedirected = "You're going to be redirected"
  def emailHasBeenSent = "An email has been sent to your email with a URL to verify your account."
  def emailNotReceived = "If you haven't received the email after a few minutes, please check your spam folder"
  def verifyingEmail = "We're verifing your email"
  def waitAMomentPlease = "Wait a moment, please"
  def completeTheCaptcha = "Complete the captcha"
  def checkoutTheRepo = "Checkout the repository!"

  def homePageDescription =
    "A reusable skeleton to build web applications in Scala/Scala.js, including user registration, login, admin portal, and deployments."
  def userProfile = "User profile"
  def userProfileDescription = "All the necessary code to create accounts, change passwords, update profile is there, "
  def tryIt = "Try it."
  def easilyExposeDataAdminPortal = "Easily expose data through the Admin portal"
  def easilyExposeDataAdminPortalDescriptionStart = "The portal is powered by "
  def easilyExposeDataAdminPortalDescriptionEnd = " Which allows exposing database tables with a simple config change."
  def reactAdmin = "react-admin"
  def thisSnippet = "This snippet:"
  def rendersAUserList = "Renders a user list:"
  def allowsViewingUpdatingASingleUser = "Allows viewing/updating a single user:"
  def adminUserTableSnippet = "Admin user table snippet"
  def adminUserList = "Admin user list"
  def adminUserView = "Admin user view"
  def swaggerIntegration = "Swagger integration"
  def swaggerIntegrationDescription =
    "The template already has the necessary boilerplate to expose the application's API through Swagger, "
  def consistentDataLoading = "Consistent data loading"
  def consistentDataLoadingDescription =
    "Asynchronous data loading is consistent when using our `AsyncComponent`, for example:"
  def dataIsBeingLoaded = "When the data is being loaded, a progress indicator is displayed:"
  def problemFetchingData = "When there is a problem fetching data, we get an opportunity to retry:"
  def simpleToFollowArchitecture = "A simple-to-follow architecture where tests are first class citizens"
  def simpleToFollowArchitectureDescription1 =
    "There is already an integration with GitHub Actions, and, there are already many integration tests to make sure that your APIs/Database work the way you expect them."
  def simpleToFollowArchitectureDescription2 =
    "There are many layers which are easy to follow, which means, boarding new developers takes little effort."

  def welcome(name: Name): String = {
    s"Welcome ${name.string}"
  }

  def userMenuOption(menuOption: UserMenuOption): String = {
    menuOption match {
      case UserMenuOption.EditSummary => "Summary"
      case UserMenuOption.EditPassword => "Change password"
    }
  }
}
