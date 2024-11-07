package com.edu.web.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DuHai
 * @since 2022/6/24 10:02
 **/

public final class ExceptionUtils {

    /**
     * 获取制度异常的内部异常列表
     *
     * @param throwable
     * @return
     */
    public static List<Throwable> getCauseThrowableList(Throwable throwable) {
        List<Throwable> causeThrows = new ArrayList<>(3);
        causeThrows.add(throwable);
        Throwable causeThrowable = throwable.getCause();
        while (causeThrowable != null && !causeThrows.contains(causeThrowable)) {
            causeThrows.add(causeThrowable);
            causeThrowable = causeThrowable.getCause();
        }
        return causeThrows;
    }
}
