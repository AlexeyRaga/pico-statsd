package org.pico.statsd.syntax

import com.timgroup.statsd.StatsDClient
import org.pico.event.{Bus, SinkSource, Source}
import org.pico.statsd.{CounterMetric, GaugeMetric, HistogramMetric}
import org.pico.disposal.std.autoCloseable._

package object event {
  implicit class SourceOps_Common_Rht98nT[A, S <: Source[A]](val self: S) extends AnyVal {
    @inline
    def stats(f: (StatsDClient, A) => Unit)
             (implicit c: StatsDClient): Source[A] = {
      self += self.subscribe(a => f(c, a))
      self
    }
  }
  
  implicit class SinkSourceOps_Counter_Rht98nT[A, B](val self: SinkSource[A, B]) extends AnyVal {
    @inline
    def counting(aspect: String, tags: String*)
                (implicit c: StatsDClient): SinkSource[A, B] = {
      self += self.subscribe(a => c.count(aspect, 1, tags: _*))
      self
    }
    
    @inline
    def counting(aspect: String, delta: Long, tags: String*)
                (implicit c: StatsDClient): SinkSource[A, B] = {
      self += self.subscribe(a => c.count(aspect, delta, tags: _*))
      self
    }
    
    @inline
    def viaCounter(aspect: String, tags: String*)
                  (implicit c: StatsDClient, m: CounterMetric[B]): SinkSource[A, B] = {
      self += self.subscribe(x => m.send(c, aspect, x, tags.toList))
      self
    }
  }
  
  
  implicit class SourceOps_Counter_Rht98nT[A](val self: Source[A]) extends AnyVal {
    @inline
    def counting(aspect: String, tags: String*)
                (implicit c: StatsDClient): Source[A] = {
      self += self.subscribe(a => c.count(aspect, 1, tags: _*))
      self
    }

    @inline
    def counting(aspect: String, delta: Long, tags: String*)
                (implicit c: StatsDClient): Source[A] = {
      self += self.subscribe(a => c.count(aspect, delta, tags: _*))
      self
    }

    @inline
    def viaCounter(aspect: String, tags: String*)
                  (implicit c: StatsDClient, m: CounterMetric[A]): Source[A] = {
      self += self.subscribe(a => m.send(c, aspect, a, tags.toList))
      self
    }
  }
  
  implicit class SinkSourceOps_Gauge_Rht98nT[A, B](val self: SinkSource[A, B]) extends AnyVal {
    
    @inline
    def viaGauge(aspect: String, tags: String*)
                (implicit c: StatsDClient, m: GaugeMetric[B]): SinkSource[A, B] = {
      self += self.subscribe { x => m.send(c, aspect, x, tags.toList) }
      self
    }
  }
  
  implicit class SourceOps_Gauge_Rht98nT[A](val self: Source[A]) extends AnyVal {
    
    @inline
    def viaGauge(aspect: String, tags: String*)
                    (implicit c: StatsDClient, m: GaugeMetric[A]): Source[A] = {
      self += self.subscribe { x => m.send(c, aspect, x, tags.toList) }
      self
    }
  }
  
  implicit class SinkSourceOps_Histogram_Rht98nT[A, B](val self: SinkSource[A, B]) extends AnyVal {

    @inline
    def viaHistogram(aspect: String, tags: String*)
                    (implicit c: StatsDClient, m: HistogramMetric[B]): SinkSource[A, B] = {
      self += self.subscribe { x => m.send(c, aspect, x, tags.toList) }
      self
    }
  }
  
  implicit class SourceOps_Histogram_Rht98nT[A](val self: Source[A]) extends AnyVal {

    @inline
    def viaHistogram(aspect: String, tags: String*)
                    (implicit c: StatsDClient, m: HistogramMetric[A]): Source[A] = {
      self += self.subscribe { x => m.send(c, aspect, x, tags.toList) }
      self
    }
  }
}
