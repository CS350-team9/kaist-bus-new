package kr.ac.kaist.kyotong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by yearnning on 15. 1. 27..
 */
public class SmsManager {

    private Context mContext = null;
    private String mNumber = null;

    public SmsManager(Context context, String number) {
        this.mNumber = number;
        this.mContext = context;
    }

    public void showDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder.title("급한오류 신고하기")
                .content("제가 혼자 개발하다 보니, 잘못된 정보를 보여드리고 있을 수도 있습니다. 잘못된 정보로 사용자분들께서 버스를 제때 못타실까 매우 걱정하고 있습니다. 정확한 정보를 위해 최선을 다하고 있지만 혹시라도 잘못된 정보가 있다면, 제게 문자 주십시오. 늦어도 하루 이내에는 답장드리고, 오류는 빠른 시일 내에 고쳐드리겠습니다. 사용자분들의 통학에 방해가 되지 않았으면 하는 마음에 전화번호를 공개해 두는 것이니, 부탁이온데 전화는 삼가주십시오. 감사합니다.")
                .positiveText("문자보내기")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mNumber, null)));
                    }
                })
                .negativeText("닫기");
        builder.show();
    }

}
