package com.wavesplatform.datafeed.api


import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server.Route
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives.metrics
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsSettings
import fr.davit.akka.http.metrics.prometheus.PrometheusRegistry
import fr.davit.akka.http.metrics.prometheus.marshalling.PrometheusMarshallers._
import io.prometheus.client.CollectorRegistry

//https://blog.knoldus.com/expose-prometheus-metrics-for-an-akka-http-application/
//https://github.com/RustedBones/akka-http-metrics/tree/6dcca3e2696b4a95372a39be60e2efae533bd0fb
class MetricsController {

  import MetricsController._

  val route: Route = (get & path( "prometheus" / "metrics")) (metrics(registry))
}

object MetricsController {
  val routes: Route = new MetricsController().route

  private val settings: HttpMetricsSettings = HttpMetricsSettings
    .default
    .withIncludePathDimension(true)
    .withIncludeStatusDimension(true)
    .withDefineError(_.status.isFailure)

  private val collector: CollectorRegistry = CollectorRegistry.defaultRegistry

  val registry: PrometheusRegistry = PrometheusRegistry(settings,collector)
}