package fi.uta.cs.weto.model;

public abstract class WetoTeacherAction extends WetoCourseAction
{
  public WetoTeacherAction(int reqOwnerViewBits, int reqOwnerUpdateBits,
          int reqOwnerCreateBits, int reqOwnerDeleteBits)
  {
    super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits,
            reqOwnerDeleteBits);
  }

  @Override
  void doPrepare() throws Exception
  {
    super.doPrepare();
    if(!ClusterType.TEACHERS.getValue().equals(getClusterType()))
    {
      throw new WetoActionException(getText("accessDenied.teacher"),
              ACCESS_DENIED);
    }
  }

}
