package it.polimi.ingsw.controller.responses;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private StatusCode status_code;
    private ViewContent viewContent;
}
