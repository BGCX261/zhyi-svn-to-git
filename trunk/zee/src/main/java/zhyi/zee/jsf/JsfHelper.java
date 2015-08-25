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

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for JSF.
 * @author Zhao Yi
 */
public final class JsfHelper {
    private JsfHelper() {
    }

    /**
     * Logs a user in with the specified username and password.
     * @throws ServletException If login fails.
     */
    public static void login(String username, String password)
            throws ServletException {
        getRequest().login(username, password);
    }

    /**
     * Invalidates the session and logs the current user out.
     * @param facesContext
     * @throws ServletException If logout fails.
     */
    public static void logout() throws ServletException {
        HttpServletRequest req = getRequest();
        req.getSession().invalidate();
        req.logout();
    }

    /**
     * Appends a message for the specified client ID to the current
     * {@link FacesContext}.
     */
    public static void addMessage(String clientId, String message, Severity severity) {
        currentFacesContext().addMessage(clientId,
                new FacesMessage(severity, message, message));
    }

    /**
     * Appends a global message to the current {@link FacesContext}.
     */
    public static void addGlobalMessage(String message, Severity severity) {
        addMessage(null, message, severity);
    }

    private static HttpServletRequest getRequest() {
        return (HttpServletRequest) currentFacesContext().getExternalContext().getRequest();
    }

    private static FacesContext currentFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
