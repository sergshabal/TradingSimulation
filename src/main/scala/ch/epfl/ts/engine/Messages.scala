package ch.epfl.ts.engine

import ch.epfl.ts.data.Currency.Currency


abstract class Message()
//case class Transaction() extends Message


abstract class Order(val uid: Long, val whatC: Currency, val quantity: Double, val withC: Currency)
case class RejectedOrder(o: Order) extends Order(o.uid, o.whatC, o.quantity, o.withC)
case class AcceptedOrder(o: Order) extends Order(o.uid, o.whatC, o.quantity, o.withC)



/* Get wallet state */
abstract class WalletState(val uid: Long) //extends Message

/* Getter */
case class GetWalletFunds(override val uid: Long) extends WalletState(uid)
case class GetWalletAllOrders(override val uid: Long) extends WalletState(uid)
case class GetWalletOpenOrder(override val uid: Long) extends WalletState(uid)
case class GetWalletClosedOrder(override val uid: Long) extends WalletState(uid)
case class GetWalletCanceledOrder(override val uid: Long) extends WalletState(uid)

/* Answers */
case class WalletFunds(override val uid: Long, f: Map[Currency, Double]) extends WalletState(uid)
case class WalletAllOrders(override val uid: Long, opO: List[Order], clO: List[Order], caO: List[Order]) extends WalletState(uid)
case class WalletOpenOrders(override val uid: Long, opO: List[Order]) extends WalletState(uid)
case class WalletClosedOrders(override val uid: Long, clO: List[Order]) extends WalletState(uid)
case class WalletCanceledOrders(override val uid: Long, caO: List[Order]) extends WalletState(uid)

/* Actions */
case class FundWallet(override val uid: Long, c: Currency, q: Double) extends WalletState(uid)

