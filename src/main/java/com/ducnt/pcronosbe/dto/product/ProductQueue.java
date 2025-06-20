package com.ducnt.pcronosbe.dto.product;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProductQueue {
    public static ConcurrentLinkedQueue<String> skuQueue;

    public ProductQueue() {
        if(skuQueue == null) {
            skuQueue = new ConcurrentLinkedQueue<>();
        }
    }

    public static void addSku(List<String> skuList) {
        skuQueue.addAll(skuList);
    }

    public String pollSku() {
        return skuQueue.poll();
    }

    private static ConcurrentLinkedQueue<String> getSkuQueue() {
        return skuQueue;
    }
}
