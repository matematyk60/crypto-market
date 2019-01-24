package com.market.shared

import java.time.{Instant, ZoneId, ZonedDateTime}

import scala.concurrent.duration.FiniteDuration

object TimeConverter {

  private val zoneId = ZoneId.systemDefault()

  def millisToZoned(date: Long): ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), zoneId)

  def secondsToZoned(date: Long): ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochSecond(date), zoneId)

  def zonedToMillis(date: ZonedDateTime): Long = date.toInstant.toEpochMilli

  def addDurationToTime(time: ZonedDateTime,
                        duration: FiniteDuration): ZonedDateTime =
    time.plusNanos(duration.toNanos)

}
