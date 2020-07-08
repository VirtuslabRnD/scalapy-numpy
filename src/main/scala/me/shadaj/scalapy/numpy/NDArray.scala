package me.shadaj.scalapy.numpy

import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.{PyValue, Reader, Writer}


/**
  NDArray should check the original type on element assignment
  https://github.com/VirtuslabRnD/scalapy-numpy/issues/3
 */
s
class NDArray[+T](val value: PyValue)(implicit reader: Reader[T]) extends py.Object with Seq[T] {
  private val origDynamic = this.as[py.Dynamic]

  def tolist: py.Any = origDynamic.tolist()

  def unary_-(): NDArray[T] = (-origDynamic).as[NDArray[T]]

  def +[A >: T](o: A)(implicit writer: Writer[A], reader: Reader[A]): NDArray[A] = (origDynamic + o).as[NDArray[A]]
  def +[A >: T](o: NDArray[A])(implicit writer: Writer[NDArray[A]], reader: Reader[A]): NDArray[A] = (origDynamic + o).as[NDArray[A]]

  def -[A >: T](o: A)(implicit writer: Writer[A], reader: Reader[A]): NDArray[A] = (origDynamic - o).as[NDArray[A]]
  def -[A >: T](o: NDArray[A])(implicit writer: Writer[NDArray[A]], reader: Reader[A]): NDArray[A] = (origDynamic - o).as[NDArray[A]]

  def *[A >: T](o: A)(implicit writer: Writer[A], reader: Reader[A]): NDArray[A] = (origDynamic * o).as[NDArray[A]]
  def *[A >: T](o: NDArray[A])(implicit writer: Writer[NDArray[A]], reader: Reader[A]): NDArray[A] = (origDynamic * o).as[NDArray[A]]

  def /[A >: T](o: A)(implicit writer: Writer[A], reader: Reader[A]): NDArray[A] = (origDynamic / o).as[NDArray[A]]
  def /[A >: T](o: NDArray[A])(implicit writer: Writer[NDArray[A]], reader: Reader[A]): NDArray[A] = (origDynamic / o).as[NDArray[A]]

  def T[A >: T](implicit writer: Writer[A], reader: Reader[A]): NDArray[A] = origDynamic.T.as[NDArray[A]]

  def astype[A](
     newType: NumPyType[A]
   )(implicit reader: Reader[A]): NDArray[A] = origDynamic.astype(newType).as[NDArray[A]]

  def reshape(shape: Seq[Int]): NDArray[T] = origDynamic.reshape(shape).as[NDArray[T]]

  def shape: Seq[Int] = origDynamic.shape.as[Seq[Int]]

  override def slice(from: Int, until: Int): NDArray[T] = {
    val slice = py.global.slice(from, until)
    origDynamic.__getitem__(slice).as[NDArray[T]]
  }

  override def length: Int = py.global.len(this).as[Int]

  override def apply(idx: Int): T = origDynamic.arrayAccess(idx).as[T]

  override def iterator: Iterator[T] = (0 until length).toIterator.map(apply)
}

object NDArray {
  implicit def reader[T](implicit reader: Reader[T]): Reader[NDArray[T]] = new Reader[NDArray[T]] {
    override def read(v: PyValue): NDArray[T] = new NDArray[T](v)(reader)
  }
}
