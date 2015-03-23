package ch.epfl.ts.traders

import ch.epfl.ts.component.{StartSignal, Component}
import scala.util.Random
import ch.epfl.ts.data.{LimitBidOrder, LimitAskOrder, Order}
import ch.epfl.ts.data.Currency._
import ch.epfl.ts.component.StartSignal
import scala.concurrent.duration.DurationInt

import scala.language.postfixOps

case class SendMarketOrder()

/**
 * Trader that gives just random ask and bid orders alternatively
 * Created by sygi on 23.03.15.
 */
class MadTrader(uid: Long, intervalMillis: Int, orderVolume: Double) extends Component {
  import context._

  var orderId = 4567
  val initDelayMillis = 10000

  var alternate = 0
  val r = new Random

  override def receiver = {
    case StartSignal() => start
    case SendMarketOrder => {
      if (alternate % 2 == 0) {
        println("SimpleTrader: sending market bid order")
        send[Order](LimitAskOrder(orderId, uid, System.currentTimeMillis(), USD, USD, 50, 10 + r.nextInt(10)))
      } else {
        println("SimpleTrader: sending market ask order")
        send[Order](LimitBidOrder(orderId, uid, System.currentTimeMillis(), USD, USD, 50, 10 + r.nextInt(10)))
      }
      alternate = alternate + 1
      orderId = orderId + 1
    }
    case _ => println("SimpleTrader: received unknown")
  }

  def start = {
    system.scheduler.schedule(initDelayMillis milliseconds, intervalMillis milliseconds, self, SendMarketOrder)
  }

}