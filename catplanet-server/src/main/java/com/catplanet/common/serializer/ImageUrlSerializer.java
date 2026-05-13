package com.catplanet.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * 图片URL序列化器：将 /uploads/xxx 相对路径转为完整的图片访问URL
 * 通过 /api/public/image/ 端点提供图片访问，确保内网穿透和真机环境都能正常加载
 */
public class ImageUrlSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeNull();
            return;
        }

        // 已经是完整URL的直接返回
        if (value.startsWith("http://") || value.startsWith("https://")) {
            gen.writeString(value);
            return;
        }

        // 提取 /uploads/ 后面的文件名
        if (value.contains("/uploads/")) {
            String filename = value.substring(value.lastIndexOf("/uploads/") + "/uploads/".length());
            String baseUrl = getBaseUrl();
            gen.writeString(baseUrl + "/api/public/image/" + filename);
        } else {
            gen.writeString(value);
        }
    }

    static String getBaseUrl() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();

                // 优先使用 Host 请求头（内网穿透场景下 natapp/cpolar 会正确转发 Host）
                String hostHeader = request.getHeader("Host");
                if (hostHeader != null && !hostHeader.isEmpty()
                        && !hostHeader.contains("localhost") && !hostHeader.contains("127.0.0.1")) {
                    // 非本地环境，强制 HTTPS（穿透服务终止 SSL 后转发 HTTP，scheme 不可靠）
                    return "https://" + hostHeader;
                }

                // 本地开发环境：使用实际 scheme + host + port
                String scheme = request.getScheme();
                String host = request.getServerName();
                int port = request.getServerPort();
                if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
                    return scheme + "://" + host;
                }
                return scheme + "://" + host + ":" + port;
            }
        } catch (Exception e) {
            // fallback
        }
        return "";
    }
}
