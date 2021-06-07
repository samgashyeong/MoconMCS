package com.example.moconmcs.data.KyungrokApi

import java.io.Serializable

data class FoodData(
    val data_res: DataRes,
    val err_msg: Any
) : Serializable