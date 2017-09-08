/*
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 */
package dk.alexandra.fresco.lib.math.integer.min;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.builder.numeric.NumericBuilder;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.network.ResourcePoolCreator;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.core.Is;
import org.junit.Assert;


/**
 * Generic test cases for basic finite field operations.
 *
 * Can be reused by a test case for any protocol suite that implements the basic field protocol
 * factory.
 *
 * TODO: Generic tests should not reside in the runtime package. Rather in mpc.lib or something.
 */
public class MinTests {

  public static class TestMinimumProtocol<ResourcePoolT extends ResourcePool> extends
      TestThreadFactory {

    @Override
    public TestThread next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<Integer> data1 = Arrays
            .asList(200, 144, 99, 211, 930, 543, 520, 532, 497, 450, 432);

        @Override
        public void test() throws Exception {
          Application<Pair<BigInteger, List<BigInteger>>, ProtocolBuilderNumeric> app =
              builder -> {
                NumericBuilder sIntFactory = builder.numeric();

                List<Computation<SInt>> inputs = data1.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                Computation<Pair<List<Computation<SInt>>, SInt>> min = builder.seq(
                    new Minimum(inputs));

                return builder.par((par) -> {
                  NumericBuilder open = par.numeric();
                  Computation<BigInteger> resultMin = open.open(min.out().getSecond());
                  List<Computation<SInt>> outputArray = min.out().getFirst();
                  List<Computation<BigInteger>> openOutputArray = new ArrayList<>(
                      outputArray.size());
                  for (Computation<SInt> computation : outputArray) {
                    openOutputArray.add(open.open(computation));

                  }
                  return () -> new Pair<>(
                      resultMin.out(),
                      openOutputArray.stream().map(Computation::out).collect(Collectors.toList()));
                });
              };
          Pair<BigInteger, List<BigInteger>> result = secureComputationEngine
              .runApplication(app, ResourcePoolCreator.createResourcePool(conf.sceConf));
          Assert.assertThat(result.getSecond().get(2), Is.is(BigInteger.ONE));
          Assert.assertThat(result.getFirst(), Is.is(new BigInteger("99")));
        }
      };
    }
  }


  public static class TestMinInfFraction<ResourcePoolT extends ResourcePool> extends
      TestThreadFactory {

    @Override
    public TestThread next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<Integer> data1 = Arrays
            .asList(20, 14, 9, 21, 93, 54, 52, 53, 49, 45, 43);
        private final List<Integer> data2 = Arrays
            .asList(140, 120, 90, 191, 123, 4, 122, 153, 149, 145, 143);
        private final List<Integer> data3 = Arrays.asList(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0);

        @Override
        public void test() throws Exception {
          Application<MinInfResult, ProtocolBuilderNumeric> app =
              builder -> {
                NumericBuilder sIntFactory = builder.numeric();

                List<Computation<SInt>> inputN = data1.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                List<Computation<SInt>> inputD = data2.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                List<Computation<SInt>> inputInfs = data3.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                Computation<MinInfFrac.MinInfOutput> min = builder.seq(
                    new MinInfFrac(inputN, inputD, inputInfs));

                return builder.par((par) -> {
                  NumericBuilder open = par.numeric();
                  Computation<BigInteger> resultMinN = open.open(min.out().nm);
                  Computation<BigInteger> resultMinD = open.open(min.out().dm);
                  Computation<BigInteger> resultMinInfs = open.open(min.out().infm);
                  List<Computation<SInt>> outputArray = min.out().cs;
                  List<Computation<BigInteger>> openOutputArray = new ArrayList<>(
                      outputArray.size());
                  for (Computation<SInt> computation : outputArray) {
                    openOutputArray.add(open.open(computation));
                  }
                  return () -> new MinInfResult(
                      resultMinN.out(), resultMinD.out(), resultMinInfs.out(),
                      openOutputArray.stream().map(Computation::out).collect(Collectors.toList()));
                });
              };
          MinInfResult minInfResult = secureComputationEngine
              .runApplication(app, ResourcePoolCreator.createResourcePool(conf.sceConf));
          Assert.assertThat(minInfResult.resultList.get(2), Is.is(BigInteger.ONE));
          Assert.assertThat(minInfResult.minD, Is.is(new BigInteger("90")));
          Assert.assertThat(minInfResult.minN, Is.is(new BigInteger("9")));
          Assert.assertThat(minInfResult.minInfs, Is.is(new BigInteger("0")));
        }
      };
    }
  }

  private static class MinInfResult {

    private final BigInteger minN;
    private final BigInteger minD;
    private final BigInteger minInfs;
    private final List<BigInteger> resultList;

    private MinInfResult(BigInteger minN, BigInteger minD, BigInteger minInfs,
        List<BigInteger> resultList) {
      this.minN = minN;
      this.minD = minD;
      this.minInfs = minInfs;
      this.resultList = resultList;
    }
  }

  public static class TestMinInfFractionTrivial<ResourcePoolT extends ResourcePool> extends
      TestThreadFactory {

    @Override
    public TestThread next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<Integer> data1 = Collections.singletonList(20);
        private final List<Integer> data2 = Collections.singletonList(140);
        private final List<Integer> data3 = Collections.singletonList(0);

        @Override
        public void test() throws Exception {
          Application<MinInfResult, ProtocolBuilderNumeric> app =
              builder -> {
                NumericBuilder sIntFactory = builder.numeric();

                List<Computation<SInt>> inputN = data1.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                List<Computation<SInt>> inputD = data2.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                List<Computation<SInt>> inputInfs = data3.stream()
                    .map(BigInteger::valueOf)
                    .map(sIntFactory::known)
                    .collect(Collectors.toList());

                Computation<MinInfFrac.MinInfOutput> min = builder.seq(
                    new MinInfFrac(inputN, inputD, inputInfs));

                return builder.par((par) -> {
                  NumericBuilder open = par.numeric();
                  Computation<BigInteger> resultMinN = open.open(min.out().nm);
                  Computation<BigInteger> resultMinD = open.open(min.out().dm);
                  Computation<BigInteger> resultMinInfs = open.open(min.out().infm);
                  List<Computation<SInt>> outputArray = min.out().cs;
                  List<Computation<BigInteger>> openOutputArray = new ArrayList<>(
                      outputArray.size());
                  for (Computation<SInt> computation : outputArray) {
                    openOutputArray.add(open.open(computation));

                  }

                  return () -> new MinInfResult(
                      resultMinN.out(), resultMinD.out(), resultMinInfs.out(),
                      openOutputArray.stream().map(Computation::out).collect(Collectors.toList()));
                });
              };
          MinInfResult minInfResult = secureComputationEngine
              .runApplication(app, ResourcePoolCreator.createResourcePool(conf.sceConf));
          Assert.assertThat(minInfResult.resultList.get(0), Is.is(BigInteger.ONE));
          Assert.assertThat(minInfResult.minD, Is.is(new BigInteger("140")));
          Assert.assertThat(minInfResult.minN, Is.is(new BigInteger("20")));
          Assert.assertThat(minInfResult.minInfs, Is.is(new BigInteger("0")));
        }
      };
    }
  }
}