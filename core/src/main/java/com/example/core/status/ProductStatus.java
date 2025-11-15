package com.example.core.status;

public enum ProductStatus {

    /**
     * Товар активен и доступен для продажи/заказа
     * - Виден в каталоге
     * - Можно добавлять в корзину
     * - Учитывается в остатках
     */
    ACTIVE,

    RESERVED,

    RESERVATION_FAILED

}
