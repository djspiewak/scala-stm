/* scala-stm - (c) 2010, LAMP/EPFL */

package scala.concurrent.stm
package ccstm

import java.util.concurrent.atomic.AtomicLongFieldUpdater

private[ccstm] object CCSTMRefs {
  
  trait Factory extends impl.RefFactory {
    def newRef(v0: Boolean): Ref[Boolean] = new BooleanRef(v0)
    def newRef(v0: Byte): Ref[Byte] = new ByteRef(v0)
    def newRef(v0: Short): Ref[Short] = new ShortRef(v0)
    def newRef(v0: Char): Ref[Char] = new CharRef(v0)
    def newRef(v0: Int): Ref[Int] = new IntRef(v0)
    def newRef(v0: Float): Ref[Float] = new FloatRef(v0)
    def newRef(v0: Long): Ref[Long] = new LongRef(v0)
    def newRef(v0: Double): Ref[Double] = new DoubleRef(v0)
    def newRef(v0: Unit): Ref[Unit] = new GenericRef(v0)
    def newRef[T : ClassManifest](v0: T): Ref[T] = new GenericRef(v0)
    def newTArray[A: ClassManifest](length: Int): TArray[A] = new TArrayImpl[A](length)
    def newTArray[A: ClassManifest](data: Traversable[A]): TArray[A] = new TArrayImpl[A](data)
  }

  private abstract class BaseRef[A] extends Handle[A] with RefOps[A] with ViewOps[A] {
    def handle: Handle[A] = this
    def single: Ref.View[A] = this
    def ref: Ref[A] = this
    def base: AnyRef = this
    def metaOffset: Int = 0
    def offset: Int = 0
  }

  // Every call to AtomicLongFieldUpdater checks the receiver's type with
  // receiver.getClass().isInstanceOf(declaringClass).  This means that there
  // is a substantial performance benefit to putting the meta field in the
  // concrete leaf classes instead of the abstract base class. 

  private val booleanUpdater = (new BooleanRef(false)).newMetaUpdater
  private val byteUpdater = (new ByteRef(0 : Byte)).newMetaUpdater
  private val shortUpdater = (new ShortRef(0 : Short)).newMetaUpdater
  private val charUpdater = (new CharRef(0 : Char)).newMetaUpdater
  private val intUpdater = (new IntRef(0 : Int)).newMetaUpdater
  private val floatUpdater = (new FloatRef(0 : Float)).newMetaUpdater
  private val longUpdater = (new LongRef(0 : Long)).newMetaUpdater
  private val doubleUpdater = (new DoubleRef(0 : Double)).newMetaUpdater
  private val genericUpdater = (new GenericRef("")).newMetaUpdater

  private class BooleanRef(var data: Boolean) extends BaseRef[Boolean] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = booleanUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[BooleanRef], "meta")
  }

  private class ByteRef(var data: Byte) extends BaseRef[Byte] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = byteUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[ByteRef], "meta")
  }

  private class ShortRef(var data: Short) extends BaseRef[Short] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = shortUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[ShortRef], "meta")
  }

  private class CharRef(var data: Char) extends BaseRef[Char] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = charUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[CharRef], "meta")
  }

  private class IntRef(var data: Int) extends BaseRef[Int] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = intUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[IntRef], "meta")
  }

  private class FloatRef(var data: Float) extends BaseRef[Float] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = floatUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[FloatRef], "meta")
  }

  private class LongRef(var data: Long) extends BaseRef[Long] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = longUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[LongRef], "meta")
  }

  private class DoubleRef(var data: Double) extends BaseRef[Double] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = doubleUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[DoubleRef], "meta")
  }

  private class GenericRef[A](var data: A) extends BaseRef[A] {
    @volatile var meta = 0L
    def metaCAS(m0: Long, m1: Long) = genericUpdater.compareAndSet(this, m0, m1)
    def newMetaUpdater = AtomicLongFieldUpdater.newUpdater(classOf[GenericRef[_]], "meta")
  }
}
