package com.rokid.cloudappclient.state;

import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class SceneAppStateManager extends BaseAppStateManager {

    public static SceneAppStateManager getInstance() {
        return AppStateManagerHolder.instance;
    }

    private static class AppStateManagerHolder {
        private static final SceneAppStateManager instance = new SceneAppStateManager();
    }

    @Override
    public synchronized void onAppPaused() {
        super.onAppPaused();
        MediaAction.getInstance().pausePlay();
        VoiceAction.getInstance().pausePlay();
    }

    @Override
    public synchronized void onAppResume() {
        super.onAppResume();
        Logger.d("scene  onAppResume mediaType: " + currentMediaState + " voiceType : " + currentVoiceState + " userMediaControlType: " + userMediaControlType + " userVoiceControlType: " + userVoiceControlType);

        //应用onResume的时候要考虑到用户上次操作是否是暂停
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED && !(userMediaControlType == USER_MEDIA_CONTROL_TYPE.MEDIA_PAUSE)) {
            Logger.d("scene: onAppResume resume play audio");
            MediaAction.getInstance().resumePlay();
        }

        if (currentVoiceState == VOICE_STATE.VOICE_PAUSED && !(userVoiceControlType == USER_VOICE_CONTROL_TYPE.VOICE_PAUSE)) {
            Logger.d("scene onAppResume play voice");
            VoiceAction.getInstance().resumePlay();
        }
    }

    @Override
    public void onAppDestory() {
        MediaAction.getInstance().stopPlay();
        VoiceAction.getInstance().stopPlay();
        HttpClientWrapper.getInstance().close();
        super.onAppDestory();
    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_SCENE;
    }
}
