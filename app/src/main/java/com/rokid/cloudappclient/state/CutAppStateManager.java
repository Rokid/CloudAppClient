package com.rokid.cloudappclient.state;

import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class CutAppStateManager extends BaseAppStateManager {

    public static CutAppStateManager getInstance() {
        return AppStateManagerHolder.instance;
    }

    private static class AppStateManagerHolder {
        private static final CutAppStateManager instance = new CutAppStateManager();
    }

    @Override
    public synchronized void onAppPaused() {
        super.onAppPaused();
        Logger.d(" cut : pause tts and finishActivity");
        VoiceAction.getInstance().stopPlay();
        MediaAction.getInstance().stopPlay();
        HttpClientWrapper.getInstance().close();
        finishActivity();
    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_CUT;
    }
}
