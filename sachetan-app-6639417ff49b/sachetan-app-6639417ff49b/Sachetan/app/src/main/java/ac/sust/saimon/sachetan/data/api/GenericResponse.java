package ac.sust.saimon.sachetan.data.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Saimon on 11-Apr-16.
 */
@JsonIgnoreProperties
public class GenericResponse {
    private  int status;
    private String message;
    private String error;
    private String error_description;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}