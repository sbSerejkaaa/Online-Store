package com.example.core.status;

public enum ProductCategorySlug {
    PHONE("phones", "📱"),
    LAPTOP("laptops", "💻"),
    HEADPHONES("headphones", "🎧"),
    ROBOT("robots", "🤖"),
    WATCH("watches", "⌚"),
    ACCESSORY("accessories", "🎮");

    private final String slug;
    private final String icon;

    ProductCategorySlug(String slug, String icon) {
        this.slug = slug;
        this.icon = icon;
    }

    public String getSlug() { return slug; }
    public String getIcon() { return icon; }
}