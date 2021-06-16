package com.example.moconmcs.Menu.HelpMenu

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.data.CommonData.QnA
import com.example.moconmcs.databinding.ActivityHelpMenuBinding

class HelpMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpMenuBinding
    private lateinit var QnAArray : ArrayList<QnA>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_menu)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_help_menu
        )
        QnAArray = ArrayList()
        QnAArray.add(QnA("Q1. 상품에 대한 결과가 너무 느리게 나와요 ㅠㅠ"
            , "A1. 데이터를 제공하는 서버가 원활하지않는 관계로 결과가 느리게 나옵니다.\n 원활한 서비스를 제공하기 위해 노력하겠습니다."))
        QnAArray.add(QnA("Q2.바코드 촬영을 했는데 인식이 안돼요",
            "A2.원활한 이용을 하지못해서 죄송할 따름입니다.\n상품의 데이터가 없어 원활한 이용이 불가능하니 바코드 검색으로 상품이 안뜰시에 품목보고번호 검색을 이용해주세요."))
        QnAArray.add(QnA("Q3.상품 검색 결과가 좀 이상한데요?"
        ,"A4. 결과가 좀 이상하신가요..? 결과창의 \'뭔가 이상하신가요?\'를 누르면 오류신고 창이나옵니다. 오류신고를 통해 저희에게 알려주세요."))
        QnAArray.add(QnA("Q4. 다이어리에 쓴 내용을 수정하고 싶어요.", "A4. 다이어리 영역을 누르면 수정가능합니다."))
        QnAArray.add(QnA("Q5. 비건 단계를 변경하고 싶어요.", "A5. 메인화면 맨 위 상단에 프로필 아이콘->프로필->기본정보->변경하기를 선택하시면 변경가능합니다."))
        QnAArray.add(QnA("Q6. 회원탈퇴는 어디서 할 수 있나요?", "A6. 메인화면 맨 위 상단에 프로필 아이콘->프로필->회원탈퇴를 선택하시면 회원탈퇴를 진행합니다."))
        QnAArray.add(QnA("Q7. 회원탈퇴 또는 비밀번호 변경이 안돼요..", "A7. 메인화면 맨 위 상단에 프로필 아이콘->로그아웃->로그인을 진행하고 비밀번호 설정 또는 회원탈퇴를 진행해주세요."))

        binding.recycler.adapter = HelpMenuAdapter(QnAArray)


        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}