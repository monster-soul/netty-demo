package com.edu.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author pengXiangJun
 * @date 2024年07月31日
 * @program
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCommandInput {


    /**
     * 十六进制命令行
     */
    private String command;

    /**
     *  客户端id
     */
    private String clineAddress;
}
