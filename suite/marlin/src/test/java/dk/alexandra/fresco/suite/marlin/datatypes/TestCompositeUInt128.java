package dk.alexandra.fresco.suite.marlin.datatypes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;
import org.junit.Test;

public class TestCompositeUInt128 {

  // TODO test toBitInteger
  private final BigInteger two = BigInteger.valueOf(2);
  private final BigInteger twoTo32 = BigInteger.ONE.shiftLeft(32);
  private final BigInteger twoTo64 = BigInteger.ONE.shiftLeft(64);
  private final BigInteger twoTo128 = BigInteger.ONE.shiftLeft(128);

  @Test
  public void testConstruct() {
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(BigInteger.ZERO).toBigInteger()
    );
    assertEquals(
        BigInteger.ONE,
        new CompositeUInt128(BigInteger.ONE).toBigInteger()
    );
    assertEquals(
        new BigInteger("42"),
        new CompositeUInt128(new BigInteger("42")).toBigInteger()
    );
    assertEquals(
        twoTo32,
        new CompositeUInt128(twoTo32).toBigInteger()
    );
    assertEquals(
        twoTo32.subtract(BigInteger.ONE),
        new CompositeUInt128(twoTo32.subtract(BigInteger.ONE)).toBigInteger()
    );
    assertEquals(
        twoTo32.add(BigInteger.ONE),
        new CompositeUInt128(twoTo32.add(BigInteger.ONE)).toBigInteger()
    );
    assertEquals(
        twoTo64.subtract(BigInteger.ONE),
        new CompositeUInt128(twoTo64.subtract(BigInteger.ONE)).toBigInteger()
    );
    assertEquals(
        twoTo64,
        new CompositeUInt128(twoTo64).toBigInteger()
    );
    assertEquals(
        twoTo64.add(BigInteger.ONE),
        new CompositeUInt128(twoTo64.add(BigInteger.ONE)).toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(BigInteger.ONE),
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)).toBigInteger()
    );
  }

  @Test
  public void testAdd() {
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(0).add(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        two,
        new CompositeUInt128(1).add(new CompositeUInt128(1)).toBigInteger()
    );
    assertEquals(
        twoTo32,
        new CompositeUInt128(twoTo32).add(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        twoTo32.add(BigInteger.ONE),
        new CompositeUInt128(twoTo32).add(new CompositeUInt128(1)).toBigInteger()
    );
    assertEquals(
        twoTo64,
        new CompositeUInt128(twoTo64).add(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        twoTo64.add(BigInteger.ONE),
        new CompositeUInt128(twoTo64).add(new CompositeUInt128(1)).toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(BigInteger.ONE),
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)).add(new CompositeUInt128(0))
            .toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE))
            .add(new CompositeUInt128(BigInteger.ONE)).toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(new BigInteger("10000000")).add(twoTo32.add(twoTo64)).mod(twoTo128),
        new CompositeUInt128(twoTo128.subtract(new BigInteger("10000000")))
            .add(new CompositeUInt128(twoTo32.add(twoTo64))).toBigInteger()
    );
    assertEquals(
        twoTo32.add(twoTo64).mod(twoTo128),
        new CompositeUInt128(twoTo32).add(new CompositeUInt128(twoTo64)).toBigInteger()
    );
  }

  @Test
  public void testMultiply() {
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(0).multiply(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(1).multiply(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(0).multiply(new CompositeUInt128(1)).toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(1024).multiply(new CompositeUInt128(0)).toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)).multiply(new CompositeUInt128(0))
            .toBigInteger()
    );
    assertEquals(
        BigInteger.ONE,
        new CompositeUInt128(1).multiply(new CompositeUInt128(1)).toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(BigInteger.ONE),
        new CompositeUInt128(new CompositeUInt128(1))
            .multiply(new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)))
            .toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(BigInteger.ONE),
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)).multiply(new CompositeUInt128(1))
            .toBigInteger()
    );
    // multiply no overflow
    assertEquals(
        new BigInteger("42").multiply(new BigInteger("7")),
        new CompositeUInt128(new BigInteger("42")).multiply(new CompositeUInt128(new BigInteger("7")))
            .toBigInteger()
    );
    // multiply with overflow
    assertEquals(
        new BigInteger("42").multiply(twoTo128.subtract(BigInteger.ONE)).mod(twoTo128),
        new CompositeUInt128(new BigInteger("42"))
            .multiply(new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)))
            .toBigInteger()
    );
    assertEquals(
        twoTo64.multiply(twoTo64.add(BigInteger.TEN)).mod(twoTo128),
        new CompositeUInt128(twoTo64)
            .multiply(new CompositeUInt128(twoTo64.add(BigInteger.TEN)))
            .toBigInteger()
    );
  }

  @Test
  public void testNegate() {
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(BigInteger.ZERO).negate().toBigInteger()
    );
    assertEquals(
        BigInteger.ONE,
        new CompositeUInt128(twoTo128.subtract(BigInteger.ONE)).negate().toBigInteger()
    );
    assertEquals(
        two,
        new CompositeUInt128(twoTo128.subtract(two)).negate().toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(two),
        new CompositeUInt128(two).negate().toBigInteger()
    );
  }

  @Test
  public void testSubtract() {
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(BigInteger.ZERO).subtract(new CompositeUInt128(BigInteger.ZERO))
            .toBigInteger()
    );
    assertEquals(
        BigInteger.ONE,
        new CompositeUInt128(BigInteger.ONE).subtract(new CompositeUInt128(BigInteger.ZERO))
            .toBigInteger()
    );
    assertEquals(
        BigInteger.ZERO,
        new CompositeUInt128(BigInteger.ONE).subtract(new CompositeUInt128(BigInteger.ONE))
            .toBigInteger()
    );
    assertEquals(
        twoTo128.subtract(BigInteger.ONE),
        new CompositeUInt128(BigInteger.ONE).subtract(new CompositeUInt128(two))
            .toBigInteger()
    );
  }

  @Test
  public void testToByteArrayWithPadding() {
    byte[] bytes = new byte[]{0x42};
    CompositeUInt<CompositeUInt128> uint = new CompositeUInt128(bytes);
    byte[] expected = new byte[16];
    expected[expected.length - 1] = 0x42;
    byte[] actual = uint.toByteArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testToByteArray() {
    byte[] bytes = new byte[16];
    new Random(1).nextBytes(bytes);
    CompositeUInt<CompositeUInt128> uint = new CompositeUInt128(bytes);
    byte[] actual = uint.toByteArray();
    assertArrayEquals(bytes, actual);
  }

  @Test
  public void testToByteArrayMore() {
    byte[] bytes = new byte[]{
        0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, // high
        0x02, 0x02, 0x02, 0x02, // mid
        0x03, 0x03, 0x02, 0x03  // low
    };
    CompositeUInt<CompositeUInt128> uint = new CompositeUInt128(bytes);
    byte[] actual = uint.toByteArray();
    assertArrayEquals(bytes, actual);
  }

}