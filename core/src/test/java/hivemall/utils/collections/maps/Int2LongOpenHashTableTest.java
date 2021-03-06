/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hivemall.utils.collections.maps;

import hivemall.utils.lang.ObjectUtils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class Int2LongOpenHashTableTest {

    @Test
    public void testSize() {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(16384);
        map.put(1, 3L);
        Assert.assertEquals(3L, map.get(1));
        map.put(1, 5L);
        Assert.assertEquals(5L, map.get(1));
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testDefaultReturnValue() {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(16384);
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(-1L, map.get(1));
        long ret = Long.MIN_VALUE;
        map.defaultReturnValue(ret);
        Assert.assertEquals(ret, map.get(1));
    }

    @Test
    public void testPutAndGet() {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(16384);
        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            Assert.assertEquals(-1L, map.put(i, i));
        }
        Assert.assertEquals(numEntries, map.size());
        for (int i = 0; i < numEntries; i++) {
            long v = map.get(i);
            Assert.assertEquals(i, v);
        }
    }

    @Test
    public void testPutRemoveGet() {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(16384);
        map.defaultReturnValue(0L);
        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            Assert.assertEquals(0L, map.put(i, i));
            Assert.assertEquals(0L, map.put(-i, -i));
            if (i % 2 == 0) {
                Assert.assertEquals(i, map.remove(i));
            } else {
                Assert.assertEquals(i, map.put(i, i));
            }
        }
        Assert.assertEquals(numEntries + (numEntries / 2) - 1, map.size());
        for (int i = 0; i < numEntries; i++) {
            if (i % 2 == 0) {
                Assert.assertFalse(map.containsKey(i));
            } else {
                Assert.assertEquals(i, map.get(i));
            }
            Assert.assertEquals(-i, map.get(-i));
        }
    }

    @Test
    public void testSerde() throws IOException, ClassNotFoundException {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(16384);
        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            Assert.assertEquals(-1L, map.put(i, i));
        }

        byte[] b = ObjectUtils.toCompressedBytes(map);
        map = new Int2LongOpenHashTable(16384);
        ObjectUtils.readCompressedObject(b, map);

        Assert.assertEquals(numEntries, map.size());
        for (int i = 0; i < numEntries; i++) {
            long v = map.get(i);
            Assert.assertEquals(i, v);
        }
    }

    @Test
    public void testIterator() {
        Int2LongOpenHashTable map = new Int2LongOpenHashTable(1000);
        Int2LongOpenHashTable.MapIterator itor = map.entries();
        Assert.assertFalse(itor.hasNext());

        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            Assert.assertEquals(-1L, map.put(i, i));
        }
        Assert.assertEquals(numEntries, map.size());

        itor = map.entries();
        Assert.assertTrue(itor.hasNext());
        while (itor.hasNext()) {
            Assert.assertFalse(itor.next() == -1);
            int k = itor.getKey();
            long v = itor.getValue();
            Assert.assertEquals(k, v);
        }
        Assert.assertEquals(-1, itor.next());
    }
}
