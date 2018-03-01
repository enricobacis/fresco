package dk.alexandra.fresco.fixedpoint.basic;

import dk.alexandra.fresco.decimal.RealNumeric;
import dk.alexandra.fresco.decimal.SReal;
import dk.alexandra.fresco.decimal.fixed.FixedNumeric;
import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;

public class BasicFixedPointTests {

  public static class TestInput<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);

            return fixed.numeric().open(input);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(value, output));
        }
      };
    }
  }

  public static class TestUseSInt<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      BigInteger value = BigInteger.valueOf(11);

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SInt> sint = producer.numeric().input(value, 1);
            DRes<SReal> input = fixed.numeric().fromSInt(sint);

            return fixed.numeric().open(input);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(new BigDecimal(value), output));
        }
      };
    }
  }

  public static class TestOpenToParty<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);

            return fixed.numeric().open(input, 1);
          };
          BigDecimal output = runApplication(app);

          if (conf.getMyId() == 1) {
            Assert.assertTrue(TestUtils.isEqual(value, output));
          } else {
            Assert.assertNull(output);
          }

        }
      };
    }
  }

  public static class TestKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().known(value);

            return fixed.numeric().open(input);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(value, output));
        }
      };
    }
  }

  public static class TestAddKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      BigDecimal value2 = BigDecimal.valueOf(20.1);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);
            DRes<SReal> sum = fixed.numeric().add(value2, input);

            return fixed.numeric().open(sum);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(value.add(value2), output));
        }
      };
    }
  }

  public static class TestSubtractSecret<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      BigDecimal value2 = BigDecimal.valueOf(20.1);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);
            DRes<SReal> input2 = fixed.numeric().input(value2, 1);
            DRes<SReal> diff = fixed.numeric().sub(input, input2);

            return fixed.numeric().open(diff);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(value.subtract(value2), output));
        }
      };
    }
  }

  public static class TestSubKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      BigDecimal value2 = BigDecimal.valueOf(20.1);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);
            DRes<SReal> diff = fixed.numeric().sub(input, value2);

            return fixed.numeric().open(diff);
          };
          BigDecimal output = runApplication(app);

          Assert.assertTrue(TestUtils.isEqual(value.subtract(value2), output));
        }
      };
    }
  }

  public static class TestMultKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      BigDecimal value2 = BigDecimal.valueOf(0.2);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input2 = fixed.numeric().input(value2, 1);
            DRes<SReal> product = fixed.numeric().mult(value, input2);

            return fixed.numeric().open(product);
          };
          BigDecimal output = runApplication(app);
          Assert.assertTrue(TestUtils.isEqual(value.multiply(value2), output));
        }
      };
    }
  }

  public static class TestDivisionKnownDivisor<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      BigDecimal value = BigDecimal.valueOf(10.00100);
      BigDecimal value2 = BigDecimal.valueOf(0.2);
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<BigDecimal, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            DRes<SReal> input = fixed.numeric().input(value, 1);
            DRes<SReal> product = fixed.numeric().div(input, value2);

            return fixed.numeric().open(product);
          };
          BigDecimal output = runApplication(app);
          Assert.assertTrue(TestUtils.isEqual(value.divide(value2), output));
        }
      };
    }
  }

  public static class TestMult<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      List<BigDecimal> openInputs =
          Stream.of(1.223, 222.23, 5.59703, 0.004, 5.90, 6.0, 0.0007, 0.1298, 9.99)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      List<BigDecimal> openInputs2 =
          Stream.of(1.000, 1.0000, 0.22211, 100.1, 11.0, .07, 0.0005, 10.0012, 999.0101)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<List<BigDecimal>, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            List<DRes<SReal>> closed1 =
                openInputs.stream().map(fixed.numeric()::known).collect(Collectors.toList());
            List<DRes<SReal>> closed2 =
                openInputs2.stream().map(fixed.numeric()::known).collect(Collectors.toList());

            List<DRes<SReal>> result = new ArrayList<>();
            for (DRes<SReal> inputX : closed1) {
              result.add(fixed.numeric().mult(inputX, closed2.get(closed1.indexOf(inputX))));
            }

            List<DRes<BigDecimal>> opened =
                result.stream().map(fixed.numeric()::open).collect(Collectors.toList());
            return () -> opened.stream().map(DRes::out).collect(Collectors.toList());
          };
          List<BigDecimal> output = runApplication(app);

          for (BigDecimal openOutput : output) {
            int idx = output.indexOf(openOutput);

            BigDecimal a = openInputs.get(idx);
            BigDecimal b = openInputs2.get(idx);
            Assert.assertTrue(TestUtils.isEqual(a.multiply(b), openOutput));
          }
        }
      };
    }
  }

  public static class TestAdd<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      List<BigDecimal> openInputs =
          Stream.of(1.223, 222.23, 5.59703, 0.004, 5.90, 6.0, 0.0007, 0.121998, 9.999999)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      List<BigDecimal> openInputs2 =
          Stream.of(1.000, 1.0000, 0.22211, 100.1, 11.0, .07, 0.0005, 10.00112, 999991.0)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<List<BigDecimal>, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            List<DRes<SReal>> closed1 =
                openInputs.stream().map(fixed.numeric()::known).collect(Collectors.toList());
            List<DRes<SReal>> closed2 =
                openInputs2.stream().map(fixed.numeric()::known).collect(Collectors.toList());

            List<DRes<SReal>> result = new ArrayList<>();
            for (DRes<SReal> inputX : closed1) {
              result.add(fixed.numeric().add(inputX, closed2.get(closed1.indexOf(inputX))));
            }

            List<DRes<BigDecimal>> opened =
                result.stream().map(fixed.numeric()::open).collect(Collectors.toList());
            return () -> opened.stream().map(DRes::out).collect(Collectors.toList());
          };
          List<BigDecimal> output = runApplication(app);

          for (BigDecimal openOutput : output) {
            int idx = output.indexOf(openOutput);

            BigDecimal a = openInputs.get(idx);
            BigDecimal b = openInputs2.get(idx);
            Assert.assertTrue(TestUtils.isEqual(a.add(b), openOutput));
          }
        }
      };
    }
  }

  public static class TestDiv<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      List<BigDecimal> openInputs =
          Stream.of(1.223, 222.23, 5.59703, 0.004, 5.90, 6.0, 0.00007, 0.12198, 9.99999)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      List<BigDecimal> openInputs2 =
          Stream.of(1.000, 1.0000, 0.22211, 100.1, 11.0, 0.5, 0.0005, 10.00112, 999991.0)
              .map(BigDecimal::valueOf).collect(Collectors.toList());

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<List<BigDecimal>, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            List<DRes<SReal>> closed1 =
                openInputs.stream().map(fixed.numeric()::known).collect(Collectors.toList());
            List<DRes<SReal>> closed2 =
                openInputs2.stream().map(fixed.numeric()::known).collect(Collectors.toList());

            List<DRes<SReal>> result = new ArrayList<>();
            for (DRes<SReal> inputX : closed1) {
              result.add(fixed.numeric().div(inputX, closed2.get(closed1.indexOf(inputX))));
            }

            List<DRes<BigDecimal>> opened =
                result.stream().map(fixed.numeric()::open).collect(Collectors.toList());
            return () -> opened.stream().map(DRes::out).collect(Collectors.toList());
          };
          List<BigDecimal> output = runApplication(app);

          for (BigDecimal openOutput : output) {
            int idx = output.indexOf(openOutput);

            BigDecimal a = openInputs.get(idx);
            BigDecimal b = openInputs2.get(idx);
            Assert.assertTrue(TestUtils.isEqual(a.divide(b, RoundingMode.DOWN), openOutput));
          }
        }
      };
    }
  }

  public static class TestLeq<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      List<BigDecimal> openInputs =
          Stream.of(1.223, 222.23, 5.59703, 0.2, 1000.1, 6.9, 0.00007, 0.12198)
              .map(BigDecimal::valueOf).collect(Collectors.toList());
      List<BigDecimal> openInputs2 =
          Stream.of(1.000, 222.24, 5.000000001, 0.19, 1001.0, 7.0, 0.000070, 10.12199)
              .map(BigDecimal::valueOf).collect(Collectors.toList());

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        @Override
        public void test() throws Exception {
          Application<List<BigInteger>, ProtocolBuilderNumeric> app = producer -> {
            RealNumeric fixed = new FixedNumeric(producer);

            List<DRes<SReal>> closed1 =
                openInputs.stream().map(fixed.numeric()::known).collect(Collectors.toList());
            List<DRes<SReal>> closed2 =
                openInputs2.stream().map(fixed.numeric()::known).collect(Collectors.toList());

            List<DRes<SInt>> result = new ArrayList<>();
            for (DRes<SReal> inputX : closed1) {
              result.add(fixed.numeric().leq(inputX, closed2.get(closed1.indexOf(inputX))));
            }

            List<DRes<BigInteger>> opened =
                result.stream().map(producer.numeric()::open).collect(Collectors.toList());
            return () -> opened.stream().map(DRes::out).collect(Collectors.toList());
          };
          List<BigInteger> output = runApplication(app);

          for (BigInteger openOutput : output) {
            int idx = output.indexOf(openOutput);

            BigDecimal a = openInputs.get(idx);
            BigDecimal b = openInputs2.get(idx);
            int expected = (a.compareTo(b) != 1) ? 1 : 0;

            Assert.assertTrue(openOutput.intValue() == expected);
          }
        }
      };
    }
  }
}