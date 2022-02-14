package net.wiringbits.modules

import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.cluster.typed.Cluster
import com.google.inject.{AbstractModule, Provides}
import play.api.libs.concurrent.AkkaGuiceSupport

import javax.inject.{Inject, Provider, Singleton}

/** Custom module to wire akka Cluster to the play injector.
  *
  * This is adapted from the akka-cluster-sharing play module:
  *   - https://github.com/playframework/playframework/tree/master/cluster/play-cluster-sharding
  */
class AkkaClusterModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    // It is important to create the cluster when the application start, otherwise, the application
    // won't use the clustered mode.
    bind(classOf[Cluster])
      .toProvider(classOf[AkkaClusterModule.CustomProvider])
      .asEagerSingleton()

//    val x: Topic.Command[String] = ???
//    topic ! Topic.Subscribe(context)
//    topic ! Topic.Publish("Hello Subscribers!")

//    bindTypedActor(Topic[String]("my-topic"), "topic-actor")
    println("Binding sample actor")
    bindTypedActor(actor, "sample-actor")
  }

  @Provides
  @Singleton
  def topicActor(system: ActorSystem): TopicActor = {
    val ref = system.toTyped.systemActorOf(Topic[String]("my-topic"), "topic-actor")
    TopicActor(ref)
  }

  def actor: Behavior[String] = Behaviors.setup { context =>
    Behaviors.receiveMessage { msg =>
      context.log.info(s"Got msg: ${msg}")
      Behaviors.same
    }
  }
}

class EchoActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindTypedActor(actor, "sample-echo-actor")
  }

  private def actor: Behavior[String] = Behaviors.setup { context =>
    Behaviors.receiveMessage { msg =>
      context.log.info(s"Got message: ${msg}")
      Behaviors.same
    }
  }
}

case class TopicActor(ref: ActorRef[Topic.Command[String]])

object AkkaClusterModule {
  @Singleton
  class CustomProvider @Inject() (actorSystem: ActorSystem) extends Provider[Cluster] {
    val get: Cluster = Cluster(actorSystem.toTyped)
  }
}
