package it.polimi.ingsw.controller.responses;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private StatusCode status_code;
    private ViewContent viewContent;

    public ServerResponse(StatusCode status_code, ViewContent viewContent) {
        this.status_code = status_code;
        this.viewContent = viewContent;
    }

}
