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
package zhyi.zv.common;

import zhyi.zv.ui.viewer.ViewerType;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import zhyi.zv.util.StringListConverter;

/**
 * Zip Viewer's options.
 * @author Zhao Yi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Options {
    private static final String OPTIONS_FILE_PATH
            = System.getProperty("user.home") + File.separator + "zip-viewer.options";

    private static Options options = loadOptions();

    private static Options loadOptions() {
        try {
            Options unmarshalledOptions = JAXB.unmarshal(
                    new File(OPTIONS_FILE_PATH), Options.class);
            for (Map.Entry<String, String> e : unmarshalledOptions.xmlizedVtfeMap.entrySet()) {
                unmarshalledOptions.vtfeMap.put(ViewerType.valueOf(e.getKey()),
                        StringListConverter.asList(e.getValue()));
            }
            return unmarshalledOptions;
        } catch (Exception ex) {
            return new Options();
        }
    }

    @XmlJavaTypeAdapter(CharsetAdapter.class)
    private Charset charset;
    private ViewerType defaultViewerType;
    /**
     * "Viewer Type <-> File Extensions" mapping.
     */
    @XmlTransient
    private Map<ViewerType, List<String>> vtfeMap;
    /**
     * Helps marshal/unmarshal {@link #vtfeMap}.
     */
    private Map<String, String> xmlizedVtfeMap;

    private Options() {
        charset = Charset.defaultCharset();
        defaultViewerType = ViewerType.PLAIN_TEXT;
        vtfeMap = new EnumMap<>(ViewerType.class);
        vtfeMap.put(ViewerType.PLAIN_TEXT, Arrays.asList("txt"));
        vtfeMap.put(ViewerType.RICH_TEXT, Arrays.asList("rtf"));
        vtfeMap.put(ViewerType.HTML, Arrays.asList("html", "htm"));
        vtfeMap.put(ViewerType.IMAGE, Arrays.asList("bmp", "gif", "jpg", "jpeg", "png"));
        vtfeMap.put(ViewerType.HEXADECIMAL, Arrays.asList("exe", "dll", "so"));
        xmlizedVtfeMap = new HashMap<>();
    }

    public static Options getInstance() {
        return options;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public ViewerType getDefaultViewerType() {
        return defaultViewerType;
    }

    public void setDefaultViewerType(ViewerType defaultViewerType) {
        this.defaultViewerType = defaultViewerType;
    }

    public Map<ViewerType, List<String>> getVtftMap() {
        return vtfeMap;
    }

    /**
     * Persists {@code this} to XML file {@code <user.home>/zip-viewer.options}.
     */
    public void persist() {
        for (Map.Entry<ViewerType, List<String>> e : vtfeMap.entrySet()) {
            xmlizedVtfeMap.put(e.getKey().name(),
                    StringListConverter.asString(e.getValue()));
        }
        JAXB.marshal(this, new File(OPTIONS_FILE_PATH));
    }

    private static class CharsetAdapter extends XmlAdapter<String, Charset> {
        @Override
        public Charset unmarshal(String v) throws Exception {
            return Charset.forName(v);
        }

        @Override
        public String marshal(Charset v) throws Exception {
            return v.name();
        }
    }
}
