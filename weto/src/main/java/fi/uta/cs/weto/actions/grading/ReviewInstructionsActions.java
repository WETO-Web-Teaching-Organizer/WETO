package fi.uta.cs.weto.actions.grading;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.InstructionBean;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public class ReviewInstructionsActions
{
  /* --- COMMON ITEMS FOR ALL ACTIONS --- */

 /* XML Tags */
  public static final String INSTRUCTIONTAG = "instruction";
  public static final String NAMETAG = "name";
  public static final String INSTRUCTIONTEXTTAG = "instructiontext";
  public static final String MAXPOINTSTAG = "maxpoints";
  public static final String MINPOINTSTAG = "minpoints";

  /* Comparator that sorts Tags based on rank */
  static final Comparator<Tag> RANK_ORDER = new Comparator<Tag>()
  {
    @Override
    public int compare(Tag tag1, Tag tag2)
    {
      return tag1.getRank() - tag2.getRank();
    }

  };

  /* Static function that gets all review instructions related to a single task. */
  private static ArrayList<Tag> getInstructionsTags(Connection conn,
          Integer taskId)
  {
    ArrayList<Tag> reviewInstructions = new ArrayList<>();
    try
    {
      reviewInstructions = Tag.selectByTaggedIdAndType(conn, taskId,
              TagType.REVIEW_INSTRUCTIONS.getValue());
      Collections.sort(reviewInstructions, RANK_ORDER);
    }
    catch(SQLException e)
    {
    }
    return reviewInstructions;
  }

  public static InstructionBean instructionTagToBean(Tag instruction)
          throws WetoActionException
  {
    // Try to convert XML to a JDOM object
    String rawXml = instruction.getText();
    SAXBuilder builder = new SAXBuilder();

    Document jdomDoc;
    try
    {
      jdomDoc = builder.build(new StringReader(rawXml));
    }
    catch(Exception e)
    {
      throw new WetoActionException();
    }

    // Build InstructionBean from the <instruction> -element
    Element instructionElem = jdomDoc.getRootElement();
    Element nameElem = instructionElem.getChild(NAMETAG);
    Element textElem = instructionElem.getChild(INSTRUCTIONTEXTTAG);
    Element minPointsElem = instructionElem.getChild(MINPOINTSTAG);
    Element maxPointsElem = instructionElem.getChild(MAXPOINTSTAG);

    InstructionBean ibean = new InstructionBean();
    String name = nameElem.getText();
    if(name != null)
    {
      ibean.setName(name);
    }
    else
    {
      ibean.setName("null");
    }

    String text = textElem.getText();
    if(text != null)
    {
      ibean.setText(text);
    }
    else
    {
      ibean.setText("null");
    }
    float minPoints = Float.valueOf(minPointsElem.getText());
    float maxPoints = Float.valueOf(maxPointsElem.getText());
    ibean.setMinPoints(minPoints);
    ibean.setMaxPoints(maxPoints);
    // Rank and id are directly from Tag
    ibean.setId(instruction.getId());
    ibean.setRank(instruction.getRank());
    return ibean;
  }

  /* Gets instructions as easy-to-read InstructionBeans */
  public static ArrayList<InstructionBean> getInstructionsBeans(Connection conn,
          Integer taskId) throws WetoActionException
  {
    ArrayList<InstructionBean> instructionsBeans = new ArrayList<>();
    ArrayList<Tag> instructionsTags = getInstructionsTags(conn, taskId);
    for(Tag instruction : instructionsTags)
    {
      instructionsBeans.add(instructionTagToBean(instruction));
    }
    return instructionsBeans;
  }

  /* Builds a XML string based on parameters. */
  public static String buildXML(String questionName, String questionText,
          float minPoints, float maxPoints)
  {
    // Create the root element: <question></question>
    Element instructionElement = new Element(INSTRUCTIONTAG);

    // Create and append children elements
    Element instructionNameElem = new Element(NAMETAG);
    Element instructionTextElem = new Element(INSTRUCTIONTEXTTAG);
    Element minPointsElement = new Element(MINPOINTSTAG);
    Element maxPointsElement = new Element(MAXPOINTSTAG);
    instructionNameElem.addContent(questionName);
    instructionTextElem.addContent(questionText);
    minPointsElement.addContent(String.valueOf(minPoints));
    maxPointsElement.addContent(String.valueOf(maxPoints));

    instructionElement.addContent(instructionNameElem);
    instructionElement.addContent(instructionTextElem);
    instructionElement.addContent(minPointsElement);
    instructionElement.addContent(maxPointsElement);

    // Setup XML outputter
    XMLOutputter xmlOutput = new XMLOutputter();
    xmlOutput.setFormat(Format.getRawFormat());
    xmlOutput.getFormat().setLineSeparator(LineSeparator.NL);

    // Return result
    return xmlOutput.outputString(new Document(instructionElement));
  }

  /* PageBean represents the information from the webpage. Also extends WetoTeacherAction to import utility functions. */
  private static abstract class PageBean extends WetoTeacherAction
  {
    public PageBean(int reqOwnerViewBits, int reqOwnerUpdateBits,
            int reqOwnerCreateBits, int reqOwnerDeleteBits)
    {
      super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits,
              reqOwnerDeleteBits);
    }

    private String newInstructionName;
    private String newInstructionText;
    private float newMinPoints;
    private float newMaxPoints;
    // Represents the order of tags by their ids.
    private int[] order;
    // The id of a question to be modified or deleted
    private int instructionId;

    public String getNewInstructionName()
    {
      return newInstructionName;
    }

    public void setNewInstructionName(String newInstructionName)
    {
      this.newInstructionName = newInstructionName;
    }

    public String getNewInstructionText()
    {
      return newInstructionText;
    }

    public void setNewInstructionText(String newInstructionText)
    {
      this.newInstructionText = newInstructionText;
    }

    public int getInstructionId()
    {
      return instructionId;
    }

    public void setInstructionId(int instructionId)
    {
      this.instructionId = instructionId;
    }

    public int[] getOrder()
    {
      return order;
    }

    public void setOrder(int[] order)
    {
      this.order = order;
    }

    public float getNewMinPoints()
    {
      return newMinPoints;
    }

    public void setNewMinPoints(String newMinPoints)
    {
      this.newMinPoints = Float.parseFloat(newMinPoints);
    }

    public float getNewMaxPoints()
    {
      return newMaxPoints;
    }

    public void setNewMaxPoints(String newMaxPoints)
    {
      this.newMaxPoints = Float.parseFloat(newMaxPoints);
    }

  }

  public static class CreateInstruction extends PageBean
  {
    public CreateInstruction()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), Tab.GRADING.getBit(),
              Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      /* Build XML based on parameters gotten from request */
      String text = getNewInstructionText();
      String name = getNewInstructionName();
      float minPoints = getNewMinPoints();
      float maxPoints = getNewMaxPoints();
      String xmlString = buildXML(name, text, minPoints, maxPoints);

      /* Construct a new tag */
      Tag reviewInstruction = new Tag();
      reviewInstruction.setText(xmlString);
      reviewInstruction.setAuthorId(getCourseUserId());
      reviewInstruction.setTaggedId(getTaskId());
      reviewInstruction.setType(TagType.REVIEW_INSTRUCTIONS.getValue());

      int nextRank = 0;
      ArrayList<Tag> instructions = getInstructionsTags(getCourseConnection(),
              getTaskId());
      if(instructions != null)
      {
        if(instructions.size() >= 1)
        {
          Tag lastTag = instructions.get(instructions.size() - 1);
          nextRank = lastTag.getRank() + 1;
        }
      }
      reviewInstruction.setRank(nextRank);

      /* Finally insert */
      reviewInstruction.insert(getCourseConnection());
      /* Set task's "hasReviewInstructions" flag to true */
      Task task = getTask();
      task.setHasReviewInstructions(true);
      task.update(getCourseConnection());
      return SUCCESS;
    }

  }

  public static class DeleteInstruction extends PageBean
  {
    public DeleteInstruction()
    {
      super(Tab.MAIN.getBit(), 0, 0, Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      int deleteId = getInstructionId();
      Tag instruction = Tag.select1ById(getCourseConnection(), deleteId);
      if(!TagType.REVIEW_INSTRUCTIONS.getValue().equals(instruction.getType()))
      {
        throw new WetoActionException();
      }
      validateCourseSubtaskId(instruction.getTaggedId());
      instruction.delete(getCourseConnection());
      /* If task hasn't got review instructions anymore, set its "hasReviewInstructions" to false. */
      Task task = getTask();
      if(getInstructionsTags(getCourseConnection(), task.getId()).isEmpty())
      {
        task.setHasReviewInstructions(false);
        task.update(getCourseConnection());
      }
      return SUCCESS;
    }

  }

  public static class UpdateOrder extends WetoTeacherAction
  {
    private String order;

    public UpdateOrder()
    {
      super(Tab.MAIN.getBit(), 0, Tab.GRADING.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Integer taskId = getTaskId();
      Connection courseConn = getCourseConnection();
      String[] orderStrings = order.split(",");
      HashMap<Integer, Integer> rankMap = new HashMap<>();
      int i = 0;
      for(String orderStr : orderStrings)
      {
        Integer instructionTagId = Integer.valueOf(orderStr);
        rankMap.put(instructionTagId, i++);
      }
      ArrayList<Tag> reviewInstructions = Tag
              .selectByTaggedIdAndType(courseConn, taskId,
                      TagType.REVIEW_INSTRUCTIONS.getValue());
      for(Tag instructionTag : reviewInstructions)
      {
        instructionTag.setRank(rankMap.get(instructionTag.getId()));
        instructionTag.update(courseConn);
      }
      return SUCCESS;
    }

    public void setOrder(String order)
    {
      this.order = order;
    }

  }

  public static class UpdateInstruction extends PageBean
  {
    public UpdateInstruction()
    {
      super(Tab.MAIN.getBit(), 0, Tab.GRADING.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Tag reviewInstruction = Tag.select1ById(getCourseConnection(),
              getInstructionId());
      if(!TagType.REVIEW_INSTRUCTIONS.getValue().equals(reviewInstruction
              .getType()))
      {
        throw new WetoActionException();
      }
      validateCourseSubtaskId(reviewInstruction.getTaggedId());
      // Construct a new text for Tag
      String text = getNewInstructionText();
      String name = getNewInstructionName();
      float minPoints = getNewMinPoints();
      float maxPoints = getNewMaxPoints();
      String newXmlString = buildXML(name, text, minPoints, maxPoints);
      // Update the Tag
      reviewInstruction.setText(newXmlString);
      reviewInstruction.update(getCourseConnection());
      return SUCCESS;
    }

  }

  public static class ViewReviewInstructions extends WetoTeacherAction
  {
    private ArrayList<InstructionBean> reviewInstructions;

    public ViewReviewInstructions()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws WetoActionException
    {
      int taskId = getTaskId();
      Connection conn = getCourseConnection();
      reviewInstructions = getInstructionsBeans(conn, taskId);
      return SUCCESS;
    }

    public ArrayList<InstructionBean> getReviewInstructions()
    {
      return reviewInstructions;
    }

  }
}
