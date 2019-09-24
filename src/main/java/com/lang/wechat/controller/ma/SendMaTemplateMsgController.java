package com.lang.wechat.controller.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUniformMessage;
import com.lang.wechat.util.R;
import com.lang.wechat.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序模板消息(代码已测试通过)
 */
@RestController
@Slf4j
public class SendMaTemplateMsgController {

    @Autowired
    private WxMaService wxMaService;

    @Value("${wechat.mp.appId}")
    private String mpAppId;

    @Value("${wechat.ma.appId}")
    private String maAppId;

    /**
     * sendTemplateMsg()发送小程序模板消息 v1
     *
     * @return
     */
    @GetMapping("/sendMaTemplateMsg")
    public R sendMaTemplateMsg() {
        try {
            WxMaTemplateMessage maMessage = WxMaTemplateMessage.builder().build();
            maMessage.setToUser("oV60Q0UVgSSkyMkxNEKzxbVZ1UAI");
            maMessage.setFormId("wx231540309868908dd8a91c601253154200");
            maMessage.setPage("/pages/index/index");
            maMessage.setTemplateId("ytVENCPmta_X29jUAmgO0pR4SVABJSeU8kSUcCwWRSI");
            List<WxMaTemplateData> dataList = new ArrayList<>();
            dataList.add(new WxMaTemplateData("keyword1", "你愁啥"));
            dataList.add(new WxMaTemplateData("keyword2", "瞅你咋的"));
            dataList.add(new WxMaTemplateData("keyword3", "再瞅一个事实"));
            dataList.add(new WxMaTemplateData("keyword4", "试试就试试"));
            maMessage.setData(dataList);
            wxMaService.getMsgService().sendTemplateMsg(maMessage);
            return R.ok("发送小程序模板消息成功！");
        } catch (Exception e) {
            log.error("发送小程序模板消息失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }


    /**
     * sendUniformMsg()发送小程序模板消息 v2
     *
     * @return
     */
    @GetMapping("/sendMaUniformMsg")
    public R sendMaUniformMsg() {
        try {
            WxMaUniformMessage uniformMessage = WxMaUniformMessage.builder().build();
            // 通过小程序的openId发送服务号的模板消息:true
            uniformMessage.setMpTemplateMsg(false);
            uniformMessage.setToUser("oV60Q0UVgSSkyMkxNEKzxbVZ1UAI");
            uniformMessage.setFormId("wx231540309868908dd8a91c601253154200");
            uniformMessage.setPage("/pages/index/index");
            uniformMessage.setTemplateId("ytVENCPmta_X29jUAmgO0pR4SVABJSeU8kSUcCwWRSI");
            List<WxMaTemplateData> dataList = new ArrayList<>();
            dataList.add(new WxMaTemplateData("keyword1", "你愁啥"));
            dataList.add(new WxMaTemplateData("keyword2", "瞅你咋的"));
            dataList.add(new WxMaTemplateData("keyword3", "再瞅一个事实"));
            dataList.add(new WxMaTemplateData("keyword4", "试试就试试"));
            uniformMessage.setData(dataList);
            wxMaService.getMsgService().sendUniformMsg(uniformMessage);
            return R.ok("发送小程序模板消息成功！");
        } catch (Exception e) {
            log.error("发送小程序模板消息失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }

    /**
     * sendKefuMsg()发送小程序客服消息 应用较少 一般都是服务号客服消息
     *
     * @return
     */
    @GetMapping("/sendKefuMsg")
    public R sendKefuMsg() {
        try {
            WxMaKefuMessage kefuMessage = WxMaKefuMessage.newTextBuilder()
                    .toUser("oHUFm1ZCrSH5fasZvPKT6z8CeJKg")
                    .content("这是小程序文字客服消息")
                    .build();
            /*WxMaKefuMessage kefuMessage = WxMaKefuMessage.newImageBuilder()
                    .toUser("oV60Q0UVgSSkyMkxNEKzxbVZ1UAI")
                    .mediaId("MEDIA_ID")
                    .build();
            WxMaKefuMessage kefuMessage = WxMaKefuMessage.newLinkBuilder()
                    .toUser("OPENID")
                    .url("url")
                    .description("description")
                    .title("title")
                    .thumbUrl("thumbUrl")
                    .build();
            WxMaKefuMessage kefuMessage = WxMaKefuMessage.newMaPageBuilder()
                    .toUser("OPENID")
                    .title("title")
                    .pagePath("pagePath")
                    .thumbMediaId("thumbMediaId")
                    .build();*/
            wxMaService.getMsgService().sendKefuMsg(kefuMessage);
            return R.ok("发送小程序文字客服消息成功！");
        } catch (Exception e) {
            log.error("发送小程序文字客服消息失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }

    /**
     * 通过小程序的openId发送服务号的模板消息
     *
     * @return
     */
    @GetMapping("/sendMpUniformMsg")
    public R sendMpUniformMsg() {
        try {
            WxMaUniformMessage uniformMessage = WxMaUniformMessage.builder().build();
            // 通过小程序的openId发送服务号的模板消息:true
            uniformMessage.setMpTemplateMsg(true);
            uniformMessage.setToUser("oV60Q0UVgSSkyMkxNEKzxbVZ1UAI");
            uniformMessage.setAppid(mpAppId);
            uniformMessage.setTemplateId("UlJ4dw5NDFWMxGSI5FIROwL8rtzExw7Sg2qla4laUnw");
            // 服务号模板消息跳转到小程序页面 2选1
            //uniformMessage.setMiniProgram(new WxMaUniformMessage.MiniProgram(maAppId, "/pages/index/index", false, false));
            // 服务号模板消息跳转到H5页面 2选1
            uniformMessage.setUrl("https://www.baidu.com");
            List<WxMaTemplateData> dataList = new ArrayList<>();
            dataList.add(new WxMaTemplateData("first", "开瞅！"));
            dataList.add(new WxMaTemplateData("keyword1", "你愁啥"));
            dataList.add(new WxMaTemplateData("keyword2", "瞅你咋的"));
            dataList.add(new WxMaTemplateData("keyword3", "再瞅一个事实"));
            dataList.add(new WxMaTemplateData("keyword4", "试试就试试"));
            dataList.add(new WxMaTemplateData("remark", "瞅累了！"));
            uniformMessage.setData(dataList);
            wxMaService.getMsgService().sendUniformMsg(uniformMessage);
            return R.ok("发送服务号模板消息成功！");
        } catch (Exception e) {
            log.error("发送服务号模板消息失败！", e);
            return R.error(StatusEnum.FAIL);
        }
    }
}
