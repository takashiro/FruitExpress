package cn.weifruit.fruitexp.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.weifruit.fruitexp.MarketActivity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

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
        SendAuth.Resp resp = (SendAuth.Resp) response;
        if (resp.state.equals("login")) {
            Intent intent = new Intent(this, MarketActivity.class);
            intent.setAction("weixin_login");
            intent.putExtra("code", resp.code);
            startActivity(intent);
        }
    }
}
