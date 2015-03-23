package ch.epfl.ts.example

import ch.epfl.ts.component.ComponentBuilder
import ch.epfl.ts.engine.{RevenueCompute, MarketSimulator, MarketRules}
import ch.epfl.ts.component.persist.{DummyPersistor, Persistor, TransactionPersistor, OrderPersistor}
import akka.actor.Props
import ch.epfl.ts.component.replay.{ReplayConfig, Replay}
import ch.epfl.ts.data._
import scala.reflect.ClassTag
import ch.epfl.ts.component.utils.{BackLoop, Printer}
import ch.epfl.ts.indicators.{SMA, SmaIndicator, OhlcIndicator}
import ch.epfl.ts.traders._
import ch.epfl.ts.data.Transaction
import ch.epfl.ts.data.DelOrder
import ch.epfl.ts.data.LimitBidOrder
import ch.epfl.ts.data.LimitAskOrder
import ch.epfl.ts.component.fetch.{TrueFxFetcher, PullFetchComponent}

/**
 * Simple class that will have a simple trader and will grab data from fx live and forward it to him.
 * Created by sygi on 23.03.15.
 */
object ProcessOnlineData {
  def main(args: Array[String]) {
    implicit val builder = new ComponentBuilder("ReplayOnlineData")

    // market params
    val marketId = 1L
    val rules = new MarketRules()

    // Persistor
    val dummyPersistor = new DummyPersistor()

    // Create components
    // market
    val market = builder.createRef(Props(classOf[MarketSimulator], marketId, rules), "market")
    // Printer
    val printer = builder.createRef(Props(classOf[Printer], "ReplayLoopPrinter"), "printer")
    // Fetcher
    val fetcher = builder.createRef(Props(classOf[PullFetchComponent[Quote]], new TrueFxFetcher(), implicitly[ClassTag[Transaction]]), "bitstampTransactionFetcher")
    // backloop
    val backloop = builder.createRef(Props(classOf[BackLoop], marketId, dummyPersistor), "backloop")

    // Trader
    val traderNames: Map[Long, String] = Map(132L -> "MadTrader")
    val simpleTrader = builder.createRef(Props(classOf[MadTrader], 132L, 10000, 50.0), "madTrader")

    // Display
    val display = builder.createRef(Props(classOf[RevenueCompute], 5000, traderNames), "display")

    //fetcher
    fetcher.addDestination(market, classOf[Quote])
    // market
    market.addDestination(backloop, classOf[Transaction])
    market.addDestination(backloop, classOf[LimitBidOrder])
    market.addDestination(backloop, classOf[LimitAskOrder])
    market.addDestination(backloop, classOf[DelOrder])
    market.addDestination(display, classOf[Transaction])

    // backLoop
    backloop.addDestination(simpleTrader, classOf[Transaction])

    // trader
    simpleTrader.addDestination(market, classOf[LimitAskOrder])
    simpleTrader.addDestination(market, classOf[LimitBidOrder])

    builder.start
  }
}
