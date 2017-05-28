
package sh.kono.micro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Endpoint implements Serializable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("metadata")
    @Expose
    private EndpointMetadata metadata;
    private final static long serialVersionUID = 2480517198654014964L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public EndpointMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(EndpointMetadata metadata) {
        this.metadata = metadata;
    }

}
