package com.example.moconmcs.Main

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.*
import com.example.moconmcs.Dialog.CommDialog
import com.example.moconmcs.Dialog.CommDialogInterface
import com.example.moconmcs.Dialog.LogoutDialog
import com.example.moconmcs.Dialog.LogoutDialogInterface
import com.example.moconmcs.Main.BottomSheet.BottomSheetButtonClickListener
import com.example.moconmcs.Main.BottomSheet.BottomSheetDialog
import com.example.moconmcs.Main.FoodDiary.FoodDiaryFragment
import com.example.moconmcs.Main.FoodMap.FoodMapFragment
import com.example.moconmcs.Main.FoodMap.PlaceSearchActivity
import com.example.moconmcs.Main.SearchFood.BarCodeActivity
import com.example.moconmcs.Main.SearchFood.FoodNumInput
import com.example.moconmcs.Main.SearchFood.PrevResultActivity
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.Menu.HelpMenu.HelpMenuActivity
import com.example.moconmcs.Menu.ProfileActivity
import com.example.moconmcs.Menu.ProfileViewModel
import com.example.moconmcs.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(),
    BottomSheetButtonClickListener, CommDialogInterface, LogoutDialogInterface{
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var curUserUid : String
    private lateinit var curFragment : Fragment
    private lateinit var db : AppDatabase
    val bottomSheetDialog : BottomSheetDialog =
        BottomSheetDialog()
    private lateinit var commDialog: CommDialog
    private lateinit var logoutDialog : LogoutDialog
    private lateinit var foodResultList : ArrayList<FoodListEntity>
    private lateinit var foodNumList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        commDialog = CommDialog(this, this, "??? ??????", "?????? ?????????????????????????")
        logoutDialog = LogoutDialog(this, this, "????????????", "??????????????? ???????????????????")

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        firebaseAuth = FirebaseAuth.getInstance()

        db = AppDatabase.getInstance(this)
//        db.foodListDao().insert(FoodListEntity("123", "?????????", "?????????"))
//        db.foodListDao().insert(FoodListEntity("12345", "????????????", "??????"))
        foodResultList = (db.foodListDao().getAll() as ArrayList<FoodListEntity>?)!!
        Log.d(TAG, "onCreate: ${foodResultList}")
        curUserUid = firebaseAuth.currentUser!!.uid.toString()
        firebaseFirestore.collection("User").document(curUserUid).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    viewModel.setUserProfile(
                        it.result.data!!.getValue("name").toString()
                        , it.result.data!!.getValue("userKind").toString()
                        , firebaseAuth.currentUser!!.email.toString()
                        ,it.result.data!!.getValue("pw").toString())
                    Log.d(TAG, "onCreate: ${viewModel.userHash!!.value.toString()}")
                }
            }



        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        binding.bottmnavview.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.food_map -> {
                        setFragment(1, "?????????")
                    }
                    R.id.setting -> setFragment(2, "??????????????????")
                }
                true
            }
            selectedItemId = R.id.food_map
        }
        binding.floatingBtn.setOnClickListener {
            bottomSheetDialog.show(supportFragmentManager, "foodBottomSheet")
            foodResultList = db.foodListDao().getAll() as ArrayList<FoodListEntity>
            foodNumList = db.foodListDao().getAllFoodNum() as ArrayList<String>
            Log.d(TAG, "onCreate: ?????????$foodNumList\n???????????????$foodResultList")
        }
    }

    fun setFragment(num: Int, title: String){
        when(num){
            1->curFragment = FoodMapFragment()
            2->curFragment = FoodDiaryFragment()
        }
        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, curFragment)
                            .commit()
        binding.title.text = title
    }

    override fun layoutClick(num: Int) {
        when(num){
            1->{
                startActivity(Intent(this, BarCodeActivity::class.java))
                bottomSheetDialog.dismiss()
            }
            2->{
                startActivity(Intent(this, FoodNumInput::class.java))
                bottomSheetDialog.dismiss()
            }
            3->{
                startActivity(Intent(this, PrevResultActivity::class.java)
                    .putExtra("foodlist", foodResultList))
                bottomSheetDialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile ->{
                Log.d("asdf", "onCreate: ????????? : ${viewModel.userName!!.value}\n????????? : ${viewModel.userEmail!!.value}\n???????????? : ${viewModel.userHash!!.value}")
                startActivity(Intent(this, ProfileActivity::class.java)
                    .putExtra("userName", viewModel.userName!!.value)
                    .putExtra("userKind", viewModel.userKind!!.value)
                    .putExtra("userEmail", viewModel.userEmail!!.value)
                    .putExtra("userHash", viewModel.userHash!!.value))
            }
            R.id.setting ->{
                logoutDialog.show()
            }
            R.id.search -> {
                startActivityForResult(Intent(this, PlaceSearchActivity::class.java), 1235)
            }
            R.id.helpMenu ->{
                startActivity(Intent(this, HelpMenuActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 1235) {
            binding.bottmnavview.selectedItemId = R.id.food_map
        }
    }

    override fun onBackPressed() {
        commDialog.show()
    }

    override fun onCheckBtnClick() {
        finish()
    }

    override fun onCancleBtnClick() {
        commDialog.dismiss()
    }

    override fun onCheckLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onLogoutCancel() {
        logoutDialog.dismiss()
    }
}


