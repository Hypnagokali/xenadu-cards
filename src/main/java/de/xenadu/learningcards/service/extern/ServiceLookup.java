package de.xenadu.learningcards.service.extern;


import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@ApplicationScoped
public class ServiceLookup {

    private final ServiceDiscovery serviceDiscovery;

    public ServiceLookup(@RestClient ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public ServiceInfo getInfoOf(String serviceName) {

        final String userServiceXml = serviceDiscovery.getUserService(serviceName);

        try {
            JAXBContext context = JAXBContext.newInstance(ServiceInfo.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            return (ServiceInfo) unmarshaller.unmarshal(new StringReader(userServiceXml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("No Service found with name = " + serviceName);

    }

}
