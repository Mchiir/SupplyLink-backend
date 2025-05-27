package com.supplylink.dtos.res;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResDTO {
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
}
