package com.test.js.chat;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WritingTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_task);

        setTitle("업무 작성");

//        ActionBar actionBar = getSupportActionBar();//액션바에 뒤로가기 버튼을 보여주기 위해 actionBar.setDisplayHomeAsUpEnabled(true); 추가
//
//        actionBar.setDisplayHomeAsUpEnabled(true);

//        //버튼 리소스를 뷰로 전개
//        View myButtonLayout = getLayoutInflater().inflate(R.layout.button, null);
//        //액션바의 인스턴스 생성
//        ActionBar ab = getSupportActionBar();
//        //액션바의 커스텀 영역에 버튼 뷰 추가
//        ab.setCustomView(myButtonLayout);
//        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
//                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    //    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//            defaule:
//            return super.onOptionsItemSelected(item);
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.newPost) {
            //페이스북에 포스트 등록하기...
            finish();
            return true;

        }

        finish();
        return super.onOptionsItemSelected(item);
    }

    public void onBtnClicked(View view) {
        switch (view.getId()) {
            // case R.id.white:
            //    break;
            // case R.id.black:
        }
    }
}
