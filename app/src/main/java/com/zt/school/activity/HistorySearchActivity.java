package com.zt.school.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.zt.school.R;
import com.zt.school.base.BaseActivity;
import com.zt.school.confige.Constant;
import butterknife.BindView;
/**
 * Created 九七
 * 历史搜索
 */
public class HistorySearchActivity extends BaseActivity {
    @BindView(R.id.id_search_edit)
    EditText mSerachEdit;
    private String keyword;
    @Override
    protected int setContentViewId(){
        return R.layout.activity_history_search;
    }

    @Override
    protected boolean setStatusBarFontColorIsBlack(){
        return true;
    }

    @Override
    protected void initView(){
        mSerachEdit.setFocusable(true);
        mSerachEdit.setFocusableInTouchMode(true);
        mSerachEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mSerachEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchContent();
                }
                return false;
            }
        });
    }

    private void searchContent(){
        closeSoftware();
        keyword = mSerachEdit.getText().toString().trim();
        keyword = TextUtils.isEmpty(keyword) ? null : keyword;
        Intent intent = new Intent();
        intent.putExtra(Constant.KEYWORD, keyword);
        setResult(200, intent);
        finish();
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.translate_none, R.anim.anim_close_search);
    }
}
