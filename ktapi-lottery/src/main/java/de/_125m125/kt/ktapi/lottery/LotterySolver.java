/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.lottery;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

public class LotterySolver {
    public static class OwnedItem {
        @Parsed
        public String name;
        @Parsed
        public int    amount;

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(final int amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "OwnedItem [name=" + this.name + ", amount=" + this.amount + "]";
        }
    }

    public static void main(final String[] args) throws IOException {
        final File items = new File("items.tsv");
        final File orders = new File("orders.tsv");
        final File transactions = new File("transactions.tsv");

        final List<OwnedItem> ownedItems = loadItems(items);
        System.out.println(ownedItems);
        final int total = ownedItems.stream().mapToInt(OwnedItem::getAmount).sum();
        if ((total & (total - 1)) != 0) {
            throw new RuntimeException("total number of items is not a power of 2");
        }

        final int[] hashes =
                { getHash(items, total), getHash(orders, total), getHash(transactions, total) };
        final Set<Integer> previouses = new HashSet<>();
        for (int i = 0; i < hashes.length; i++) {
            int current = hashes[i];
            final Random r = new Random(current);
            while (previouses.contains(current)
                    || "Kadcontrade".equals(findOwner(current, ownedItems))) {
                System.out.println("Skipping " + current);
                current = r.nextInt(total);
            }
            System.err.println("Winner " + (i + 1) + ": " + findOwner(current, ownedItems)
                    + " with number " + current);
            previouses.add(current);
        }
    }

    private static Object findOwner(final int current, final List<OwnedItem> ownedItems) {
        int offset = 0;
        for (int i = 0; i < ownedItems.size(); i++) {
            offset += ownedItems.get(i).getAmount();
            if (offset > current) {
                return ownedItems.get(i).getName();
            }
        }
        throw new IllegalArgumentException("number " + current + " is outside the range");
    }

    private static List<OwnedItem> loadItems(final File items) {
        final BeanListProcessor<OwnedItem> rowProcessor = new BeanListProcessor<>(OwnedItem.class);

        final TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        final TsvParser parser = new TsvParser(parserSettings);
        parser.parse(items);

        return rowProcessor.getBeans();
    }

    private static int getHash(final File file, final int total) throws IOException {
        return new BigInteger(Files.asByteSource(file).hash(Hashing.sha256()).asBytes()).intValue()
                & (total - 1);
    }
}
