package com.edu.controller;

import com.edu.model.input.SendCommandInput;
import com.edu.service.CommandService;
import com.edu.web.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengXiangJun
 * @date 2024年07月31日
 * @program
 * @description
 */

@RestController
@RequestMapping("command")
public class CommandController extends BaseController{


    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * 发送命令
     *
     * @return
     */
    @PostMapping("sendCommand")
    public Boolean sendCommand(@RequestBody SendCommandInput input) {
        return this.commandService.sendCommand(input);
    }

}
