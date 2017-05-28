
package sh.kono.micro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ZeroconfTxt implements Serializable
{

    @SerializedName("Service")
    @Expose
    private String service;
    @SerializedName("Version")
    @Expose
    private String version;
    @SerializedName("Endpoints")
    @Expose
    private List<Endpoint> endpoints = null;
    @SerializedName("EndpointMetadata")
    @Expose
    private Metadata metadata;
    private final static long serialVersionUID = -8035363797192732342L;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

}
