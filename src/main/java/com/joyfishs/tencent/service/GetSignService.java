package com.joyfishs.tencent.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GetSignService {

    public String getSign(List<String> values, String ticket) {

        if (values == null) {
            throw new NullPointerException("values is null");
        }
        // remove null
        values.removeAll(Collections.singleton(null));
        values.add(ticket);
        java.util.Collections.sort(values);
        log.info("updateFace - signList:{}", values);

        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
        }
        @SuppressWarnings("deprecation")
        String sha1 = Hashing.sha1().hashString(java.util.Objects.requireNonNull(sb.toString()), java.util.Objects.requireNonNull(java.nio.charset.StandardCharsets.UTF_8)).toString();
        return sha1.toUpperCase();
    }

}
