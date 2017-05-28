
package sh.kono.micro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Metadata implements Serializable
{

    @SerializedName("broker")
    @Expose
    private String broker;
    @SerializedName("registry")
    @Expose
    private String registry;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("transport")
    @Expose
    private String transport;
    private final static long serialVersionUID = -7700402510710034232L;

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

}
