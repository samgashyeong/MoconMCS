package com.example.moconmcs.data. KyungrokApi

data class FoodData(
    val _id: String,
    val aquaProd: Int,
    val count: Int,
    val disinfectant: Int,
    val foodAdditives: Int,
    val livestock: Int,
    val materials: List<Material>,
    val microbe: Int,
    val notFound: Int,
    val nutrient: Int,
    val otherThanLivestock: Int,
    val plant: Int,
    val prodName: String,
    val prodNum: String,
    val starch: Int,
    val status: Any
)