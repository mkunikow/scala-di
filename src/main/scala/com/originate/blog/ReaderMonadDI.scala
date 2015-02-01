package com.originate.blog

/**
 * Created by michal on 2/1/15.
 */
object ReaderMonadDI extends App {

//  Dependency Injection with the Reader Monad

case class User(id: Int, username: String, firstName: String, lastName: String, supervisorId: Int, email: String)

  trait UserRepository {
    def get(id: Int): User
    def find(username: String): User
  }

//  We define a “primitive” Reader for each operation defined in the UserRepository trait:
  trait Users {
    import scalaz.Reader

    def getUser(id: Int) = Reader((userRepository: UserRepository) =>
      userRepository.get(id)
    )

    def findUser(username: String) = Reader((userRepository: UserRepository) =>
      userRepository.find(username)
    )
  }

//  We can now define all the other operations (as Readers) in terms of the primitive Readers:
  object UserInfo extends Users {

    def userEmail(id: Int) = {
      getUser(id) map (_.email)
    }

    def userInfo(username: String) =
      for {
        user <- findUser(username)
        boss <- getUser(user.supervisorId)
      } yield Map(
        "fullName" -> s"${user.firstName} ${user.lastName}",
        "email" -> s"${user.email}",
        "boss" -> s"${boss.firstName} ${boss.lastName}"
      )
  }

//  Unlike in the implicits example, we don’t have UserRepository anywhere in the signatures of userEmail and userInfo.
//    We don’t have to mention UserRepository anywhere other than our primitives.
//    If we gain additional dependencies we can encapsulate them all in a single Config object and we only have to change the primitives.
//    For example, say we needed to add a mail service:

//  For example, say we needed to add a mail service:

    trait MailService

    trait Config {
      def userRepository: UserRepository

      def mailService: MailService
    }

    trait Users2 {

      import scalaz.Reader

      def getUser(id: Int) = Reader((config: Config) =>
        config.userRepository.get(id)
      )

      def findUser(username: String) = Reader((config: Config) =>
        config.userRepository.find(username)
      )
    }

//  Our UserInfo object doesn’t need to change.


//  Injecting the dependency
//  Assuming we have a concrete implementation of UserRepository as an object called UserRepositoryImpl,
//  we might define a controller like this:

//object Application extends Application(UserRepositoryImpl)
//
//  class Application(userRepository: UserRepository) extends Controller with Users {
//
//    def userEmail(id: Int) = Action {
//      Ok(run(UserInfo.userEmail(id)))
//    }
//
//    def userInfo(username: String) = Action {
//      Ok(run(UserInfo.userInfo(username)))
//    }
//
//    private def run[A](reader: Reader[UserRepository, A]): JsValue = {
//      Json.toJson(reader(userRepository))
//    }
//  }

//  But I can’t choose!

//  There’s no rule that says we have to use just one dependency injection strategy.
//  Why not use the Reader Monad throughout our application’s core, and the cake pattern at the outer edge?

//  object Application extends Application with UserRepositoryComponentImpl
//
//  trait Application extends Controller with Users {
//    this: UserRepositoryComponent =>
//
//    def userEmail(id: Int) = Action {
//      Ok(run(UserInfo.userEmail(id)))
//    }
//
//    def userInfo(username: String) = Action {
//      Ok(run(UserInfo.userInfo(username)))
//    }
//
//    private def run[A](reader: Reader[UserRepository, A]): JsValue = {
//      Json.toJson(reader(userRepository))
//    }
//  }

//  This way we get the benefit of the cake pattern but we only have to apply it to our controllers.
//  The Reader Monad lets us push the injection out to the edges of our application where it belongs.
}
