package com.example.moconmcs.data.KyungrokApi

import java.io.Serializable

data class Material(
    public val MLSFC_NM: String,
    public val RPRSNT_NML: String,
    public val matName: String
) : Serializable