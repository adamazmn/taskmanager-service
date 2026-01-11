package com.example.taskmanager.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ResUtil {

    public Map<String, Object> createSuccessRes(String status, String msg, Object data) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", status);
        res.put("msg", msg);
        res.put("data", data);

        return res;
    }

    public Map<String, Object> createErrorRes(String status, String msg) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", status);
        res.put("msg", msg);

        return res;
    }

}
