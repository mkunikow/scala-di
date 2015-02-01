package com.originate.blog

/**
 * Created by michal on 2/1/15.
 */
object ImplicitsDI extends App {

//  Implicits

//  The cake pattern works really well but tends to require a lot of boilerplate.
//  Another approach is to add implicit parameters to the methods that require the dependency.

  case class User(id: Int, username: String, firstName: String, lastName: String, supervisorId: Int, email: String)

  trait UserRepository {
    def get(id: Int): User
    def find(username: String): User
  }

  trait Users {

    def getUser(id: Int)(implicit userRepository: UserRepository) = {
      userRepository.get(id)
    }

    def findUser(username: String)(implicit userRepository: UserRepository) = {
      userRepository.find(username)
    }
  }

  object UserInfo extends Users {

    def userEmail(id: Int)(implicit userRepository: UserRepository) = {
      getUser(id).email
    }

    def userInfo(username: String)(implicit userRepository: UserRepository) = {
      val user = findUser(username)
      val boss = getUser(user.supervisorId)
      Map(
        "fullName" -> s"${user.firstName} ${user.lastName}",
        "email" -> s"${user.email}",
        "boss" -> s"${boss.firstName} ${boss.lastName}"
      )
    }
  }

//  This is a lot less code and has the advantage of allowing us to substitute a different UserRepository at any point
//  either by defining our own implicit value or by passing it as an explicit argument.

//  A downside of this approach is that it clutters up the method signatures.
//  Every method that depends on a UserRepository (userEmail and userInfo in this example) has to declare that implicit parameter.
}
