package zhyi.eelibz.jsf;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class FacesUtilities {

    public static void login(FacesContext facesContext, String username,
            String password) throws ServletException {
        HttpServletRequest req = (HttpServletRequest) facesContext
                .getExternalContext().getRequest();
        req.login(username, password);
    }

    public static void logout(FacesContext facesContext,
            boolean shouldInvalidateSession) throws ServletException {
        HttpServletRequest req = (HttpServletRequest) facesContext
                .getExternalContext().getRequest();
        req.logout();
        if (shouldInvalidateSession) {
            req.getSession().invalidate();
        }
    }

}
