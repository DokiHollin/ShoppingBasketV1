package au.edu.sydney.soft3202.task1;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SampleBenchmark {
    ShoppingBasket sb;

    @Setup
    public void init(){
        sb = new ShoppingBasket();
    }

    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addItemBenchmark(Blackhole bh) {
        sb.addItem("apple", 1);
    }

    @Benchmark
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.Throughput)
    public void removeItemNameBenchmark(Blackhole bh) {
        sb.addItem("apple",1);
        sb.removeItem("apple",1);
        bh.consume(sb);
    }

    @Benchmark
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.Throughput)
    public void addNewItemBenchmark(Blackhole bh) {
        sb.addNewItem("ppoa", 10);
        bh.consume(sb);
    }

    @Benchmark
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.Throughput)
    public void removeItemsBenchmark(Blackhole bh) {
        sb.addNewItem("ppoa", 10);
        sb.removeItem("ppoa", 10);
        sb.items.remove("ppoa");
        sb.values.remove("ppoa");
        bh.consume(sb);
    }

    @Benchmark
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @BenchmarkMode(Mode.Throughput)
    public void addItemNameIncrementAndRemove(Blackhole bh) {
        sb.addNewItem("aa",1);
        for(int i = 0; i < 10; i++) {
            sb.addItem("aa", 1);
        }
        sb.items.remove("a");
        sb.values.remove("a");
        bh.consume(sb);
    }

}
