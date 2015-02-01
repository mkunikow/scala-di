package com.originate.blog

/**
 * Created by michal on 2/1/15.
 */
object CakePatternDI extends App {

  case class User(id: Int, username: String, firstName: String, lastName: String, supervisorId: Int, email: String)

  trait UserRepository {
    def get(id: Int): User
    def find(username: String): User
  }

//  The Cake Pattern

//  In the cake pattern we represent the dependency as a component trait. We put the thing that is depended on into
//  the trait along with an abstract method to return an instance.

  trait UserRepositoryComponent {

    def userRepository: UserRepository

    trait UserRepository {
      def get(id: Int): User
      def find(username: String): User
    }
  }

// Then we declare the dependency in abstract classes using self-type declarations.

  trait Users {
    this: UserRepositoryComponent =>

    def getUser(id: Int): User = {
      userRepository.get(id)
    }

    def findUser(username: String): User = {
      userRepository.find(username)
    }
  }

  trait UserInfo extends Users {
    this: UserRepositoryComponent =>

    def userEmail(id: Int): String = {
      getUser(id).email
    }

    def userInfo(username: String): Map[String, String] = {
      val user = findUser(username)
      val boss = getUser(user.supervisorId)
      Map(
        "fullName" -> s"${user.firstName} ${user.lastName}",
        "email" -> s"${user.email}",
        "boss" -> s"${boss.firstName} ${boss.lastName}"
      )
    }
  }

  //  We extend the component trait to provide concrete implementations.
  trait UserRepositoryComponentImpl extends UserRepositoryComponent {

    def userRepository = new UserRepositoryImpl

    class UserRepositoryImpl extends UserRepository {

      def get(id: Int) = {
        User(id, "test1", "first", "last", 1, "test1@gmail.com")
      }

      def find(username: String) = {
        User(1, username, "first", "last", 1, "test1@gmail.com")
      }
    }
  }

  //  Finally we can create an instance of our class with the dependency by mixing in a concrete implementation.
  object UserInfoImpl extends
    UserInfo with
    UserRepositoryComponentImpl

//  For testing we can create a test instance of our class and mix in a mock implementation.

//  object TestUserInfo extends
//    UserInfo with
//    UserRepositoryComponent {
//
//    lazy val userRepository = mock[UserRepository]
//  }



}
