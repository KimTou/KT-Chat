package cn.tojintao.controller;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.service.MsgService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cjt
 * @date 2022/10/4 15:41
 */
@RestController
@RequestMapping("/push")
public class PushController {

    @Autowired
    private MsgService msgService;

    @ApiOperation("推送私聊消息")
    @PostMapping("/pushMsg")
    public ResultInfo<String> pushMsg(@RequestBody MessageVo messageVo) {
        msgService.push(messageVo.getReceiver(), messageVo);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    @ApiOperation("推送群聊消息")
    @PostMapping("/pushGroupMsg")
    public ResultInfo<String> pushGroupMsg(@RequestBody GroupMessage groupMessage) {
        List<Integer> userIdList = groupMessage.getUserIdList();
        for (Integer receiverId : userIdList) {
            msgService.push(receiverId, groupMessage);
        }
        return ResultInfo.success(CodeEnum.SUCCESS);
    }
}
