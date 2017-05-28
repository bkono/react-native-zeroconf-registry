
package sh.kono.micro;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EndpointMetadata implements Serializable
{

    @SerializedName("stream")
    @Expose
    private String stream;
    private final static long serialVersionUID = 4319090979787278423L;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

}
