package com.edu.controller;

import com.edu.web.annotation.WrapControllerResult;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peng
 * @version 1.0
 * @Description:
 * @date 2024/10/31
 */
@RestController
@WrapControllerResult(wrapOnError = true, wrapOnSuccess = true)
public class BaseController {
}
