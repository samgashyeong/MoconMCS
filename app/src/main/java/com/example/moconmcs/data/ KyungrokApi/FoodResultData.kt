package com.example.moconmcs.data. KyungrokApi

data class FoodResultData(
    val aquaProd: Int,
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