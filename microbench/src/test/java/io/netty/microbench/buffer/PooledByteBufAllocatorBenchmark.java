/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.microbench.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.microbench.util.AbstractMicrobenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;

@Threads(1)
public class PooledByteBufAllocatorBenchmark extends AbstractMicrobenchmark {

    private static final ByteBufAllocator pooledAllocator = PooledByteBufAllocator.DEFAULT;
    private static final int SIZE = 8;

    private final ByteBuf[] buffers = new ByteBuf[512];

    @Setup(Level.Trial)
    public void populateCache() {
        // Allocate multiple times
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = pooledAllocator.heapBuffer(SIZE);
        }

        // Release all previous allocated buffers which means
        // these should be put back in the ThreadLocal cache
        for (ByteBuf buf: buffers) {
            buf.release();
        }
    }

    @Benchmark
    public ByteBuf[] allocAndFree() {
        // Allocate again which should now be served out of the
        // ThreadLocal cache
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = pooledAllocator.heapBuffer(SIZE);
        }

        for (int i = 0; i < buffers.length; i++) {
            buffers[i].release();
        }
        return buffers;
    }
}
