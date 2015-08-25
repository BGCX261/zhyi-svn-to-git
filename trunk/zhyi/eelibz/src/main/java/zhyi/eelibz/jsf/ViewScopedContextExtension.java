package zhyi.eelibz.jsf;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class ViewScopedContextExtension implements Extension {

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery) {
        afterBeanDiscovery.addContext(new ViewScopedContext());
    }

}
