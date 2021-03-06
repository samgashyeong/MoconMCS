package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Dialog.CommDialog
import com.example.moconmcs.Dialog.CommDialogInterface
import com.example.moconmcs.Dialog.ErrorDialog
import com.example.moconmcs.Dialog.ErrorDialogInterface
import com.example.moconmcs.Main.AppDatabase
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodNum
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodResult
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.FoodData
import com.example.moconmcs.databinding.ActivityFoodResultLodingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FoodResultLoading : AppCompatActivity(), ErrorDialogInterface, CommDialogInterface{
    private lateinit var binding: ActivityFoodResultLodingBinding
    private lateinit var viewModel: FoodViewModel
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var errorDialog: ErrorDialog
    private lateinit var commDialog: CommDialog
    private lateinit var data : FoodData
    private lateinit var auth : FirebaseAuth
    private lateinit var Fdb : FirebaseFirestore
    private lateinit var db : AppDatabase
    private var ActivityOn = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_result_loding
        ) //?????????????????????
        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)
        errorDialog = ErrorDialog(this, this, "????????? ??????", "??????????????? ?????? ????????? ???????????? ???????????? ??????????????????.\n????????? ????????? ???????????????????")
        commDialog = CommDialog(this, this, "?????? ??????", "????????? ?????????????????????????")
        db = AppDatabase.getInstance(this)

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()
        val barcode: String?
        val foodNum: String?
        if(intent.hasExtra("barcodenum")){
            barcode = intent.getStringExtra("barcodenum").toString()
            getFoodNum(barcode)
        }
        else{
            foodNum = intent.getStringExtra("foodNum").toString()
            getFoodResult(foodNum)
        }
        binding.backBtn.setOnClickListener {
            commDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun getFoodNum(barCode:String){ //????????? ??????
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL_BARCODE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodNum::class.java)

        Log.d("asdf", "api: ${api}")

        GlobalScope.launch(Dispatchers.IO){
            val execution = api.isExecutionCode(barCode).awaitResponse()
            Log.d("response", "response :${execution}")
            if(execution.isSuccessful){
                val isExecution = execution.body()

                withContext(Dispatchers.Main){

                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                    if(isExecution?.C005?.total_count.equals("0")){
                        Log.d(TAG, "getFoodNum: ???????????? ??????????????????.")
                        startActivity(Intent(this@FoodResultLoading, FoodResultActivity::class.java)
                            .putExtra("barCodeFail", "????????? ????????? ?????????????????????. ????????????????????? ??????????????????."))
                        finish()
                    }
                    else{ //??????????
                        binding.tvResult.text = "???????????? ?????????????????????. ?????? ??????????????????!"
                        val foodName = isExecution!!.C005.row[0].PRDLST_NM
                        val foodNumber = isExecution.C005.row[0].PRDLST_REPORT_NO
                        Log.d(TAG, "getFoodNum: ${isExecution}")
                        getFoodResult(foodNumber)
                    }
                }
            }
        }
    }

    fun getFoodResult(FoodNum : String){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL_KYUNGROK_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodResult::class.java)

        GlobalScope.launch(Dispatchers.IO){
            val execution = api.isExecutionCode(FoodNum).awaitResponse()
            Log.d("response", "response :${execution}")
            if(execution.isSuccessful){
                if(ActivityOn){
                    val isExecution = execution.body()

                    withContext(Dispatchers.Main){

//                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                        if(isExecution?.err_msg == "Cannot find product with following prodNum : $FoodNum"){
                            Log.d(TAG, "getFoodResult: ????????? ????????????.")
                            if(commDialog.isShowing){
                                commDialog.dismiss()
                            }
                            startActivity(Intent(this@FoodResultLoading, FoodResultActivity::class.java)
                                .putExtra("ResultFail", "?????? ????????????."))
                            finish()
                        }
                        else{
                            Log.d(TAG, "getFoodResult: ${isExecution?.data_res?._id}" +
                                    ", ${isExecution?.data_res?.prodName}" +
                                    ", ${isExecution?.data_res?.materials}")
                            if(commDialog.isShowing){
                                commDialog.dismiss()
                            }
                            if(isExecution?.data_res?.status == "Error"){
                                data = isExecution
                                errorDialog.show()
                            }
                            else{
                                Log.d(TAG, "getFoodResult: ?????? ??? ????????????!")
                                binding.tvResult.text = "?????? ??? ????????????!"
                                if (isExecution != null) {
                                    Log.d(TAG, "getFoodResult: ?????????")
                                    checkCanEat(isExecution)
                                }
//                                startActivity(Intent(this@FoodResultLoading, FoodResultActivity::class.java)
//                                    .putExtra("FoodResult", isExecution))
//                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCanEat(excution : FoodData) {
        var userKind = ""
        auth = FirebaseAuth.getInstance()
        Fdb = FirebaseFirestore.getInstance()
        Fdb.collection("User").document(auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    userKind = it.result.data!!.getValue("userKind").toString()
//                    if(excution!!.data_res.aquaProd > 0 && excution!!.data_res.livestock > 0){
//                        checkIsSaveData(excution, excution.data_res.prodNum, "bad_meatAndFish", excution.data_res.materials)
//                        resultIntent("0", "bad_meatAndFish", userKind, excution)
//                    }
//                    else if(excution?.data_res.livestock > 0){
//                        Log.d(TAG, "IsEat: ????????? ????????????")
//                        checkIsSaveData(excution, excution.data_res.prodNum, "bad_meat", excution.data_res.materials)
//                        resultIntent("0", "bad_meat", userKind, excution)
//                    }
//                    else if(excution?.data_res.aquaProd >0){
//                        checkIsSaveData(excution, excution.data_res.prodNum, "bad_fish", excution.data_res.materials)
//                        resultIntent("0", "bad_fish", userKind, excution)
//                    }
//                    else{
//                        checkIsBadResult(userKind, excution)
//                    }
                    val badFoodList = ArrayList<String>()
                    if(excution.data_res.aquaProd > 0) badFoodList.add("fish")
                    if(excution.data_res.livestock > 0) badFoodList.add("meat")
                    if(excution.data_res.otherThanLivestock > 0) badFoodList.add("egg")
                    if(excution.data_res.dairy > 0) badFoodList.add("milk")
                    if(excution.data_res.prodNum !in db.foodListDao().getAllFoodNum()) {
                        db.foodListDao().insert(
                            FoodListEntity(
                                excution.data_res.prodNum, excution.data_res.prodName,
                                badFoodList.joinToString("/"), excution.data_res.materials
                            )
                        )
                    }
                    resultIntent(isCanEat(userKind, badFoodList.joinToString("/")),
                        getCauseCantEat(userKind, badFoodList.joinToString("/")), userKind, excution)
                }
            }
    }

    companion object {
        const val serViceKey = "6a957af97bed49989b74"
        const val BASE_URL_BARCODE = "https://openapi.foodsafetykorea.go.kr/api/$serViceKey/C005/json/"
        const val BASE_URL_KYUNGROK_API = "https://vcheck-api.herokuapp.com/api/"
        fun isCanEat(userKind: String, badFoodList: String): Boolean {
            return getCauseCantEat(userKind, badFoodList) == "safe"
        }
        fun getCauseCantEat(userKind: String, badFoodList: String): String {
            val list = badFoodList.split("/")
            if(list.contains("meat") && list.contains("fish")) return "meat|fish"
            if(list.contains("fish")) return "fish"
            if(list.contains("meat")) return "meat"
            when(userKind) {
                "??????" -> {
                    if(list.contains("egg") && list.contains("milk")) return "egg|milk"
                    if(list.contains("egg")) return "egg"
                    if(list.contains("milk")) return "milk"
                }
                "??????" -> {
                    if(list.contains("egg")) return "egg"
                }
                "??????" -> {
                    if(list.contains("milk")) return "milk"
                }
            }
            return "safe"
        }
    }

//    private fun checkIsSaveData(
//        excution: FoodData,
//        prodNum: String,
//        s: String,
//        material: List<Material>
//    ) {
//        if(prodNum !in db.foodListDao().getAllFoodNum()){
//            db.foodListDao().insert(FoodListEntity(excution.data_res.prodNum, excution.data_res.prodName, s,
//                material
//            ))
//        }
//    }

//    private fun checkIsBadResult(userKind: String, excution: FoodData) {
//        Log.d(TAG, "checkIsBadResult: ???????????????")
//        when(userKind){
//            "??????"->{
//                if(excution.data_res.eggs>0){
//                    checkIsSaveData(excution, excution.data_res.prodNum, "bad_egg",excution.data_res.materials)
//                    resultIntent("0", "bad_egg", userKind, excution)
//                }
//                else if(excution.data_res.dairy >0){
//                    checkIsSaveData(excution, excution.data_res.prodNum, "bad_dairy",excution.data_res.materials)
//                    resultIntent("0", "bad_dairy", userKind, excution)
//                }
//                else if(excution.data_res.dairy >0 && excution.data_res.eggs > 0){
//                    checkIsSaveData(excution, excution.data_res.prodNum, "bad_dairyAndEggs",excution.data_res.materials)
//                    resultIntent("0", "bad_dairyAndEggs", userKind, excution)
//                }
//                else{
//                    checkIsSaveData(excution, excution.data_res.prodNum, "good_vegan", excution.data_res.materials)
//                    resultIntent("1", "eat", userKind, excution)
//                }
//            }
//            "??????"->{
//                //?????????, ???, ?????? ?????? ???????????? ??????
//                if(excution.data_res.eggs>0){
//                    checkIsSaveData(excution, excution.data_res.prodNum, "bad_egg", excution.data_res.materials)
//                    resultIntent("0", "bad_egg", userKind, excution)
//                }
//                else{
//                    checkIsSaveData(excution, excution.data_res.prodNum, "good_locto", excution.data_res.materials)
//                    resultIntent("1", "eat", userKind, excution)
//                }
//            }
//            "??????"->{
//                if(excution.data_res.dairy >0){
//                    checkIsSaveData(excution, excution.data_res.prodNum, "bad_dairy", excution.data_res.materials)
//                    resultIntent("0", "bad_dairy", userKind, excution)
//                }
//                else{
//                    checkIsSaveData(excution, excution.data_res.prodNum, "good_ovo", excution.data_res.materials)
//                    resultIntent("1", "eat", userKind, excution)
//                }
//            }
//            "????????????"->{
//                checkIsSaveData(excution, excution.data_res.prodNum, "good_loctoovo", excution.data_res.materials)
//                resultIntent("1", "eat", userKind, excution)
//            }
//        }
//    }

    private fun resultIntent(canEat: Boolean, cause: String, userKind: String, excution: FoodData) {
        startActivity(Intent(this, FoodResultActivity::class.java)
            .putExtra("FoodResult", excution)
            .putExtra("canEat", canEat)
            .putExtra("cause", cause)
            .putExtra("userKind", userKind))
        finish()
    }


    //    fun checkInternet(){
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    } ?????? ??????

    override fun onCheckBtnClick() {
        commDialog.dismiss()
        ActivityOn = false
        finish()
    }

    override fun onCancleBtnClick() {
        commDialog.dismiss()
    }

    override fun onCheckBtnClick1() {
        checkCanEat(data)
        errorDialog.dismiss()
    }

    override fun onCancleBtnClick1() {
        finish()
    }

    override fun onBackPressed() {
        commDialog.show()
    }
}