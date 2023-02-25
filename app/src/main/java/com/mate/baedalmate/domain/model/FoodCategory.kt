package com.mate.baedalmate.domain.model

enum class FoodCategory(val categoryId: Int, val categoryName: String) {
    KOREAN(1, "한식"),
    CHINESE(2, "중식"),
    JAPANESE(3, "일식"),
    WESTERN(4, "양식"),
    FASTFOOD(5, "패스트푸드"),
    BUNSIK(6, "분식"),
    DESSERT(7, "카페·디저트"),
    CHICKEN(8, "치킨"),
    PIZZA(9, "피자"),
    ASIA(10, "아시안"),
    PACKEDMEAL(11, "도시락")
}