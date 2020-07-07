package com.lang.wechat.controller.mp;

import com.lang.wechat.util.R;
import com.lang.wechat.util.ResultType;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class SendMpTemplateMsgController {

    @Autowired
    private WxMpService wxMpService;

    @Value("${wx.ma.appId}")
    private String appId;

    /**
     * 发送服务号模板消息
     *
     * @return
     */
    @GetMapping("/sendMpTemplateMsg")
    public R sendMpTemplateMsg() {
        try {
            WxMpTemplateMessage mpTemplateMessage = WxMpTemplateMessage.builder()
                    .toUser("otal2t22M_fB96D7ftqAekegdZK0")
                    .templateId("N78dyQecpJOeQu2x1f39MTlBl8Ca3W-QOiX5iX9XB9M")
                    //.miniProgram(new WxMpTemplateMessage.MiniProgram(appId, "/pages/index/index", true)) //跳小程序 2选1
                    .url("http://www.baidu.com") //跳H5 2选1
                    .build();

            List<WxMpTemplateData> dataList = new ArrayList<>();
            dataList.add(new WxMpTemplateData("first", "开瞅！"));
            dataList.add(new WxMpTemplateData("keyword1", "你愁啥"));
            dataList.add(new WxMpTemplateData("keyword2", "瞅你咋的"));
            dataList.add(new WxMpTemplateData("keyword3", "再瞅一个事实"));
            dataList.add(new WxMpTemplateData("keyword4", "试试就试试"));
            dataList.add(new WxMpTemplateData("remark", "瞅累了！"));
            mpTemplateMessage.setData(dataList);
            wxMpService.getTemplateMsgService().sendTemplateMsg(mpTemplateMessage);
            return R.ok("发送服务号模板消息成功！");
        } catch (Exception e) {
            log.error("发送服务号模板消息失败！", e);
            return R.error(ResultType.FAIL);
        }
    }

    /**
     * 发送服务号客服消息
     *
     * @return
     */
    @GetMapping("/sendMpKefuMsg")
    public R sendMpKefuMsg() {
        try {
            // 文字客服消息
            WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().toUser("oHUFm1ZCrSH5fasZvPKT6z8CeJKg").content("文字客服消息").build();

            // 图片客服消息
            //WxMpKefuMessage kefuMessage = WxMpKefuMessage.IMAGE().toUser("OPENID").mediaId("MEDIA_ID").build();

            // 声音客服消息
            //WxMpKefuMessage kefuMessage = WxMpKefuMessage.VOICE().toUser("OPENID").mediaId("MEDIA_ID").build();

            // 视频客服消息
            /*WxMpKefuMessage kefuMessage = WxMpKefuMessage.VIDEO().toUser("OPENID").title("TITLE").mediaId("MEDIA_ID")
                    .thumbMediaId("MEDIA_ID").description("DESCRIPTION").build();*/

            // 音乐客服消息
            /*WxMpKefuMessage kefuMessage = WxMpKefuMessage.MUSIC().toUser("OPENID").title("TITLE").thumbMediaId("MEDIA_ID")
                    .description("DESCRIPTION").musicUrl("MUSIC_URL").hqMusicUrl("HQ_MUSIC_URL").build();*/

            // 新闻客服消息
            /*WxMpKefuMessage.WxArticle article1 = new WxMpKefuMessage.WxArticle();
            article1.setUrl("URL");
            article1.setPicUrl("PIC_URL");
            article1.setDescription("Is Really A Happy Day");
            article1.setTitle("Happy Day");

            WxMpKefuMessage.WxArticle article2 = new WxMpKefuMessage.WxArticle();
            article2.setUrl("URL");
            article2.setPicUrl("PIC_URL");
            article2.setDescription("Is Really A Happy Day");
            article2.setTitle("Happy Day");

            WxMpKefuMessage kefuMessage = WxMpKefuMessage.NEWS().toUser("OPENID")
                    .addArticle(article1).addArticle(article2).build();*/

            // 跳转到小程序客服消息
            /*WxMpKefuMessage kefuMessage = WxMpKefuMessage.MINIPROGRAMPAGE()
                    .toUser("OPENID").title("title").appId("appid")
                    .pagePath("pagepath").thumbMediaId("thumb_media_id").build();*/

            // 消息菜单客服消息
            /*WxMpKefuMessage kefuMessage = WxMpKefuMessage.MSGMENU().toUser("OPENID")
                    .addMenus(new WxMpKefuMessage.MsgMenu("101", "msgmenu1"),
                            new WxMpKefuMessage.MsgMenu("102", "msgmenu2"))
                    .headContent("head_content").tailContent("tail_content").build();*/


            wxMpService.getKefuService().sendKefuMessage(kefuMessage);

            return R.ok("发送服务号客服消息成功！");
        } catch (Exception e) {
            log.error("发送服务号客服消息失败！", e);
            return R.error(ResultType.FAIL);
        }
    }

}
