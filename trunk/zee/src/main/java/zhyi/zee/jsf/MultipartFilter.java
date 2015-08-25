/*
 * Copyright (C) 2011 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zee.jsf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;

/**
 * This filter helps {@link FacesServlet} to recognize a {@code multipart/form-data}
 * request. To enable it, either configure it in the web application deployment
 * descriptor ({@code web.xml} or {@code web-fragment.xml}), or subclass it with
 * a {@link WebFilter} annotation.
 * @author Zhao Yi
 */
public class MultipartFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            MultipartRequest req = new MultipartRequest((HttpServletRequest) request);
            for (Part part : req.getParts()) {
                if (part.getContentType() == null) {
                    req.addParameter(part.getName(), decode(part));
                }
            }
            chain.doFilter(req, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    private String decode(Part part) throws IOException {
        try (InputStreamReader in = new InputStreamReader(
                part.getInputStream(), StandardCharsets.UTF_8)) {
            char[] buffer = new char[64];
            int nread = 0;
            StringBuilder sb = new StringBuilder();
            while ((nread = in.read(buffer)) != -1) {
                sb.append(buffer, 0, nread);
            }
            return sb.toString();
        }
    }

    /**
     * The parameter map in {@link HttpServletRequest} is immutable, so we have
     * to hack it with a wrapper.
     */
    private static class MultipartRequest extends HttpServletRequestWrapper {
        private Map<String, String[]> parameters;

        public MultipartRequest(HttpServletRequest request) {
            super(request);
            parameters = new HashMap<>();
        }

        private void addParameter(String name, String value) {
            String[] oldValues = parameters.get(name);
            if (oldValues == null) {
                parameters.put(name, new String[] {value});
            } else {
                int size = oldValues.length;
                String[] values = new String[size + 1];
                System.arraycopy(oldValues, 0, values, 0, size);
                values[size] = value;
                parameters.put(name, values);
            }
        }

        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            return values == null ? null : values[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameters;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            final Iterator<String> it = parameters.keySet().iterator();
            return new Enumeration<String>() {
                @Override
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public String nextElement() {
                    return it.next();
                }
            };
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameters.get(name);
        }
    }
}
