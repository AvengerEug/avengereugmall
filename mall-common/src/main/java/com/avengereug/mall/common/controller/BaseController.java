package com.avengereug.mall.common.controller;

import com.avengereug.mall.common.Enum.BusinessCodeEnum;
import com.avengereug.mall.common.exception.RRException;
import com.avengereug.mall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BaseController {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R exceptionHandler(Exception e) {
        log.error("发生异常", e);

        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
            Map<String, Object> map = new HashMap<>();
            fieldErrors.stream().forEach(item -> map.put(item.getField(), item.getDefaultMessage()));

            return R.error(BusinessCodeEnum.VALID_EXCEPTION.getCode(),
                    BusinessCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
        } else if (e instanceof RRException) {
            RRException rrException = ((RRException) e);
            return R.error(rrException.getCode(), rrException.getMsg());
        }

        return R.error(BusinessCodeEnum.UNKNOWN_EXCEPTION.getCode(), BusinessCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }

}
