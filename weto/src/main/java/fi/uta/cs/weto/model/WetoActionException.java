package fi.uta.cs.weto.model;

import com.opensymphony.xwork2.Action;

public class WetoActionException extends Exception
{
  private final String result;

  public WetoActionException(String m, String result)
  {
    super(m);
    this.result = result;
  }

  public WetoActionException(String m)
  {
    super(m);
    result = Action.ERROR;
  }

  public WetoActionException()
  {
    result = Action.ERROR;
  }

  public String getResult()
  {
    return result;
  }

}
