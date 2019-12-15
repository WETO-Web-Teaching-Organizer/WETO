package fi.uta.cs.weto.actions.grading;

import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoCourseAction;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class GetNavigationTree extends WetoCourseAction
{
  private InputStream documentStream;

  public GetNavigationTree()
  {
    super(Tab.MAIN.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    documentStream = new ByteArrayInputStream(getNavigationTree().getBytes(
            "UTF-8"));
    return SUCCESS;
  }

  public InputStream getDocumentStream()
  {
    return documentStream;
  }

}
