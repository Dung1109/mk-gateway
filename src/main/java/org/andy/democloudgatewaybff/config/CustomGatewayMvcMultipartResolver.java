package org.andy.democloudgatewaybff.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.*;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CustomGatewayMvcMultipartResolver extends org.springframework.web.multipart.support.StandardServletMultipartResolver {
    private boolean resolveLazily = false;

    private boolean strictServletCompliance = false;


    /**
     * Set whether to resolve the multipart request lazily at the time of
     * file or parameter access.
     * <p>Default is "false", resolving the multipart elements immediately, throwing
     * corresponding exceptions at the time of the {@link #resolveMultipart} call.
     * Switch this to "true" for lazy multipart parsing, throwing parse exceptions
     * once the application attempts to obtain multipart files or parameters.
     *
     * @since 3.2.9
     */
    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    /**
     * Specify whether this resolver should strictly comply with the Servlet
     * specification, only kicking in for "multipart/form-data" requests.
     * <p>Default is "false", trying to process any request with a "multipart/"
     * content type as far as the underlying Servlet container supports it
     * (which works on e.g. Tomcat but not on Jetty). For consistent portability
     * and in particular for consistent custom handling of non-form multipart
     * request types outside of Spring's {@link MultipartResolver} mechanism,
     * switch this flag to "true": Only "multipart/form-data" requests will be
     * wrapped with a {@link MultipartHttpServletRequest} then; other kinds of
     * requests will be left as-is, allowing for custom processing in user code.
     *
     * @since 5.3.9
     */
    public void setStrictServletCompliance(boolean strictServletCompliance) {
        this.strictServletCompliance = strictServletCompliance;
    }


    @Override
    public boolean isMultipart(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(),
                (this.strictServletCompliance ? MediaType.MULTIPART_FORM_DATA_VALUE : "multipart/"));
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new CustomGatewayMultipartHttpServletRequest(request, this.resolveLazily);
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (!(request instanceof AbstractMultipartHttpServletRequest abstractMultipartHttpServletRequest) ||
                abstractMultipartHttpServletRequest.isResolved()) {
            // To be on the safe side: explicitly delete the parts,
            // but only actual file parts (for Resin compatibility)
            try {
                for (Part part : request.getParts()) {
                    if (request.getFile(part.getName()) != null) {
                        part.delete();
                    }
                }
            } catch (Throwable ex) {
                LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
            }
        }
    }


    public static class CustomGatewayMultipartHttpServletRequest extends org.springframework.web.multipart.support.StandardMultipartHttpServletRequest {


        public CustomGatewayMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
            super(request);
        }

        public CustomGatewayMultipartHttpServletRequest(HttpServletRequest request, boolean lazyParsing) throws MultipartException {
            super(request, lazyParsing);
        }

        // add the public getter to have access to this Map in the MultipartRestClientProxyExchange class
        public MultiValueMap<String, MultipartFile> getMultipartFiles() {
            return super.getMultipartFiles();
        }
    }

}
