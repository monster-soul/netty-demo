package com.edu.service;


import com.edu.model.input.SendCommandInput;

public interface CommandService {

    /**
     * 十六进制命令行
     *
     * @param input
     * @return
     */
    Boolean sendCommand(SendCommandInput input);
}
