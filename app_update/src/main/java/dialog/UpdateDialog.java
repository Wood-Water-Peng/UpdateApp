package dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.pj.app_update.R;

/**
 * Created by pj on 2016/3/23.
 */
public class UpdateDialog extends Dialog {
    private TextView mTv_positive;
    private TextView mTv_negative;
    private TextView mTitle;
    private TextView mContent;
    private Builder mBuilder;
    private Context context;

    UpdateDialog(Builder builder) {
        super(builder.mContext);
        context = builder.mContext;
        mBuilder = builder;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mBuilder.mContext);
        View rootView = inflater.inflate(R.layout.dialog_update, null);
        setContentView(rootView);
        //title
        mTitle = (TextView) rootView.findViewById(R.id.update_dialog_title);
        mTitle.setText(mBuilder.mTitle);
        mTitle.setTextColor(Color.BLACK);
        //content
        mContent = (TextView) rootView.findViewById(R.id.update_dialog_content);
        mContent.setText(mBuilder.mContent);
        //positive
        mTv_positive = (TextView) rootView.findViewById(R.id.update_dialog_positive);
        if (TextUtils.isEmpty(mBuilder.positiveText)) {
            mTv_positive.setText("确定");
        } else {
            mTv_positive.setText(mBuilder.positiveText);
        }
//        mTv_positive.setTextColor(mBuilder.positiveTextColor);    颜色暂时不起作用
        mTv_positive.setTextColor(Color.BLUE);
        mTv_positive.setOnClickListener(mBuilder.mPositiveButtonListener);
        //negative
        mTv_negative = (TextView) rootView.findViewById(R.id.update_dialog_negative);
        if (TextUtils.isEmpty(mBuilder.negativeText)) {
            mTv_negative.setText("取消");
        } else {
            mTv_negative.setText(mBuilder.negativeText);
        }
//        mTv_negative.setTextColor(mBuilder.negativeTextColor);
        mTv_negative.setTextColor(Color.BLACK);
        mTv_negative.setOnClickListener(mBuilder.mNegativeButtonListener);
    }

    /**
     * 改进
     * 1.添加一个设置默认参数的方法，如果用户没有设置
     * 那么，自己使用 默认的参数补全
     */
    public static class Builder {
        private String mTitle;
        private String mContent;
        private Context mContext;
        private String positiveText;
        private int positiveTextColor;
        private View.OnClickListener mPositiveButtonListener;
        private String negativeText;
        private int negativeTextColor;
        private View.OnClickListener mNegativeButtonListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            mContent = content;
            return this;
        }


        public Builder setPositiveButton(String text, int textColor, View.OnClickListener listener) {
            positiveText = text;
            positiveTextColor = textColor;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, int textColor, View.OnClickListener listener) {
            negativeText = text;
            negativeTextColor = textColor;
            mNegativeButtonListener = listener;
            return this;
        }

        public UpdateDialog create() {
            UpdateDialog updateDialog = new UpdateDialog(this);
            return updateDialog;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setCancelable(false);
        init();
    }
}
