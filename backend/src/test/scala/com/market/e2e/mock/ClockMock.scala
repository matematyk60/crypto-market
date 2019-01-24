package com.market.e2e.mock
import java.time.{Clock, Instant, ZoneId}

import scala.concurrent.duration.FiniteDuration

class ClockMock(baseClock: Clock, zone: Option[ZoneId] = None) extends Clock {
  private var durationOffset: Option[FiniteDuration] = None

  override def getZone: ZoneId = zone.getOrElse(baseClock.getZone)
  override def withZone(zone: ZoneId): Clock =
    new ClockMock(baseClock, Some(zone))
  override def instant(): Instant =
    durationOffset
      .map(offset => baseClock.instant().plusMillis(offset.toMillis))
      .getOrElse(baseClock.instant())

  def addTime(time: FiniteDuration): Unit =
    durationOffset = durationOffset.map(_ + time).orElse(Some(time))
  def reset(): Unit = durationOffset = None
}
