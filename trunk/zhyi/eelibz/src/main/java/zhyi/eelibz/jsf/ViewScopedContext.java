package zhyi.eelibz.jsf;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.faces.context.FacesContext;

public class ViewScopedContext implements Context {

    @Override
    public Class<? extends Annotation> getScope() {
        return ViewScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;
        String beanName = bean.getName();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap(true);
        if (viewMap.containsKey(beanName)) {
            return (T) viewMap.get(beanName);
        } else if (creationalContext != null) {
            T t = bean.create(creationalContext);
            viewMap.put(beanName, t);
            return t;
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
