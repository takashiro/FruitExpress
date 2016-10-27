package cn.weifruit.fruitexp.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.weifruit.fruitexp.MarketActivity;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    IWXAPI mApi;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        mApi = WXAPIFactory.createWXAPI(this, WxApiConfig.APPID);
        mApi.registerApp(WxApiConfig.APPID);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onDestroy(){
        mApi.unregisterApp();
        super.onDestroy();
    }

    @Override
    public void onReq(BaseReq request){
    }

    @Override
    public void onResp(BaseResp response){
        switch (response.getType()) {
            case ConstantsAPI.COMMAND_PAY_BY_WX:
                PayResp resp = (PayResp) response;
                Intent intent = new Intent(this, MarketActivity.class);
                intent.setAction("loadUrl");
                intent.putExtra("url", "module/weixin/api/callback.php?is_client=1&out_trade_no=" + resp.extData);
                startActivity(intent);
                break;
        }
        finish();
    }
}
