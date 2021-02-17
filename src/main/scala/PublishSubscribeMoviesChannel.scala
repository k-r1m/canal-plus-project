package main.scala

import akka.actor.{ActorRef, ActorSystem}
import com.newmotion.akka.rabbitmq._
import com.typesafe.config.ConfigFactory

object PublishSubscribeMoviesChannel {
  implicit val system = ActorSystem()
  val factory = new ConnectionFactory()
  val connection = system.actorOf(ConnectionActor.props(factory), "canal-plus-movies")
  ConfigFactory.load()
  val exchange = "movies-comedy"

  def setupPublisher(channel: Channel, self: ActorRef) {
    channel.exchangeDeclare(exchange, "fanout")
    val queue = channel.queueDeclare().getQueue
    channel.queueBind(queue, exchange, "")
  }
  connection ! CreateChannel(ChannelActor.props(setupPublisher), Some("publish-movies"))

  def setupSubscriber(channel: Channel, self: ActorRef) {
    channel.exchangeDeclare(exchange, "fanout")
    val queue = channel.queueDeclare().getQueue
    channel.queueBind(queue, exchange, "")
    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        println("received: " + fromBytes(body))
      }
    }
    channel.basicConsume(queue, true, consumer)
  }
  connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscribe-movies"))

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")
  def toBytes(x: Long) = x.toString.getBytes("UTF-8")
}
