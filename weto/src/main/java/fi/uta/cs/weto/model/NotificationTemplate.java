package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.NoSuchItemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class NotificationTemplate implements Cloneable {
    private String type;
    private String template;

    public NotificationTemplate() {
        type = null;
        template = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public static NotificationTemplate getTemplateFromResource(String type) throws NoSuchItemException {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("NotificationTemplates.properties");

            if(inputStream == null) {
                throw new NoSuchItemException();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            HashMap<String, String> templates = new HashMap<>();
            while((line = reader.readLine()) != null) {
                String[] items = line.split("=");

                templates.put(items[0], items[1]);
            }
            reader.close();

            NotificationTemplate template = new NotificationTemplate();
            template.setType(type);
            template.setTemplate(templates.get(type));

            return template;
        }
        catch (NullPointerException e) {
            throw new NoSuchItemException("Failed to find template with given type " + type);
        }
        catch (IOException e) {
            throw new NoSuchItemException("Failed to fetch resource: " + e.getMessage());
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return("NotificationTemplate\n" +
                "type:" + type + "\n" +
                "template:" + template + "\n" +
                "\n");
    }
}
