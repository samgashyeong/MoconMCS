package com.example.moconmcs.data.KyungrokApi

import java.io.Serializable

data class DataRes(
    val _id: String,
    val aquaProd: Int,
    val count: Int,
    val disinfectant: Int,
    val etc: Int,
    val foodAdditives: Int,
    val livestock: Int,
    val materials: ArrayList<Material>,
    val microbe: Int,
    val notFound: Int,
    val nutrient: Int,
    val otherThanLivestock: Int,
    val plant: Int,
    val prodName: String,
    val prodNum: String,
    val starch: Int,
    val status: String
) : Serializable