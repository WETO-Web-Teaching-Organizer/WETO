package fi.uta.cs.weto.actions.front;

import com.google.gson.Gson;
import fi.uta.cs.weto.model.WetoMasterAction;

public class GetRealUser extends WetoMasterAction {

  private String jsonString;

  @Override
  public String action() throws Exception {
    Gson gson = new Gson();
    jsonString = gson.toJson(getNavigator().getRealUser());
    return SUCCESS;
  }

  public String getJsonString() {
    return jsonString;
  }

  public void setJsonString(String jsonString) {
    this.jsonString = jsonString;
  }
}

