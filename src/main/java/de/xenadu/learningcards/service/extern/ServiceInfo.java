package de.xenadu.learningcards.service.extern;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "application")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInfo {

    @XmlElement(name = "instance")
    private ServiceInstance instance;

    public ServiceInstance getInstance() {
        return instance;
    }

    @XmlRootElement(name= "instance")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ServiceInstance {
        @XmlElement(name = "hostName")
        private String hostName;

        @XmlElement(name = "ipAddr")
        private String ipAddr;

        @XmlElement(name="port")
        private String port;

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(String ipAddr) {
            this.ipAddr = ipAddr;
        }

        public int getPort() {
            return Integer.parseInt(port);
        }

        public void setPort(String port) {
            this.port = port;
        }
    }
}
