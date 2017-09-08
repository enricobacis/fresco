package dk.alexandra.fresco.framework.builder.binary;

import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.collections.sort.KeyedCompareAndSwap;
import dk.alexandra.fresco.lib.field.bool.ConditionalSelect;
import dk.alexandra.fresco.lib.field.bool.generic.AndFromPublicValue;
import dk.alexandra.fresco.lib.field.bool.generic.NandFromAndAndNot;
import dk.alexandra.fresco.lib.field.bool.generic.OrFromPublicValue;
import dk.alexandra.fresco.lib.field.bool.generic.OrFromXorAnd;
import dk.alexandra.fresco.lib.field.bool.generic.XnorFromXorAndNot;
import dk.alexandra.fresco.lib.math.bool.add.BitIncrementer;
import dk.alexandra.fresco.lib.math.bool.add.FullAdder;
import dk.alexandra.fresco.lib.math.bool.add.OneBitFullAdder;
import dk.alexandra.fresco.lib.math.bool.add.OneBitHalfAdder;
import dk.alexandra.fresco.lib.math.bool.log.Logarithm;
import dk.alexandra.fresco.lib.math.bool.mult.BinaryMultiplication;
import java.util.List;

public class DefaultBinaryBuilderAdvanced implements BinaryBuilderAdvanced {

  private final ProtocolBuilderBinary builder;

  protected DefaultBinaryBuilderAdvanced(ProtocolBuilderBinary builder) {
    super();
    this.builder = builder;
  }

  @Override
  public Computation<SBool> or(Computation<SBool> left, Computation<SBool> right) {
    return builder.seq(new OrFromXorAnd(left, right));
  }

  @Override
  public Computation<SBool> or(Computation<SBool> left, boolean right) {
    return builder.seq(new OrFromPublicValue(left, right));
  }

  @Override
  public Computation<SBool> xnor(Computation<SBool> left, Computation<SBool> right) {
    return builder.seq(new XnorFromXorAndNot(left, right));
  }

  @Override
  public Computation<SBool> xnor(Computation<SBool> left, boolean right) {
    if (right) {
      return left;
    } else {
      return builder.binary().not(left);
    }
  }

  @Override
  public Computation<SBool> nand(Computation<SBool> left, Computation<SBool> right) {
    return builder.seq(new NandFromAndAndNot(left, right));
  }

  @Override
  public Computation<SBool> nand(Computation<SBool> left, boolean right) {
    if (right) {
      return builder.binary().not(left);
    } else {
      return builder.binary().known(true);
    }
  }

  @Override
  public Computation<Pair<SBool, SBool>> oneBitFullAdder(Computation<SBool> left,
      Computation<SBool> right, Computation<SBool> carry) {
    return builder.seq(new OneBitFullAdder(left, right, carry));
  }

  @Override
  public Computation<List<Computation<SBool>>> fullAdder(List<Computation<SBool>> lefts,
      List<Computation<SBool>> rights, Computation<SBool> inCarry) {
    return builder.seq(new FullAdder(lefts, rights, inCarry));
  }

  @Override
  public Computation<List<Computation<SBool>>> bitIncrement(List<Computation<SBool>> base,
      Computation<SBool> increment) {
    return builder.seq(new BitIncrementer(base, increment));
  }

  @Override
  public Computation<SBool> and(Computation<SBool> left, boolean right) {
    return builder.seq(new AndFromPublicValue(left, right));
  }

  @Override
  public Computation<SBool> condSelect(Computation<SBool> condition, Computation<SBool> left,
      Computation<SBool> right) {
    return builder.seq(new ConditionalSelect(condition, left, right));
  }

  @Override
  public Computation<Pair<SBool, SBool>> oneBitHalfAdder(Computation<SBool> left,
      Computation<SBool> right) {
    return builder.seq(new OneBitHalfAdder(left, right));
  }



  @Override
  public Computation<List<Computation<SBool>>> binaryMult(List<Computation<SBool>> lefts,
      List<Computation<SBool>> rights) {
    return builder.seq(new BinaryMultiplication(lefts, rights));
  }

  @Override
  public Computation<List<Computation<SBool>>> logProtocol(List<Computation<SBool>> number) {
    return builder.seq(new Logarithm(number));
  }

  @Override
  public Computation<List<Pair<List<Computation<SBool>>, List<Computation<SBool>>>>> keyedCompareAndSwap(
      Pair<List<Computation<SBool>>, List<Computation<SBool>>> leftKeyAndValue,
      Pair<List<Computation<SBool>>, List<Computation<SBool>>> rightKeyAndValue) {
    return builder.seq(new KeyedCompareAndSwap(leftKeyAndValue, rightKeyAndValue));
  }

}